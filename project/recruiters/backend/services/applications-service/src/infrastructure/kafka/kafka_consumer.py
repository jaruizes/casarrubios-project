import logging
import threading
import time
from typing import Callable

from confluent_kafka import Consumer, KafkaError, KafkaException

logger = logging.getLogger(__name__)

def safe_decode(value):
    return value.decode() if isinstance(value, bytes) else value


class KafkaConsumer():
    def __init__(self, bootstrap_servers: str, topic: str, group_id: str):
        self.bootstrap_servers = bootstrap_servers
        self.group_id = group_id
        self.topic = [topic]
        self.running = False
        self.consumer_thread = None
        self.consumer = Consumer({
            'bootstrap.servers': self.bootstrap_servers,
            'group.id': self.group_id,
            'enable.auto.commit': 'false',
            'auto.offset.reset': 'earliest'
        })


    def start(self, processor: Callable):
        if self.running:
            logger.warning("Consumer already running")
            return

        self.running = True
        self.consumer_thread = threading.Thread(
            target=self._consume_loop,
            args=(processor,),
            daemon=True
        )
        self.consumer_thread.start()
        logger.info(f"Started Kafka consumer for topics {self.topic}")

    def _consume_loop(self, processor: Callable):
        try:
            self.consumer.subscribe(self.topic)
            logger.info(f"Subscribed to topics: {self.topic}")

            while self.running:
                msg = self.consumer.poll(timeout=1.0)
                if msg is None:
                    continue
                else:
                    from opentelemetry.propagate import extract
                    from opentelemetry.context import attach, detach
                    from opentelemetry import trace

                    headers_list = msg.headers() or []
                    headers_dict = {
                        safe_decode(k): safe_decode(v)
                        for k, v in headers_list if k and v
                    }

                    tracer = trace.get_tracer(__name__)
                    ctx = extract(headers_dict)
                    token = attach(ctx)

                    with tracer.start_as_current_span("kafka-consume"):
                        processor(msg)

                    detach(token)
                    self.consumer.commit(asynchronous=False)
        except Exception as e:
            logger.exception(f"Error in Kafka consumer loop: {str(e)}")
        finally:
            if self.consumer:
                self.consumer.close()
                logger.info("Kafka consumer closed")

    def stop(self):
        if not self.running:
            return

        logger.info("Stopping Kafka consumer...")
        self.running = False

        if self.consumer_thread and self.consumer_thread.is_alive():
            self.consumer_thread.join(timeout=5)
            logger.info("Kafka consumer thread joined")

        if self.consumer:
            self.consumer.close()
            logger.info("Kafka consumer closed")
