import json
import logging
from typing import Dict, Any
import aiokafka
from src.application.ports.event_producer import EventProducer
from kafka import KafkaProducer as Producer


logger = logging.getLogger(__name__)


class KafkaProducer(EventProducer):
    """
    Implementación del productor de eventos usando Kafka
    """

    def __init__(self, bootstrap_servers: str):
        self.bootstrap_servers = bootstrap_servers
        self.producer = Producer(bootstrap_servers=self.bootstrap_servers)

    def initialize(self):
        # self.producer = Producer(bootstrap_servers=self.bootstrap_servers)

        # self.producer = aiokafka.AIOKafkaProducer(
        #     bootstrap_servers=self.bootstrap_servers,
        #     value_serializer=lambda v: json.dumps(v).encode('utf-8')
        # )
        # self.producer.initialize()
        logger.info("Kafka producer initialized")

    def send(self, topic: str, event: Dict[str, Any], key: str = None):
        """
        Envía un evento a un topic específico

        Args:
            topic: El nombre del topic donde publicar
            event: El evento a publicar como diccionario
            key: Clave opcional para el particionado
        """
        if not self.producer:
            logger.error("Producer not initialized, cannot send message")
            return

        try:
            key_bytes = key.encode('utf-8') if key else None
            event_bytes = json.dumps(event).encode("utf-8")

            self.producer.send(topic, event_bytes, key_bytes, )
            logger.debug(f"Event sent to {topic}: {event.get('applicationId', 'unknown')}")
        except Exception as e:
            logger.exception(f"Error sending event to {topic}: {str(e)}")

    def close(self):
        """Cierra el productor"""
        if self.producer:
            self.producer.close()
            logger.info("Kafka producer closed")