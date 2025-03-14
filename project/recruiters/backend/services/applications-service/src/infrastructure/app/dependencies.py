from fastapi import Depends
from sqlalchemy.orm import Session

from src.adapters.db.repositories import ApplicationRepository
from src.domain.services.application_service import ApplicationService
from src.infrastructure.core.config import ApplicationConfig, load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection

def get_db(config: ApplicationConfig = Depends(load_config)) -> Session:
    db_connection = SQLAlchemyConnection(
        host=config.host,
        port=config.port,
        user=config.user,
        password=config.password,
        database=config.database
    )
    session_factory = db_connection.connect()
    return session_factory

def get_applications_repository(db: Session = Depends(get_db)) -> ApplicationRepository:
    return ApplicationRepository(db)

def get_application_service(application_respository: ApplicationRepository = Depends(get_applications_repository)) -> ApplicationService:
    return ApplicationService(application_respository)
