from abc import ABC, abstractmethod
from typing import Callable, Any


class EventConsumer(ABC):
    """Interfaz para consumir eventos"""

    @abstractmethod
    async def subscribe(self, topic: str, handler: Callable[[dict], Any]):
        """
        Suscribe un handler a un topic específico

        Args:
            topic: El nombre del topic para suscribirse
            handler: La función que procesará los eventos
        """
        pass

    @abstractmethod
    async def start(self):
        """Inicia el consumo de eventos"""
        pass

    @abstractmethod
    async def stop(self):
        """Detiene el consumo de eventos"""
        pass