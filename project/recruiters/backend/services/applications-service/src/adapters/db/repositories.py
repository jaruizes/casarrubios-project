from typing import Optional, List
from uuid import UUID

from sqlalchemy.orm import Session
from src.adapters.db.models import Application, Strength, Concern, HardSkill, SoftSkill, KeyResponsibility, InterviewQuestion, \
    Tag, Scoring
from src.domain.models.paginated_result import PaginatedResult
from sqlalchemy import func

from src.adapters.db.models import ResumeAnalysis
from src.domain.models.application_scoring import ApplicationScoring


class ApplicationRepository:

    def __init__(self, session_factory):
        self.session_factory = session_factory
        self.db = self.session_factory()

    def get_all_by_position(self, position_id: int, limit: int = 10, offset: int = 0) -> PaginatedResult:
        total_elements = self.db.query(func.count(Application.id)).filter(Application.position_id == position_id).scalar()
        applications: List[Application] = self.db.query(Application).filter(Application.position_id == position_id).offset(offset).limit(limit).all()

        return PaginatedResult(applications, total_elements)

    def get_by_id(self, application_id: UUID) -> Optional[Application]:
        return self.db.query(Application).filter(Application.id == str(application_id)).first()

    def save_application_scoring(self, application_scoring: ApplicationScoring):
        try:
            application_id = application_scoring.application_id
            analysis = application_scoring.analysis
            scoring = application_scoring.scoring
            resume_analysis = ResumeAnalysis(
                application_id=application_id,
                summary=analysis.summary,
                total_years_experience=analysis.total_years_xperience,
                average_permanency=analysis.average_permanency
            )
            self.db.add(resume_analysis)

            for strength_text in analysis.strengths:
                strength = Strength(application_id=application_id, strength=strength_text)
                self.db.add(strength)

            for concern_text in analysis.concerns:
                concern = Concern(application_id=application_id, concern=concern_text)
                self.db.add(concern)

            for skill_data in analysis.hard_skills:
                hard_skill = HardSkill(
                    application_id=application_id,
                    skill=skill_data.skill,
                    level=skill_data.level
                )
                self.db.add(hard_skill)

            for skill_data in analysis.soft_skills:
                soft_skill = SoftSkill(
                    application_id=application_id,
                    skill=skill_data.skill,
                    level=skill_data.level
                )
                self.db.add(soft_skill)

            for responsibility_text in analysis.key_responsibilities:
                responsibility = KeyResponsibility(
                    application_id=application_id,
                    responsibility=responsibility_text
                )
                self.db.add(responsibility)

            for question_text in analysis.interview_questions:
                question = InterviewQuestion(
                    application_id=application_id,
                    question=question_text
                )
                self.db.add(question)

            for tag_text in analysis.tags:
                tag = Tag(application_id=application_id, tag=tag_text)
                self.db.add(tag)

            scoring_db = Scoring(
                application_id=application_id,
                score=scoring.score,
                desc_score=scoring.desc_score,
                requirement_score=scoring.requirement_score,
                tasks_score=scoring.tasks_score,
                time_spent=scoring.time_spent
            )
            self.db.add(scoring_db)
            self.db.commit()
            return True
        except Exception as e:
            self.db.rollback()
            raise e
