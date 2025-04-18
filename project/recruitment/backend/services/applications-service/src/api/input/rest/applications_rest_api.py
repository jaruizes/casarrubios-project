import logging
from typing import List
from uuid import UUID
import io
from fastapi import APIRouter, Depends, Query, HTTPException
from pydantic import ValidationError
from starlette.responses import StreamingResponse

from src.api.input.rest.dto.application_rest_api_dto import PaginatedApplicationsDTO, ApplicationDTO, CandidateDTO, \
    ResumeAnalysisDTO, \
    SkillDTO, ScoringDTO, ApplicationDetailDTO
from src.domain.exceptions.ApplicationNotFoundException import ApplicationNotFoundException

from src.domain.exceptions.CVException import CVException
from src.domain.exceptions.CVFileNotFoundException import CVFileNotFoundException
from src.domain.models.applications_model import ApplicationDetail, CandidateApplication, PaginatedResult
from src.domain.services.application_service import ApplicationService

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
    '/applications/{application_id}', response_model=ApplicationDetailDTO, tags=['applicationDTO']
)
def get_application_by_id(application_id: UUID, application_service: ApplicationService = Depends(get_application_service)) -> ApplicationDetailDTO:
    logger.info(f"Getting application by ID {application_id}")

    try:
        application_detail: ApplicationDetail = application_service.get_application_by_id(application_id)
    except ApplicationNotFoundException:
        logger.error(f"Application with ID {application_id} not found")
        raise HTTPException(status_code=404, detail=f"Application with ID {application_id} not found")

    return build_application_detail_dto(application_detail)


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


def build_application_dto(application: CandidateApplication) -> ApplicationDTO:
    creation_date = application.created_at.isoformat()
    candidate = application.candidate
    application_dto = ApplicationDTO(applicationId=application.application_id,
                                     candidate=CandidateDTO(id=candidate.candidate_id, name=candidate.name, email=candidate.email, phone=candidate.phone),
                                     positionId=application.position_id,
                                     cvFile=candidate.cv,
                                     tags=application.tags,
                                     scoring=application.scoring,
                                     creationDate=creation_date)


    return application_dto

def build_application_detail_dto(application: ApplicationDetail) -> ApplicationDetailDTO:
    creation_date = application.created_at.isoformat()
    candidate = application.candidate
    candidate_dto: CandidateDTO = CandidateDTO(id=candidate.candidate_id,
                                               name=candidate.name,
                                               email=candidate.email,
                                               phone=candidate.phone)
    analysis = candidate.analysis
    analysis_dto: ResumeAnalysisDTO = ResumeAnalysisDTO(
        summary=candidate.analysis.summary,
        strengths=[strength for strength in analysis.strengths],
        concerns=[concern for concern in analysis.concerns],
        hardSkills=[SkillDTO(skill=hard_skill.skill, level=hard_skill.level) for hard_skill in analysis.hard_skills],
        softSkills=[SkillDTO(skill=soft_skill.skill, level=soft_skill.level) for soft_skill in analysis.soft_skills],
        keyResponsibilities=[responsibility for responsibility in analysis.key_responsibilities],
        interviewQuestions=[question for question in analysis.interview_questions],
        totalYearsExperience=analysis.total_years_xperience,
        averagePermanency=analysis.average_permanency,
        tags=[tag for tag in analysis.tags]
    )

    scoring_dto: ScoringDTO = ScoringDTO(
        score=application.scoring.score,
        descScore=application.scoring.desc_score,
        requirementScore=application.scoring.requirement_score,
        tasksScore=application.scoring.tasks_score,
        timeSpent=application.scoring.time_spent,
        explanation=application.scoring.explanation
    )

    application_dto = ApplicationDetailDTO(applicationId=application.application_id,
                                           candidate=candidate_dto,
                                           positionId=application.position_id,
                                           cvFile=candidate.cv,
                                           analysis=analysis_dto,
                                           scoring=scoring_dto,
                                           creationDate=creation_date)

    return application_dto
