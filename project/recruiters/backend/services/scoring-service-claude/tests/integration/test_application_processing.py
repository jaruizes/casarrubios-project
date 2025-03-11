# tests/integration/test_application_processing.py
import os
import threading
import time
from typing import Any

import pytest
import json
import asyncio
from datetime import datetime
import logging

from src.domain.model.events import ApplicationScoredEvent
from src.infrastructure.kafka.kafka_consumer import KafkaConsumer
from src.infrastructure.kafka.kafka_producer import KafkaProducer
from src.main import main

logger = logging.getLogger(__name__)
pytestmark = pytest.mark.asyncio

# def test_position_repository(position_repository):
#     """
#     Prueba la recuperación de datos de posición desde la base de datos
#     utilizando SQLAlchemy
#     """
#     # Obtener posición de prueba (ID 1)
#     position = position_repository.get_position_by_id(1)
#
#     # Verificar que los datos sean correctos
#     assert position is not None
#     assert position.id == 1
#     assert position.title is not None
#     assert len(position.requirements) > 0
#     assert len(position.tasks) > 0
#     assert len(position.benefits) > 0
#
#     # Verificar que se recuperen los requisitos obligatorios
#     mandatory_reqs = [req for req in position.requirements if req.mandatory]
#     assert len(mandatory_reqs) > 0
#
#     # Imprimir información para debug
#     logger.info(f"Position found: {position.title}")
#     logger.info(f"Requirements: {len(position.requirements)}")
#     logger.info(f"Tasks: {len(position.tasks)}")
#     logger.info(f"Benefits: {len(position.benefits)}")


@pytest.mark.asyncio
async def test_score(setup_e2e, kafka_container, setup_topics, wait_for_result_in_kafka):
    producer = KafkaProducer(bootstrap_servers=kafka_container)
    application_event = {
        "applicationId": f"test-app-{datetime.now().timestamp()}",
        "positionId": 1,
        "candidateDescription": "I am a Python developer with experience in FastAPI and PostgreSQL. I have been working with REST APIs for the last 4 years.",
        "requirements": {
            "programming": "python",
            "framework": "fastapi",
            "database": "postgresql",
            "tools": "git"
        },
        "experiences": {
            "previous_job": "Developed APIs using Python",
            "education": "Computer Science degree",
            "projects": "RESTful API development"
        }
    }
    input_topic = os.getenv("KAFKA_INPUT_TOPIC")
    producer.send(input_topic, application_event, application_event['applicationId'])

    result_topic = os.getenv("KAFKA_OUTPUT_TOPIC")
    result = await wait_for_result_in_kafka(result_topic, timeout=20)
    assert result is not None
    __assert_event_received(result)

    # time.sleep(60)
    # result_topic = os.getenv("KAFKA_RESULT_TOPIC")
    # consumer = KafkaConsumer(bootstrap_servers=kafka_container,
    #                          topic=result_topic,
    #                          group_id='assert-group')
    #
    # consumer.start(__assert_event_received)


def __assert_event_received(message: Any):
    assert message is not None
    if hasattr(message, 'value'):
        application_scored_event = ApplicationScoredEvent(**json.loads(message.value()))
    else:
        application_scored_event = ApplicationScoredEvent(**message)

    assert application_scored_event.applicationId is not None
    assert application_scored_event.score >= 0.0
    assert application_scored_event.tasksScore >= 0.0
    assert application_scored_event.requirementScore >= 0.0
    assert application_scored_event.descScore >= 0.0
    assert application_scored_event.timeSpent >= 0.0









# @pytest.mark.asyncio
# async def test_process_application_use_case(process_application_use_case, kafka_admin_client, wait_for_result_in_kafka):
#     """
#     Prueba el procesamiento completo de una aplicación:
#     1. Publica un evento ApplicationAnalysedEvent
#     2. El caso de uso procesa la aplicación
#     3. Verifica que se emita un evento ApplicationScoredEvent correcto
#     """
#     # use_case = await process_application_use_case if asyncio.iscoroutine(process_application_use_case) else process_application_use_case
#
#
#     # Evento de aplicación analizada para pruebas
#     application_event = {
#         "applicationId": f"test-app-{datetime.now().timestamp()}",
#         "positionId": 1,  # ID de la posición de prueba
#         "candidateDescription": "I am a Python developer with experience in FastAPI and PostgreSQL. I have been working with REST APIs for the last 4 years.",
#         "requirements": {
#             "programming": "python",
#             "framework": "fastapi",
#             "database": "postgresql",
#             "tools": "git"
#         },
#         "experiences": {
#             "previous_job": "Developed APIs using Python",
#             "education": "Computer Science degree",
#             "projects": "RESTful API development"
#         }
#     }
#
#     # Procesar directamente el evento con el caso de uso
#     process_application_use_case.execute(application_event)
#
#     # Esperar a que el resultado sea publicado en Kafka
#     result = await wait_for_result_in_kafka("application-scored-events", timeout=50)
#
#     # Verificar que el resultado sea correcto
#     assert result is not None
#     assert result["applicationId"] == application_event["applicationId"]
#
#     # Verificar que contenga todas las puntuaciones
#     assert "score" in result
#     assert "descScore" in result
#     assert "requirementScore" in result
#     assert "tasksScore" in result
#     assert "timeSpent" in result
#
#     # Verificar que las puntuaciones estén en el rango correcto
#     assert 0 <= result["score"] <= 100
#     assert 0 <= result["descScore"] <= 100
#     assert 0 <= result["requirementScore"] <= 100
#     assert 0 <= result["tasksScore"] <= 100
#
#     # Verificar que el tiempo de procesamiento sea razonable
#     assert result["timeSpent"] > 0
#
#     logger.info(f"Application processed successfully. Score: {result['score']}")
#
#
# @pytest.mark.asyncio
# async def test_end_to_end_processing(kafka_container, kafka_admin_client, wait_for_result_in_kafka):
#     """
#     Prueba el procesamiento de principio a fin:
#     1. Publica un evento en el topic de entrada
#     2. Espera a que el evento sea procesado y un resultado sea publicado
#        en el topic de salida
#     """
#     from aiokafka import AIOKafkaProducer
#
#     # Crear un productor para publicar el evento de entrada
#     producer = AIOKafkaProducer(
#         bootstrap_servers=kafka_container,
#         value_serializer=lambda v: json.dumps(v).encode('utf-8')
#     )
#     await producer.start()
#
#     try:
#         # Evento de aplicación analizada para pruebas
#         application_event = {
#             "applicationId": f"test-app-e2e-{datetime.now().timestamp()}",
#             "positionId": 1,  # ID de la posición de prueba
#             "candidateDescription": "Experienced Python developer with 5 years working on web applications and REST APIs. Proficient in FastAPI and PostgreSQL.",
#             "requirements": {
#                 "programming": "python",
#                 "framework": "fastapi",
#                 "database": "postgresql, mongodb",
#                 "tools": "git, docker"
#             },
#             "experiences": {
#                 "previous_job": "Developed and maintained RESTful APIs using Python and FastAPI",
#                 "education": "Master's in Computer Science",
#                 "projects": "Created test automation frameworks and CI/CD pipelines"
#             }
#         }
#
#         # Publicar evento en el topic de entrada
#         await producer.send_and_wait(
#             "application-analysed-events",
#             application_event,
#             key=application_event["applicationId"].encode('utf-8')
#         )
#
#         logger.info(f"Test event published to Kafka: {application_event['applicationId']}")
#
#         # Esperar a que el resultado sea publicado en el topic de salida
#         result = await wait_for_result_in_kafka("application-scored-events", timeout=10)
#
#         # Verificar que el resultado sea correcto
#         assert result is not None
#         assert result["applicationId"] == application_event["applicationId"]
#
#         # Verificar que contenga todas las puntuaciones
#         assert "score" in result
#         assert "descScore" in result
#         assert "requirementScore" in result
#         assert "tasksScore" in result
#         assert "timeSpent" in result
#
#         # Verificar que las puntuaciones estén en el rango correcto
#         assert 0 <= result["score"] <= 100
#         assert 0 <= result["descScore"] <= 100
#         assert 0 <= result["requirementScore"] <= 100
#         assert 0 <= result["tasksScore"] <= 100
#
#         # El candidato parece ser un buen match, por lo que la puntuación debería ser alta
#         assert result["score"] > 70
#
#         logger.info(f"End-to-end test successful. Score: {result['score']}")
#
#     finally:
#         await producer.stop()