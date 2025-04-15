from fastapi import Depends
from sqlalchemy.orm import Session

from src.adapters.cvfiles.minio_cv_service import MinioCVService
from src.adapters.db.repositories import ApplicationRepository
from src.domain.services.application_service import ApplicationService
from src.infrastructure.core.config import ApplicationConfig, load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection
from src.infrastructure.minio.MinioClient import MinioClient


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

def get_cv_service(config: ApplicationConfig = Depends(load_config)) -> MinioCVService:
    client = MinioClient(
        endpoint=config.minio_url,
        access_key=config.minio_access_name,
        secret_key=config.minio_access_secret
    )
    return MinioCVService(
        minio_client=client,
        bucket=config.minio_bucket_name
    )

def get_application_service(application_repository: ApplicationRepository = Depends(get_applications_repository), cv_service: MinioCVService = Depends(get_cv_service)) -> ApplicationService:
    return ApplicationService(application_repository, cv_service)
