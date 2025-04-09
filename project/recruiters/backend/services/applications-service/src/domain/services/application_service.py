import logging
from uuid import UUID

from src.adapters.cvfiles.minio_cv_service import MinioCVService
from src.adapters.db.repositories import ApplicationRepository
from src.domain.exceptions.ApplicationNotFoundException import ApplicationNotFoundException
from src.domain.models.applications_model import ApplicationDetail, PaginatedResult

logger = logging.getLogger(__name__)

class ApplicationService:

    def __init__(self, repository: ApplicationRepository, cv_service: MinioCVService) -> None:
        self.repository = repository
        self.cv_service = cv_service

    def get_applications(self, position_id: int, page_size: int = 10, page: int = 0) -> PaginatedResult:
        logger.info(f"Getting applications for position {position_id} with page size {page_size} and page {page}")
        offset = page * page_size
        return self.repository.get_all_by_position(position_id, page_size, offset)


    def get_application_by_id(self, application_id: UUID) -> ApplicationDetail:
        logger.info(f"Getting application by ID {application_id}")
        application = self.repository.get_by_id(application_id)
        if not application:
            logger.error(f"Application with ID {application_id} not found")
            raise ApplicationNotFoundException(f"Application with ID {application_id} not found", "APPLICATION_NOT_FOUND")
        
        logger.info(f"Application with ID {application_id} found")
        return application


    def save_application_scoring(self, application_scoring: ApplicationDetail):
        logger.info(f"Saving application scoring for application ID {application_scoring.application_id}")
        application_id = application_scoring.application_id

        if self.repository.application_exists(application_id):
            logger.info(f"Storing analysis and scoring info for application ID {application_id}")
            self.repository.save_application_scoring(application_scoring)
        else:
            logger.error(f"Application with ID {application_id} does not exist")
            raise ApplicationNotFoundException(f"Application with ID {application_id} does not exist", "APPLICATION_NOT_FOUND")



    def get_cv_file(self, application_id: UUID) -> bytes:
        logger.info(f"Getting CV file for application ID {application_id}")
        self.get_application_by_id(application_id)

        return self.cv_service.get_cv_file(application_id)
