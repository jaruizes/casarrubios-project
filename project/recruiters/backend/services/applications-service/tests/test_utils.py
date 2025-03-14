import json
import logging
import threading
import time
from typing import Dict, Any, Optional
from uuid import UUID

from confluent_kafka import Producer
from sqlalchemy.orm import Session
from sqlalchemy.sql import text

from src.domain.models.application_scoring import ApplicationScoring
from src.adapters.db.repositories import ApplicationRepository
from src.domain.services.application_service import ApplicationService
from src.api.input.message.application_scored_event_handler import ApplicationScoredEventHandler

logger = logging.getLogger(__name__)

class TestEventProcessor:
    """Clase auxiliar para procesar eventos de Kafka en tests"""
    
    def __init__(self, db_session: Session):
        self.db_session = db_session
        self.application_repository = ApplicationRepository(lambda: db_session)
        self.application_service = ApplicationService(repository=self.application_repository)
        self.event_handler = ApplicationScoredEventHandler(applications_service=self.application_service)
        
    def process_event(self, message_value: bytes):
        """Procesa un evento directamente sin pasar por Kafka"""
        class MockMessage:
            def __init__(self, value_bytes):
                self._value = value_bytes
                
            def value(self):
                return self._value
                
            def topic(self):
                return "test-topic"
        
        mock_message = MockMessage(message_value)
        self.event_handler.handle_application_scored_event(mock_message)
        
    def check_application_processed(self, application_id: UUID) -> bool:
        """Verifica si una aplicación ha sido procesada"""
        result = self.db_session.execute(
            text('SELECT * FROM recruiters.resume_analysis WHERE application_id = :application_id'),
            {"application_id": application_id}
        )
        rows = result.fetchall()
        return len(rows) > 0


def publish_and_process_event(producer: Producer, topic: str, event_data: Dict[str, Any], 
                             processor: TestEventProcessor) -> bool:
    """Publica un evento en Kafka y lo procesa directamente"""
    try:
        # Convertir el evento a bytes
        event_bytes = json.dumps(event_data).encode("utf-8")
        
        # Publicar en Kafka (para mantener la compatibilidad con el test original)
        producer.produce(topic, key=event_data.get("applicationId"), value=event_bytes)
        producer.flush()
        logger.info(f"Event published to topic {topic}")
        
        # Procesar el evento directamente sin esperar a que Kafka lo consuma
        processor.process_event(event_bytes)
        logger.info("Event processed directly")
        
        return True
    except Exception as e:
        logger.exception(f"Error publishing or processing event: {str(e)}")
        return False
