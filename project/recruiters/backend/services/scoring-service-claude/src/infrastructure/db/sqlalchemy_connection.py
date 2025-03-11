# src/infrastructure/db/sqlalchemy_connection.py
import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import QueuePool

logger = logging.getLogger(__name__)


class SQLAlchemyConnection:
    """Gestiona la conexión a la base de datos PostgreSQL usando SQLAlchemy"""

    def __init__(self, host: str, port: int, user: str, password: str, database: str):
        self.connection_string = f"postgresql://{user}:{password}@{host}:{port}/{database}"
        self.engine = None
        self.session_factory = None

    def connect(self, pool_size: int = 5, max_overflow: int = 10):
        """
        Establece la conexión a la base de datos

        Args:
            pool_size: Tamaño del pool de conexiones
            max_overflow: Máximo número de conexiones que pueden crearse por encima del pool_size
        """
        try:
            logger.info(f"Connecting to database at {self.connection_string.split('@')[1]}")

            # Crear engine de SQLAlchemy
            self.engine = create_engine(
                self.connection_string,
                poolclass=QueuePool,
                pool_size=pool_size,
                max_overflow=max_overflow,
                pool_timeout=30,  # tiempo de espera para obtener una conexión del pool
                pool_recycle=1800,  # reciclar conexiones después de 30 minutos
                echo=False, # cambiar a True para ver las consultas SQL en los logs
                connect_args={
                    "options": "-c search_path=recruiters,public"
                }

            )

            # Crear factory de sesiones
            self.session_factory = sessionmaker(
                bind=self.engine,
                expire_on_commit=False  # evita que los objetos expiren después de commit
            )

            # Probar la conexión
            with self.engine.connect() as conn:
                conn.execute("SELECT 1")

            logger.info("Database connection established")
            return self.session_factory

        except Exception as e:
            logger.exception(f"Failed to connect to database: {str(e)}")
            raise

    def disconnect(self):
        """Cierra la conexión a la base de datos"""
        if self.engine:
            self.engine.dispose()
            logger.info("Database connection closed")