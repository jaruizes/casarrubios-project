import json
import logging
from typing import Dict, Any

from src.domain.model.application import Application
from src.domain.model.events import ApplicationAnalysedEvent
from src.domain.services.scoring_service import ScoringService
from src.infrastructure.kafka.kafka_consumer import KafkaConsumer

logger = logging.getLogger(__name__)

class ScoringServiceAsyncAPI():
    def __init__(self, scoring_service: ScoringService):
        self.scoring_service = scoring_service

    def process_application(self, message: Any):
        if hasattr(message, 'value'):
            application_analysed_event = ApplicationAnalysedEvent(**json.loads(message.value()))
        else:
            application_analysed_event = ApplicationAnalysedEvent(**message)

        applicationAnalysed = Application(
            id=application_analysed_event.applicationId,
            position_id=application_analysed_event.positionId,
            candidate_description=application_analysed_event.candidateDescription,
            requirements=application_analysed_event.requirements,
            experiences=application_analysed_event.experiences)

        logger.info(f"Processing application {applicationAnalysed.id}")
        self.scoring_service.compute_cv_score(applicationAnalysed)



