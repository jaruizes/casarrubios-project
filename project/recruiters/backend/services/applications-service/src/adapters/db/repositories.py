from typing import Optional, List
from uuid import UUID


from src.adapters.db.models import CandidateDB, CandidateAnalysisDB, CandidateStrengthDB, \
    CandidateConcernDB, CandidateHardSkillDB, CandidateSoftSkillDB, CandidateKeyResponsibilityDB, CandidateInterviewQuestionDB, \
    CandidateTagDB, CandidateApplicationsDB, ApplicationScoreDB
from src.domain.models.applications_model import Scoring, PaginatedResult, CandidateApplication, Candidate, \
    ApplicationDetail, CandidateDetail, ResumeAnalysis, Skill
from sqlalchemy import func



class ApplicationRepository:

    def __init__(self, session_factory):
        self.session_factory = session_factory
        self.db = self.session_factory()

    def get_all_by_position(self, position_id: int, limit: int = 10, offset: int = 0) -> PaginatedResult:
        total_elements = (
            self.db.query(func.count(CandidateDB.id))
            .join(CandidateApplicationsDB, CandidateDB.id == CandidateApplicationsDB.candidate_id)
            .filter(CandidateApplicationsDB.position_id == position_id)
            .scalar()
        )

        db_results = (
            self.db.query(CandidateDB, CandidateApplicationsDB)
            .join(CandidateApplicationsDB, CandidateDB.id == CandidateApplicationsDB.candidate_id)
            .filter(CandidateApplicationsDB.position_id == position_id)
            .offset(offset)
            .limit(limit)
            .all()
        )

        candidate_applications: List[CandidateApplication] = [
            CandidateApplication(
                application_id=candidate_application_db.id,
                position_id=candidate_application_db.position_id,
                candidate=Candidate(
                    name=candidate_db.name,
                    email=candidate_db.email,
                    phone=candidate_db.phone,
                    candidate_id=candidate_db.id,
                    cv=candidate_db.cv
                ),
                created_at=candidate_application_db.created_at
            )
            for candidate_db, candidate_application_db  in db_results
        ]

        return PaginatedResult(candidate_applications, total_elements)

    def application_exists(self, application_id: UUID) -> bool:
        db_result = (
            self.db.query(CandidateDB, CandidateApplicationsDB)
            .join(CandidateApplicationsDB, CandidateDB.id == CandidateApplicationsDB.candidate_id)
            .filter(CandidateApplicationsDB.id == application_id)
            .first()
        )

        if not db_result:
            return False

        return True

    def get_by_id(self, application_id: UUID) -> Optional[ApplicationDetail]:
        db_result = (
            self.db.query(CandidateDB, CandidateApplicationsDB)
            .join(CandidateApplicationsDB, CandidateDB.id == CandidateApplicationsDB.candidate_id)
            .filter(CandidateApplicationsDB.id == application_id)
            .first()
        )

        if not db_result:
            return None

        return self.__build_application_detail(application_db=db_result[1], candidate_db=db_result[0])


    def save_application_scoring(self, application_detail: ApplicationDetail):
        try:
            application_id = application_detail.application_id
            candidate_id = application_detail.candidate.candidate_id
            analysis = application_detail.candidate.analysis
            scoring = application_detail.scoring

            for strength_text in analysis.strengths:
                strength = CandidateStrengthDB(candidate_id=candidate_id, strength=strength_text)
                self.db.add(strength)

            for concern_text in analysis.concerns:
                concern = CandidateConcernDB(candidate_id=candidate_id, concern=concern_text)
                self.db.add(concern)

            for skill_data in analysis.hard_skills:
                hard_skill = CandidateHardSkillDB(
                    candidate_id=candidate_id,
                    skill=skill_data.skill,
                    level=skill_data.level
                )
                self.db.add(hard_skill)

            for skill_data in analysis.soft_skills:
                soft_skill = CandidateSoftSkillDB(
                    candidate_id=candidate_id,
                    skill=skill_data.skill,
                    level=skill_data.level
                )
                self.db.add(soft_skill)

            for responsibility_text in analysis.key_responsibilities:
                responsibility = CandidateKeyResponsibilityDB(
                    candidate_id=candidate_id,
                    responsibility=responsibility_text
                )
                self.db.add(responsibility)

            for question_text in analysis.interview_questions:
                question = CandidateInterviewQuestionDB(
                    candidate_id=candidate_id,
                    question=question_text
                )
                self.db.add(question)

            for tag_text in analysis.tags:
                tag = CandidateTagDB(candidate_id=candidate_id, tag=tag_text)
                self.db.add(tag)

            scoring_db = ApplicationScoreDB(
                application_id=application_id,
                score=scoring.score,
                desc_score=scoring.desc_score,
                requirement_score=scoring.requirement_score,
                tasks_score=scoring.tasks_score,
                explanation=scoring.explanation,
                time_spent=scoring.time_spent
            )
            self.db.add(scoring_db)

            resume_analysis = CandidateAnalysisDB(
                candidate_id=candidate_id,
                summary=analysis.summary,
                total_years_experience=analysis.total_years_xperience,
                average_permanency=analysis.average_permanency
            )
            self.db.add(resume_analysis)
            self.db.commit()
            return True
        except Exception as e:
            self.db.rollback()
            raise e


    def __build_application_detail(self, application_db: CandidateApplicationsDB, candidate_db: CandidateDB) -> ApplicationDetail:
        resume_analysis = ResumeAnalysis(
            summary=candidate_db.analysis.summary,
            total_years_xperience=candidate_db.analysis.total_years_experience,
            average_permanency=candidate_db.analysis.average_permanency,
            strengths=[strength.strength for strength in candidate_db.strengths],
            concerns=[concern.concern for concern in candidate_db.concerns],
            hard_skills=[Skill(skill=hard_skill.skill, level=hard_skill.level) for hard_skill in
                         candidate_db.hard_skills],
            soft_skills=[Skill(skill=soft_skill.skill, level=soft_skill.level) for soft_skill in
                         candidate_db.soft_skills],
            key_responsibilities=[responsibility.responsibility for responsibility in candidate_db.responsibilities],
            interview_questions=[question.question for question in candidate_db.questions],
            tags=[tag.tag for tag in candidate_db.tags]
        )

        candidate_detail = CandidateDetail(
            candidate_id=candidate_db.id,
            name=candidate_db.name,
            email=candidate_db.email,
            phone=candidate_db.phone,
            analysis=resume_analysis,
            cv=candidate_db.cv
        )

        scoring = Scoring(
            application_id=application_db.id,
            score=application_db.scoring.score,
            desc_score=application_db.scoring.desc_score,
            requirement_score=application_db.scoring.requirement_score,
            tasks_score=application_db.scoring.tasks_score,
            explanation=application_db.scoring.explanation,
            time_spent=application_db.scoring.time_spent
        )

        return ApplicationDetail(
            application_id=application_db.id,
            position_id=application_db.position_id,
            candidate=candidate_detail,
            scoring=scoring,
            created_at=application_db.created_at
        )
