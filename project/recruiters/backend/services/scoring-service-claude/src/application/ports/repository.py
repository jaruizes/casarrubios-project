# src/application/ports/repository.py
from abc import ABC, abstractmethod
from typing import Optional
from src.domain.model.position import Position


class PositionRepository(ABC):
    """Interfaz para el repositorio de posiciones"""

    @abstractmethod
    def get_position_by_id(self, position_id: int) -> Optional[Position]:
        """
        Obtiene una posición por su ID incluyendo requisitos, tareas y beneficios

        Args:
            position_id: ID de la posición a buscar

        Returns:
            Objeto Position con todos sus datos relacionados o None si no existe
        """
        pass