from typing import Optional

from sqlalchemy.orm import Session
from app.db.models import Application
from app.services.models.PaginatedResult import PaginatedResult
from sqlalchemy import func


class ApplicationRepository:
    @staticmethod
    def get_all_by_position(db: Session, position_id: int, limit: int = 10, offset: int = 0) -> PaginatedResult:
        total_elements = db.query(func.count(Application.id)).filter(Application.position_id == position_id).scalar()
        applications = db.query(Application).filter(Application.position_id == position_id).offset(offset).limit(limit).all()

        return PaginatedResult(applications, total_elements)

    @staticmethod
    def get_by_id(db: Session, application_id: int) -> Optional[Application]:
        return db.query(Application).filter(Application.id == application_id).first()
