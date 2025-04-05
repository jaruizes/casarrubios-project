import json
import logging
import socket

from confluent_kafka import Producer
from opentelemetry.propagate import inject
from opentelemetry.propagate import extract
from opentelemetry.context import attach, detach
from opentelemetry import trace

logger = logging.getLogger(__name__)


class KafkaProducer():
    def __init__(self, bootstrap_servers: str):
        self.bootstrap_servers = bootstrap_servers
        conf = {'bootstrap.servers': bootstrap_servers,
                'client.id': socket.gethostname()}

        self.producer = Producer(conf)

    def send(self, topic: str, event: dict[str, any], key: str = None):
        with tracer.start_as_current_span("scoring_produce_event"):
            try:
                event_bytes = json.dumps(event).encode("utf-8")
                self.producer.produce(
                    topic=topic,
                    key=key,
                    value=event_bytes,
                    on_delivery=self.__acked
                )

                logger.debug(f"Event sent to {topic}: {event.get('applicationId', 'unknown')}")
            except Exception as e:
                logger.exception(f"Error sending event to {topic}: {str(e)}")

    def close(self):
        if self.producer:
            self.producer.close()
            logger.info("Kafka producer closed")

    def __acked(err, msg):
        if err is not None:
            print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
        else:
            print("Message produced: %s" % (str(msg)))