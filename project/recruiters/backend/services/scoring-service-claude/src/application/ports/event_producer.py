from abc import ABC, abstractmethod
from typing import Dict, Any


class EventProducer(ABC):
    """Interfaz para producir eventos"""

    @abstractmethod
    async def send(self, topic: str, event: Dict[str, Any], key: str = None):
        """
        Envía un evento a un topic específico

        Args:
            topic: El nombre del topic donde publicar
            event: El evento a publicar como diccionario
            key: Clave opcional para el particionado
        """
        pass