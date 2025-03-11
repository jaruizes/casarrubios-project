# src/application/adapters/db/sqlalchemy_repository.py
import logging
from typing import Optional
from sqlalchemy.orm import Session
from sqlalchemy.orm import sessionmaker

from src.domain.model.position import Position as DomainPosition
from src.domain.model.position import Requirement as DomainRequirement
from src.domain.model.position import Task as DomainTask
from src.domain.model.position import Benefit as DomainBenefit
from src.application.ports.repository import PositionRepository
from src.application.adapters.db.models import Position as DbPosition

logger = logging.getLogger(__name__)


class SQLAlchemyPositionRepository(PositionRepository):
    """
    Implementación del repositorio de posiciones usando SQLAlchemy
    """

    def __init__(self, session_factory):
        self.session_factory = session_factory

    def get_position_by_id(self, position_id: int) -> Optional[DomainPosition]:
        """
        Obtiene una posición por su ID incluyendo requisitos, tareas y beneficios

        Args:
            position_id: ID de la posición a buscar

        Returns:
            Objeto Position con todos sus datos relacionados o None si no existe
        """
        session = self.session_factory()
        try:
            # Consultar la posición con todas sus relaciones en una sola consulta
            db_position = session.query(DbPosition).filter(
                DbPosition.id == position_id
            ).first()

            if not db_position:
                logger.warning(f"Position with id {position_id} not found")
                return None

            # Convertir de modelo de BD a modelo de dominio
            position = DomainPosition(
                id=db_position.id,
                title=db_position.title,
                description=db_position.description,
                status=int(db_position.status),
                created_at=str(db_position.created_at),
                published_at=str(db_position.published_at) if db_position.published_at else None,
                tags=db_position.tags
            )

            # Convertir requisitos
            position.requirements = [
                DomainRequirement(
                    id=req.id,
                    key=req.key,
                    value=req.value,
                    description=req.description,
                    mandatory=req.mandatory
                )
                for req in db_position.requirements
            ]

            # Convertir tareas
            position.tasks = [
                DomainTask(
                    id=task.id,
                    description=task.description
                )
                for task in db_position.tasks
            ]

            # Convertir beneficios
            position.benefits = [
                DomainBenefit(
                    id=benefit.id,
                    description=benefit.description
                )
                for benefit in db_position.benefits
            ]

            return position

        except Exception as e:
            logger.exception(f"Error getting position: {str(e)}")
            raise
        finally:
            session.close()