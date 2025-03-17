import io
import json
import logging
import uuid
from datetime import datetime
from pathlib import Path
from uuid import UUID
from sqlalchemy.sql import text

import pytest
from fastapi.testclient import TestClient

from src.adapters.db.models import Application
from src.api.input.rest.dto.models import ApplicationDTO
from src.infrastructure.app.app import app

client = TestClient(app)

logger = logging.getLogger(__name__)

def test_get_applications_empty(setup_config):
    response = client.get("/applications?positionId=1&pageSize=5&page=10")
    assert response.status_code == 200
    assert response.json()["applications"] == []
    assert response.json()["totalElements"] == 22

def test_get_application_by_id_right(db_session):
    application_stored: Application = db_session.query(Application).filter(Application.position_id == 1).first()
    assert application_stored is not None, "Error getting application with id 1 from the database"

    response = client.get("/applications/" + str(application_stored.id))

    assert response.status_code == 200
    application_dto = ApplicationDTO(**response.json())
    __assert_basic_data(application_dto, application_stored)

def test_get_application_by_id_not_found():
    response = client.get("/applications/550e8400-e29b-41d4-a716-446655440000")
    assert response.status_code == 404

def test_get_cv_file(setup_config, minio_setup, db_session):
    application_stored: Application = db_session.query(Application).filter(Application.position_id == 1).first()
    assert application_stored is not None, "Error getting application with id 1 from the database"

    cv_file_test_bytes = __upload_cv_fake(str(application_stored.id), minio_setup)
    response = client.get(f"/applications/{application_stored.id}/cv")

    assert response.status_code == 200
    assert response.headers["Content-Type"] == "application/pdf"
    assert response.content == cv_file_test_bytes

@pytest.mark.asyncio
async def test_process_application_scored_event(db_session, setup_producer, setup_config, event_processor, setup_e2e):
    # Obtener una aplicación existente
    application_stored: Application = db_session.query(Application).filter(Application.position_id == 1).first()
    application_id = application_stored.id
    position_id = application_stored.position_id
    
    # Verificar que no hay análisis previo
    initial_check = db_session.execute(
        text('SELECT * FROM recruiters.resume_analysis WHERE application_id = :application_id'),
        {"application_id": application_id}
    ).fetchall()
    assert len(initial_check) == 0, "Should not have resume analysis before the test"
    
    # Crear el evento
    topic = setup_config.input_topic
    application_scored_event = __build_applycation_scored_event(application_id, position_id)
    
    # Publicar y procesar el evento directamente
    from tests.test_utils import publish_and_process_event
    logging.info(f"Publishing and processing event for application_id {application_id}")
    success = publish_and_process_event(
        producer=setup_producer, 
        topic=topic, 
        event_data=application_scored_event,
        processor=event_processor
    )
    assert success, "Failed to publish and process event"
    
    # Verificar que el evento fue procesado correctamente
    processed = event_processor.check_application_processed(application_id)
    assert processed, f"Event for application_id {application_id} was not processed"
    
    # Verificar que la aplicación se puede obtener a través de la API
    response = client.get("/applications/" + str(application_id))
    assert response.status_code == 200
    
    # Verificar que los datos de análisis y puntuación están presentes
    application_dto = ApplicationDTO(**response.json())
    __assert_basic_data(application_dto, application_stored)
    __assert_analysis_and_scoring_data(application_dto, application_scored_event)


def __upload_cv_fake(application_id: str, minio_setup):
    cv_file_test_path = Path(__file__).parent / "resources" / "cv_fake.pdf"
    cv_file_test_bytes = cv_file_test_path.open("rb").read()
    file_size = len(cv_file_test_bytes)
    minio_setup.put_object("test", str(application_id), io.BytesIO(cv_file_test_bytes), file_size, content_type="application/pdf")

    return cv_file_test_bytes

def __assert_basic_data(application_dto, application_stored):
    assert application_dto is not None
    assert str(application_dto.applicationId) == str(application_stored.id)
    assert application_dto.candidate.name == application_stored.name
    assert application_dto.candidate.email == application_stored.email
    assert application_dto.candidate.phone == application_stored.phone
    assert application_dto.positionId == application_stored.position_id
    assert application_dto.cvFile == application_stored.cv
    assert application_dto.creationDate == application_stored.created_at.isoformat()

def __assert_analysis_and_scoring_data(application_dto, application_scored_event):
    assert application_dto.analysis is not None
    assert application_dto.analysis.summary == application_scored_event["analysis"]["summary"]
    assert application_dto.analysis.strengths == application_scored_event["analysis"]["strengths"]
    assert application_dto.analysis.concerns == application_scored_event["analysis"]["concerns"]

    for skill in application_dto.analysis.hardSkills:
        assert skill.skill in [s["skill"] for s in application_scored_event["analysis"]["hardSkills"]]
        assert skill.level in [s["level"] for s in application_scored_event["analysis"]["hardSkills"]]

    for skill in application_dto.analysis.softSkills:
        assert skill.skill in [s["skill"] for s in application_scored_event["analysis"]["softSkills"]]
        assert skill.level in [s["level"] for s in application_scored_event["analysis"]["softSkills"]]

    assert application_dto.analysis.keyResponsibilities == application_scored_event["analysis"]["keyResponsibilities"]
    assert application_dto.analysis.interviewQuestions == application_scored_event["analysis"]["interviewQuestions"]
    assert application_dto.analysis.totalYearsExperience == application_scored_event["analysis"]["totalYearsExperience"]
    assert application_dto.analysis.averagePermanency == application_scored_event["analysis"]["averagePermanency"]
    assert application_dto.analysis.tags == application_scored_event["analysis"]["tags"]

    assert application_dto.scoring is not None
    assert application_dto.scoring.score == application_scored_event["scoring"]["score"]
    assert application_dto.scoring.descScore == application_scored_event["scoring"]["descScore"]
    assert application_dto.scoring.requirementScore == application_scored_event["scoring"]["requirementScore"]
    assert application_dto.scoring.tasksScore == application_scored_event["scoring"]["tasksScore"]
    assert application_dto.scoring.timeSpent == application_scored_event["scoring"]["timeSpent"]

def __publish_application_scored_event(producer, topic, application_id, position_id):
    application_scored_event = __build_applycation_scored_event(application_id, position_id)
    try:
        key = application_scored_event.get("applicationId")
        event_bytes = json.dumps(application_scored_event).encode("utf-8")
        producer.produce(topic, key=key, value=event_bytes)
        producer.flush()
    except Exception as e:
        logger.exception(f"Error sending event to {topic}: {str(e)}")

    return application_scored_event

def __build_applycation_scored_event(application_id: str, position_id: int):
    return {
        "applicationId": str(application_id),
        "positionId": position_id,
        "analysis": {
            "summary": "Jose A. is a passionate technology expert with over 20 years of experience in strategic and technical roles across various sectors. He possesses extensive knowledge in Frontend, Backend, Cloud, APIs, and Databases, enabling him to manage complex architectures in high-performance enterprise environments. He emphasizes an end-to-end vision in solution delivery, ensuring that solutions are not only current but also scalable and adaptable for future needs. With experience in mentoring less experienced professionals, he actively contributes to the tech community through articles and presentations at events like Commit Conf and OpenExpo. His role as a Principal Solutions Architect at Paradigma Digital involves participation in pre-sales, designing end-to-end architectures, developing PoCs for new technologies, and standardizing best practices across the organization. He has led significant projects such as the digital transformation of document management for Mercadona and the regulatory adaptation for BME. His technical expertise spans a wide array of technologies, including Angular, Spring, Kubernetes, AWS, and GCP, and he holds multiple relevant certifications. José is committed to continuous learning and sharing knowledge, reflected in his publications and talks on modern architecture.",
            "strengths": [
                "Over 20 years of experience in technology roles.",
                "Expertise in end-to-end solution architecture.",
                "Strong mentoring and leadership skills.",
                "Active contributor to the tech community through articles and presentations.",
                "Proficient in a wide range of modern technologies and methodologies."
            ],
            "concerns": [
                "Limited recent experience in purely software development roles.",
                "Heavy focus on architecture may reduce hands-on coding experience.",
                "May require adaptation to rapidly changing technology trends.",
                "Potential gaps in experience with very niche technologies.",
                "Less emphasis on purely business-oriented roles."
            ],
            "hardSkills": [
                {"skill": "Angular", "level": "Avanzado"},
                {"skill": "Spring", "level": "Avanzado"},
                {"skill": "Kubernetes", "level": "Avanzado"},
                {"skill": "Cloud", "level": "Avanzado"},
                {"skill": "API Design", "level": "Avanzado"},
                {"skill": "AWS", "level": "Avanzado"},
                {"skill": "Software Architecture", "level": "Avanzado"},
                {"skill": "Terraform", "level": "Avanzado"},
                {"skill": "Kafka", "level": "Avanzado"},
                {"skill": "Java", "level": "Avanzado"},
                {"skill": "Microservices", "level": "Avanzado"},
                {"skill": "CI/CD", "level": "Avanzado"}
            ],
            "softSkills": [
                {"skill": "Communication", "level": "Expert"},
                {"skill": "Leadership", "level": "Expert"},
                {"skill": "Mentoring", "level": "Expert"},
                {"skill": "Problem-solving", "level": "Expert"},
                {"skill": "Team collaboration", "level": "Expert"}
            ],
            "keyResponsibilities": [
                "Lead architectural design and development of end-to-end solutions.",
                "Conduct pre-sales assessments and client engagements.",
                "Develop and standardize best practices for development across the organization.",
                "Mentor junior staff and support their professional growth.",
                "Participate in technical events and publish related articles.",
                "Manage technical aspects of significant projects from inception to execution.",
                "Design and develop Proofs of Concept (PoCs) for new technologies.",
                "Oversee the technical implementation and provide functional support."
            ],
            "interviewQuestions": [
                "Can you describe your approach to developing end-to-end architectures?",
                "What strategies do you use for mentoring junior team members?",
                "How do you keep up with new technologies and trends in the industry?",
                "Can you discuss a challenging project you managed and how you overcame obstacles?",
                "What role do you believe documentation and standardization play in your projects?"
            ],
            "totalYearsExperience": 20,
            "averagePermanency": 2.5,
            "tags": [
                "Microservices",
                "Architecture",
                "Leadership",
                "Java Expert",
                "Cloud Solutions"
            ]
        },
        "scoring": {
            "score" : 82.3,
            "descScore" : 80.0,
            "requirementScore" : 85.0,
            "tasksScore" : 80.0,
            "timeSpent" : 60.4
        }
    }
