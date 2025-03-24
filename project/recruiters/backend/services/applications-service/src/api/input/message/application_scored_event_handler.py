import json
import logging
from typing import Any
from uuid import UUID

from src.api.input.message.dto.application_scored_event_dto import ApplicationScoredEventDTO
from src.infrastructure.kafka.kafka_consumer import KafkaConsumer
from src.domain.services.application_service import ApplicationService
from src.domain.models.application_scoring import Scoring, ApplicationScoring, ResumeAnalysis, Skill

logger = logging.getLogger(__name__)

class ApplicationScoredEventHandler():
    def __init__(self, applications_service: ApplicationService):
        self.applications_service = applications_service

    def handle_application_scored_event(self, message: Any):
        logger.info("Received application scored event....")
        try:
            application_analysed_event_dto = json.loads(message.value())
            application_id = application_analysed_event_dto["applicationId"]

            logger.info(f"Processing application {application_id}")
            application_scoring = self.__map_to_application_scoring(application_analysed_event_dto)
            self.applications_service.save_application_scoring(application_scoring)

            logger.info(f"Application {application_id} has been processed")
        except Exception as e:
            logger.exception(f"Error processing application scored event: {str(e)}")

    def __map_to_application_scoring(self, application_scored_event: ApplicationScoredEventDTO) -> ApplicationScoring:
        scoring_dto = application_scored_event["scoring"]
        analysis_dto = application_scored_event["analysis"]

        scoring = Scoring(
            application_id=application_scored_event["applicationId"],
            score=scoring_dto["score"],
            desc_score=scoring_dto["descScore"],
            requirement_score=scoring_dto["requirementScore"],
            tasks_score=scoring_dto["tasksScore"],
            time_spent=scoring_dto["timeSpent"],
            explanation=scoring_dto["explanation"]
        )

        analysis = ResumeAnalysis(
            summary=analysis_dto["summary"],
            strengths=analysis_dto["strengths"],
            concerns=analysis_dto["concerns"],
            hard_skills=[Skill(skill=skill["skill"], level=skill["level"]) for skill in analysis_dto["hardSkills"]],
            soft_skills=[Skill(skill=skill["skill"], level=skill["level"]) for skill in analysis_dto["softSkills"]],
            key_responsibilities=analysis_dto["keyResponsibilities"],
            interview_questions=analysis_dto["interviewQuestions"],
            total_years_xperience=analysis_dto["totalYearsExperience"],
            average_permanency=analysis_dto["averagePermanency"],
            tags=analysis_dto["tags"]
        )

        return ApplicationScoring(
            application_id=UUID(application_scored_event["applicationId"]),
            position_id=application_scored_event["positionId"],
            analysis=analysis,
            scoring=scoring
        )



