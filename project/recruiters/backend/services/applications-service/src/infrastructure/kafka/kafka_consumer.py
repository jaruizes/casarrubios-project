import logging
import threading
import time
from typing import Callable

from confluent_kafka import Consumer, KafkaError, KafkaException

logger = logging.getLogger(__name__)

def manage_errors(msg):
    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            logger.error('EOF in partition %d: %s', msg.partition(), msg.error())
        elif msg.error():
            logger.error('Unknown error: %s', msg.error())
            raise KafkaException(msg.error())


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

    def start(self, handler: Callable):
        """Inicia el consumidor en un hilo separado"""
        if self.running:
            logger.warning("Consumer already running")
            return
        
        self.running = True
        self.consumer_thread = threading.Thread(
            target=self._consume_loop,
            args=(handler,),
            daemon=True
        )
        self.consumer_thread.start()
        logger.info(f"Started Kafka consumer for topics {self.topic}")

    def _consume_loop(self, handler: Callable):
        """Bucle principal de consumo que se ejecuta en un hilo separado"""
        try:
            self.consumer.subscribe(self.topic)
            logger.info(f"Subscribed to topics: {self.topic}")
            
            while self.running:
                msg = self.consumer.poll(timeout=1.0)
                if msg is None:
                    continue

                if msg.error():
                    manage_errors(msg)
                else:
                    try:
                        logger.info(f"Received message on topic {msg.topic()}")
                        handler(msg)
                        self.consumer.commit(asynchronous=False)
                        logger.info("Message processed and committed")
                    except Exception as e:
                        logger.exception(f"Error processing message: {str(e)}")
        except Exception as e:
            logger.exception(f"Error in Kafka consumer loop: {str(e)}")
        finally:
            if self.consumer:
                self.consumer.close()
                logger.info("Kafka consumer closed")

    def stop(self):
        """Detiene el consumidor y espera a que el hilo termine"""
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
