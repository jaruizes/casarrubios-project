from uuid import UUID

from sqlalchemy.orm import Session

from app.db.models import Application
from app.db.repositories import ApplicationRepository
from app.services.models import PaginatedResult


class ApplicationService:
    @staticmethod
    def get_applications(db: Session, position_id: int, page_size: int = 10, page: int = 0) -> PaginatedResult:
        offset = page * page_size
        return ApplicationRepository.get_all_by_position(db, position_id, page_size, offset)

    @staticmethod
    def get_application_by_id(db: Session, application_id: UUID) -> Application:
        return ApplicationRepository.get_by_id(db, application_id)

