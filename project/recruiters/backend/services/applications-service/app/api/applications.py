from typing import List
from uuid import UUID

from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy.orm import Session

from app.api.dto.models import PaginatedApplicationsDTO, CandidateDTO, ApplicationDTO
from app.core.logger import logger
from app.db.database import get_db
from app.services.application_service import ApplicationService
from app.services.models.PaginatedResult import PaginatedResult
from app.db.models import Application

router = APIRouter()

@router.get(
    '/applications', response_model=PaginatedApplicationsDTO, tags=['paginatedResult']
)
def get_applications(
    positionId: int = Query(..., description="The ID of the position"),
    pageSize: int = Query(10, ge=1, le=100),
    page: int = Query(0, ge=0),
    db: Session = Depends(get_db)) -> PaginatedApplicationsDTO:

    logger.info(f"Getting applications for position {positionId} with page size {pageSize} and page {page}")

    paginated_result: PaginatedResult = ApplicationService.get_applications(db, positionId, pageSize, page)
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
def get_application_by_id(application_id: UUID, db: Session = Depends(get_db)) -> ApplicationDTO:
    logger.info(f"Getting application by ID {application_id}")

    application = ApplicationService.get_application_by_id(db, application_id)
    if not application:
        logger.error(f"Application with ID {application_id} not found")
        raise HTTPException(status_code=404, detail=f"Application with ID {application_id} not found")

    return build_application_dto(application)


def build_application_dto(application: Application) -> ApplicationDTO:
    creation_date = application.created_at.isoformat()
    candidate: CandidateDTO = CandidateDTO(name=application.name, email=application.email, phone=application.phone)
    return ApplicationDTO(applicationId=application.id, candidate=candidate, positionId=application.position_id, cvFile=application.cv, creationDate=creation_date)