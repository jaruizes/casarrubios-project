# src/infrastructure/db/sqlalchemy_connection.py
import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import QueuePool

logger = logging.getLogger(__name__)


class SQLAlchemyConnection:
    def __init__(self, host: str, port: int, user: str, password: str, database: str):
        self.connection_string = f"postgresql://{user}:{password}@{host}:{port}/{database}"
        self.engine = None
        self.session_factory = None

    def connect(self, pool_size: int = 5, max_overflow: int = 10):
        try:
            logger.info(f"Connecting to database at {self.connection_string.split('@')[1]}")

            self.engine = create_engine(
                self.connection_string,
                poolclass=QueuePool,
                pool_size=pool_size,
                max_overflow=max_overflow,
                pool_timeout=30,
                pool_recycle=1800,
                echo=False,
                connect_args={
                    "options": "-c search_path=recruiters,public"
                }
            )

            self.session_factory = sessionmaker(
                bind=self.engine,
                expire_on_commit=False  # evita que los objetos expiren después de commit
            )

            logger.info("Database connection established")
            return self.session_factory

        except Exception as e:
            logger.exception(f"Failed to connect to database: {str(e)}")
            raise

    def disconnect(self):
        if self.engine:
            self.engine.dispose()
            logger.info("Database connection closed")