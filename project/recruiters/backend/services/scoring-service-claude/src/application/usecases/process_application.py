# src/application/usecases/process_application.py
import logging
import time
from dataclasses import asdict
import json
import asyncio
from typing import Dict, Any
from concurrent.futures import ThreadPoolExecutor

from src.application.adapters.messaging.kafka_producer import KafkaProducer
from src.domain.model.events import ApplicationAnalysedEvent, ApplicationScoredEvent
from src.domain.model.application import Application
from src.domain.services.scoring_service import ScoringService
from src.application.ports.repository import PositionRepository
from src.application.ports.event_producer import EventProducer
from src.infrastructure.observability.opentelemetry import tracer

logger = logging.getLogger(__name__)
thread_pool = ThreadPoolExecutor(max_workers=10)


class ProcessApplicationUseCase:
    """
    Caso de uso para procesar una aplicación analizada y calcular su puntuación
    """

    def __init__(
            self,
            position_repository: PositionRepository,
            event_producer: KafkaProducer,
            scoring_service: ScoringService
    ):
        self.position_repository = position_repository
        self.event_producer = event_producer
        self.scoring_service = scoring_service

    @tracer.start_as_current_span("process_application")
    def execute(self, event_data: Dict[str, Any]):
        """
        Procesa un evento ApplicationAnalysedEvent, calcula la puntuación y emite un
        evento ApplicationScoredEvent con los resultados

        Args:
            event_data: Datos del evento ApplicationAnalysedEvent
        """
        start_time = time.time()

        try:
            # Convertir a objeto de dominio
            input_event = ApplicationAnalysedEvent(**event_data)

            logger.info(f"Processing application {input_event.applicationId} for position {input_event.positionId}")

            # Crear objeto Application
            application = Application(
                id=input_event.applicationId,
                position_id=input_event.positionId,
                candidate_description=input_event.candidateDescription,
                requirements=input_event.requirements,
                experiences=input_event.experiences
            )

            # Obtener datos de la posición de la base de datos
            # Como get_position_by_id no es asíncrono en SQLAlchemy, lo ejecutamos en un thread separado
            with tracer.start_as_current_span("get_position"):
                position = self.position_repository.get_position_by_id(input_event.positionId)

            if not position:
                logger.error(f"Position {input_event.positionId} not found")
                return

            # Calcular puntuación (esto es sincrónico)
            with tracer.start_as_current_span("calculate_scores"):
                final_score, desc_score, requirement_score, task_score, scoring_time = (
                    self.scoring_service.calculate_scores(position, application)
                )

            # Tiempo total incluyendo acceso a base de datos
            total_time = round(time.time() - start_time, 3)

            # Crear evento de resultado
            output_event = ApplicationScoredEvent(
                applicationId=application.id,
                score=final_score,
                descScore=desc_score,
                requirementScore=requirement_score,
                tasksScore=task_score,
                timeSpent=total_time
            )

            # Publicar resultado
            with tracer.start_as_current_span("publish_scored_event"):
                self.event_producer.send(
                    topic="application-scored-events",
                    event=asdict(output_event),
                    key=application.id
                )

            logger.info(f"Application {application.id} scored and event published. "
                        f"Score: {final_score}, Time: {total_time}s")

        except Exception as e:
            logger.exception(f"Error processing application: {str(e)}")