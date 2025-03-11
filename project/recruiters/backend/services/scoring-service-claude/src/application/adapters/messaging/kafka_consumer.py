import json
import logging
from typing import Callable, Any, Dict
import aiokafka
from src.application.ports.event_consumer import EventConsumer

logger = logging.getLogger(__name__)


class KafkaConsumer(EventConsumer):
    """
    Implementación del consumidor de eventos usando Kafka
    """

    def __init__(self, bootstrap_servers: str, group_id: str):
        self.bootstrap_servers = bootstrap_servers
        self.group_id = group_id
        self.consumer = None
        self.handlers = {}
        self.running = False

    async def subscribe(self, topic: str, handler: Callable[[Dict[str, Any]], Any]):
        """
        Suscribe un handler a un topic específico

        Args:
            topic: El nombre del topic para suscribirse
            handler: La función que procesará los eventos
        """
        self.handlers[topic] = handler
        logger.info(f"Subscribed to topic: {topic}")

    async def start(self):
        """Inicia el consumo de eventos"""
        if not self.handlers:
            logger.warning("No handlers registered, not starting consumer")
            return

        self.consumer = aiokafka.AIOKafkaConsumer(
            *self.handlers.keys(),
            bootstrap_servers=self.bootstrap_servers,
            group_id=self.group_id,
            auto_offset_reset='earliest',
            enable_auto_commit=False,
            value_deserializer=lambda m: json.loads(m.decode('utf-8'))
        )

        await self.consumer.start()
        self.running = True

        logger.info(f"Kafka consumer started, subscribed to: {list(self.handlers.keys())}")

        try:
            async for msg in self.consumer:
                try:
                    logger.debug(f"Received message: {msg.topic}, {msg.partition}, {msg.offset}")
                    handler = self.handlers.get(msg.topic)
                    if handler:
                        await handler(msg.value)
                        await self.consumer.commit()
                    else:
                        logger.warning(f"No handler for topic {msg.topic}")
                except Exception as e:
                    logger.exception(f"Error processing message: {str(e)}")
        finally:
            self.running = False
            await self.consumer.stop()

    async def stop(self):
        """Detiene el consumo de eventos"""
        self.running = False
        if self.consumer:
            await self.consumer.stop()
            logger.info("Kafka consumer stopped")
