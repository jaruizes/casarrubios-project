from typing import Optional, List
from uuid import UUID

from sqlalchemy.orm import Session
from app.db.models import Application
from app.services.models.PaginatedResult import PaginatedResult
from sqlalchemy import func


class ApplicationRepository:
    @staticmethod
    def get_all_by_position(db: Session, position_id: int, limit: int = 10, offset: int = 0) -> PaginatedResult:
        total_elements = db.query(func.count(Application.id)).filter(Application.position_id == position_id).scalar()
        applications: List[Application] = db.query(Application).filter(Application.position_id == position_id).offset(offset).limit(limit).all()

        return PaginatedResult(applications, total_elements)

    @staticmethod
    def get_by_id(db: Session, application_id: UUID) -> Optional[Application]:
        return db.query(Application).filter(Application.id == str(application_id)).first()
