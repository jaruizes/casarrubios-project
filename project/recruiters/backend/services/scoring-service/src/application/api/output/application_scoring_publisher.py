import logging
from dataclasses import asdict

from src.application.api.input.dto.application_analyzed_event_dto import ResumeAnalysisDTO, SkillDTO
from src.application.api.output.dto.application_scored_event_dto import ApplicationScoredEventDTO, \
    ScoringDTO
from src.domain.model.application_analysis import ResumeAnalysis
from src.domain.model.application_scoring import ApplicationScoring, Scoring

logger = logging.getLogger(__name__)

class ApplicationScoringPublisher():
    def __init__(self, kafka_producer, output_topic: str):
        self.kafka_producer = kafka_producer
        self.output_topic = output_topic

    def publish_application_scored_event(self, application_scoring: ApplicationScoring):
        with tracer.start_as_current_span("publish_application_scored_event"):
            logger.info(f"Publishing application {application_scoring.application_id} scored event")
            application_scored_event_dto = self.__map_to_dto(application_scoring)
            event_dict = asdict(application_scored_event_dto)
            self.kafka_producer.send(topic=self.output_topic, event=event_dict, key=application_scoring.application_id)
            logger.info(f"[{application_scoring.application_id} //  {application_scoring.position_id}] Published applicationScoredEvent to topic {self.output_topic}")

    def __map_to_dto(self, application_scoring: ApplicationScoring) -> ApplicationScoredEventDTO:
        analysis: ResumeAnalysis = application_scoring.analysis
        scoring : Scoring = application_scoring.scoring

        analysis_dto = ResumeAnalysisDTO(
            summary=analysis.summary,
            strengths=analysis.strengths,
            concerns=analysis.concerns,
            hardSkills=[SkillDTO(skill=skill.skill, level=skill.level) for skill in analysis.hard_skills],
            softSkills=[SkillDTO(skill=skill.skill, level=skill.level) for skill in analysis.soft_skills],
            keyResponsibilities=analysis.key_responsibilities,
            interviewQuestions=analysis.interview_questions,
            totalYearsExperience=analysis.total_years_xperience,
            averagePermanency=analysis.average_permanency,
            tags=analysis.tags
        )

        scoring_dto = ScoringDTO(
            score=scoring.score,
            descScore=scoring.desc_score,
            requirementScore=scoring.requirement_score,
            tasksScore=scoring.tasks_score,
            timeSpent=scoring.time_spent,
            explanation=scoring.explanation
        )

        return ApplicationScoredEventDTO(
            applicationId=application_scoring.application_id,
            positionId=application_scoring.position_id,
            analysis=analysis_dto,
            scoring=scoring_dto
        )