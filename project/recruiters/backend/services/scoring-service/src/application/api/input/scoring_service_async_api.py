import json
import logging
from typing import Any

from src.application.api.input.dto.application_analyzed_event_dto import ApplicationAnalysedEventDTO
from src.domain.model.application_analysis import ApplicationAnalysis, ResumeAnalysis, Skill
from src.domain.services.scoring_service import ScoringService

logger = logging.getLogger(__name__)

class ScoringServiceAsyncAPI():
    def __init__(self, scoring_service: ScoringService):
        self.scoring_service = scoring_service

    def handle_application_analyzed_event(self, message: Any):
        if hasattr(message, 'value'):
            application_analysed_event_dto = ApplicationAnalysedEventDTO(**json.loads(message.value()))
        else:
            application_analysed_event_dto = ApplicationAnalysedEventDTO(**message)

        application_analysed = self.__map_to_business_model(application_analysed_event_dto)
        logger.info(f"Processing application {application_analysed.application_id}")

        self.scoring_service.score(application_analysis=application_analysed)

    def __map_to_business_model(self, application_analysed_event_dto: ApplicationAnalysedEventDTO) -> ApplicationAnalysis:
        analysis = ResumeAnalysis(
            summary=application_analysed_event_dto.analysis["summary"],
            strengths=application_analysed_event_dto.analysis["strengths"],
            concerns=application_analysed_event_dto.analysis["concerns"],
            hard_skills=[Skill(skill=skill["skill"], level=skill["level"]) for skill in
                         application_analysed_event_dto.analysis["hardSkills"]],
            soft_skills=[Skill(skill=skill["skill"], level=skill["level"]) for skill in
                         application_analysed_event_dto.analysis["softSkills"]],
            key_responsibilities=application_analysed_event_dto.analysis["keyResponsibilities"],
            interview_questions=application_analysed_event_dto.analysis["interviewQuestions"],
            total_years_xperience=application_analysed_event_dto.analysis["totalYearsExperience"],
            average_permanency=application_analysed_event_dto.analysis["averagePermanency"],
            tags=application_analysed_event_dto.analysis["tags"]
        )

        return ApplicationAnalysis(
            application_id=application_analysed_event_dto.applicationId,
            candidate_id=application_analysed_event_dto.candidateId,
            position_id=application_analysed_event_dto.positionId,
            analysis=analysis
        )



