import logging
from uuid import UUID

from src.adapters.db.models import Application
from src.adapters.db.repositories import ApplicationRepository
from src.domain.models.application_scoring import ApplicationScoring
from src.domain.models.paginated_result import PaginatedResult

logger = logging.getLogger(__name__)

class ApplicationService:

    def __init__(self, repository: ApplicationRepository) -> None:
        self.repository = repository

    def get_applications(self, position_id: int, page_size: int = 10, page: int = 0) -> PaginatedResult:
        offset = page * page_size
        return self.repository.get_all_by_position(position_id, page_size, offset)

    def get_application_by_id(self, application_id: UUID) -> Application:
        return self.repository.get_by_id(application_id)

    def save_application_scoring(self, application_scoring: ApplicationScoring):
        application_id = application_scoring.application_id
        application = self.get_application_by_id(application_id)
        if not application:
            logger.error(f"No se encontró la aplicación con ID: {application_id}")
            raise ValueError(f"No se encontró la aplicación con ID: {application_id}")

        self.repository.save_application_scoring(application_scoring)

