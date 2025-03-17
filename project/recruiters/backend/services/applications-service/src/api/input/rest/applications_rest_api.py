import logging
from typing import List
from uuid import UUID
import io
from fastapi import APIRouter, Depends, Query, HTTPException
from starlette.responses import StreamingResponse

from src.api.input.rest.dto.models import PaginatedApplicationsDTO, ApplicationDTO, CandidateDTO, ResumeAnalysisDTO, \
    SkillDTO, ScoringDTO
from src.domain.exceptions.CVException import CVException
from src.domain.exceptions.CVFileNotFoundException import CVFileNotFoundException
from src.domain.services.application_service import ApplicationService
from src.domain.models.paginated_result import PaginatedResult
from src.adapters.db.models import Application, HardSkill

from src.infrastructure.app.dependencies import get_application_service

logger = logging.getLogger(__name__)
router = APIRouter()

@router.get(
    '/applications', response_model=PaginatedApplicationsDTO, tags=['paginatedResult']
)
def get_applications(
    positionId: int = Query(..., description="The ID of the position"),
    pageSize: int = Query(10, ge=1, le=100),
    page: int = Query(0, ge=0),
    application_service: ApplicationService = Depends(get_application_service)) -> PaginatedApplicationsDTO:

    logger.info(f"Getting applications for position {positionId} with page size {pageSize} and page {page}")

    paginated_result: PaginatedResult = application_service.get_applications(positionId, pageSize, page)
    logger.info(f"Found {paginated_result.total_elements} applications. Returning page {page} of {pageSize} elements")

    total_pages = paginated_result.total_elements // pageSize
    if paginated_result.total_elements % pageSize > 0:
        total_pages += 1

    applications_dto: List[ApplicationDTO] = []
    for application in paginated_result.data:
        applications_dto.append(build_application_dto(application))

    return PaginatedApplicationsDTO(applications=applications_dto,
                                    totalElements=paginated_result.total_elements,
                                    totalPages=total_pages,
                                    size=pageSize,
                                    number=page)

@router.get(
    '/applications/{application_id}', response_model=ApplicationDTO, tags=['applicationDTO']
)
def get_application_by_id(application_id: UUID, application_service: ApplicationService = Depends(get_application_service)) -> ApplicationDTO:
    logger.info(f"Getting application by ID {application_id}")

    application = application_service.get_application_by_id(application_id)
    if not application:
        logger.error(f"Application with ID {application_id} not found")
        raise HTTPException(status_code=404, detail=f"Application with ID {application_id} not found")

    return build_application_dto(application, True)


@router.get('/applications/{application_id}/cv')
def get_application_cv(application_id: UUID, application_service: ApplicationService = Depends(get_application_service)) -> StreamingResponse:
    logger.info(f"Getting application CV by ID {application_id}")

    try:
        cv_file = application_service.get_cv_file(application_id)
        return StreamingResponse(
            content=io.BytesIO(cv_file), 
            media_type="application/pdf",
            headers={
                "Content-Disposition": f"attachment; filename=cv_{application_id}.pdf",
                "Content-Length": str(len(cv_file))
            }
        )
    except CVFileNotFoundException as e:
        logger.error(f"CV file not found for application ID {application_id}: {e}")
        raise HTTPException(status_code=404, detail=f"CV file not found for application ID {application_id}")
    except CVException as e:
        logger.error(f"Error getting cv file for application ID {application_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Error getting cv for application ID {application_id}")


def build_application_dto(application: Application, is_detail: bool = False) -> ApplicationDTO:
    creation_date = application.created_at.isoformat()
    candidate: CandidateDTO = CandidateDTO(name=application.name, email=application.email, phone=application.phone)
    application_dto = ApplicationDTO(applicationId=application.id, candidate=candidate, positionId=application.position_id, cvFile=application.cv, creationDate=creation_date)

    analysis: ResumeAnalysisDTO = None
    if is_detail and application.resume_analysis is not None:
        analysis: ResumeAnalysisDTO = ResumeAnalysisDTO(
            summary=application.resume_analysis.summary,
            strengths=[strength.strength for strength in application.strengths],
            concerns=[concern.concern for concern in application.concerns],
            hardSkills=[SkillDTO(skill=hard_skill.skill, level=hard_skill.level) for hard_skill in application.hard_skills],
            softSkills=[SkillDTO(skill=soft_skill.skill, level=soft_skill.level) for soft_skill in application.soft_skills],
            keyResponsibilities=[responsibility.responsibility for responsibility in application.key_responsibilities],
            interviewQuestions=[question.question for question in application.interview_questions],
            totalYearsExperience=application.resume_analysis.total_years_experience,
            averagePermanency=application.resume_analysis.average_permanency,
            tags=[tag.tag for tag in application.tags]
        )
    else:
        analysis: ResumeAnalysisDTO = ResumeAnalysisDTO(
            summary="",
            strengths=[],
            concerns=[],
            hardSkills=[],
            softSkills=[],
            keyResponsibilities=[],
            interviewQuestions=[],
            totalYearsExperience=0,
            averagePermanency=0,
            tags=[tag.tag for tag in application.tags]
        )

    application_dto.add_analysis(analysis)

    if application.scoring is not None:
        scoring: ScoringDTO = ScoringDTO(
            score=application.scoring.score,
            descScore=application.scoring.desc_score,
            requirementScore=application.scoring.requirement_score,
            tasksScore=application.scoring.tasks_score,
            timeSpent=application.scoring.time_spent
        )
        application_dto.add_scoring(scoring)

    return application_dto