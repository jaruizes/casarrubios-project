import logging

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
        self.topic = [topic]
        self.group_id = group_id
        self.consumer = Consumer({
            'bootstrap.servers': self.bootstrap_servers,
            'group.id': self.group_id,
            'enable.auto.commit': 'false',
            'auto.offset.reset': 'earliest'
        })

    def start(self, processor):
        try:
            self.consumer.subscribe(self.topic)
            while True:
                msg = self.consumer.poll(timeout=1.0)
                if msg is None: continue

                if msg.error():
                    manage_errors(msg)
                else:
                    from opentelemetry.propagate import extract
                    from opentelemetry.context import attach, detach
                    from opentelemetry import trace

                    headers_list = msg.headers() or []
                    headers_dict = {
                        safe_decode(k): self.__safe_decode(v)
                        for k, v in headers_list if k and v
                    }

                    tracer = trace.get_tracer(__name__)
                    ctx = extract(headers_dict)
                    token = attach(ctx)

                    with tracer.start_as_current_span("kafka-consume"):
                        processor(msg)

                    detach(token)
                    self.consumer.commit(asynchronous=False)
        finally:
            self.stop()

    def stop(self):
        self.consumer.close()

    def __safe_decode(value):
        return value.decode() if isinstance(value, bytes) else value

