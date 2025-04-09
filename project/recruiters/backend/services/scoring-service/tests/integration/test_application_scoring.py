import json
import logging
import os
import uuid
from typing import Any

import pytest

from src.application.api.output.dto.application_scored_event_dto import ApplicationScoredEventDTO
from src.infrastructure.kafka.kafka_producer import KafkaProducer

logger = logging.getLogger(__name__)
pytestmark = pytest.mark.asyncio


@pytest.mark.asyncio
async def test_score(setup_e2e, kafka_container, setup_topics, wait_for_result_in_kafka):
    producer = KafkaProducer(bootstrap_servers=kafka_container)
    application_analysed_event = __build_application_analysed_event()
    input_topic = os.getenv("KAFKA_INPUT_TOPIC")
    producer.send(input_topic, application_analysed_event, application_analysed_event['applicationId'])

    result_topic = os.getenv("KAFKA_OUTPUT_TOPIC")
    result = await wait_for_result_in_kafka(result_topic, timeout=200)
    assert result is not None
    __assert_event_received(result)


def __assert_event_received(message: Any):
    assert message is not None
    if hasattr(message, 'value'):
        application_scored_event = ApplicationScoredEventDTO(**json.loads(message.value()))
    else:
        application_scored_event = ApplicationScoredEventDTO(**message)

    assert application_scored_event.applicationId is not None
    assert application_scored_event.candidateId is not None
    assert application_scored_event.positionId is not None
    assert application_scored_event.analysis is not None
    assert application_scored_event.scoring is not None

    scoring = application_scored_event.scoring

    assert scoring['score'] >= 0.0
    assert scoring['tasksScore'] >= 0.0
    assert scoring['requirementScore'] >= 0.0
    assert scoring['descScore'] >= 0.0
    assert scoring['timeSpent'] >= 0.0
    assert scoring['explanation'] is not None

    logger.info(f"Time spent: {scoring['timeSpent']}")
    logger.info(f"Score: {scoring['score']}")
    logger.info(f"Desc score: {scoring['descScore']}")
    logger.info(f"Requirement score: {scoring['requirementScore']}")
    logger.info(f"Tasks score: {scoring['tasksScore']}")


def __build_application_analysed_event():
    return {
        "applicationId": f"{uuid.uuid4()}",
        "candidateId": f"{uuid.uuid4()}",
        "positionId": 7,
        "analysis": {
            "summary": "JA. is a passionate technology expert with over 20 years of experience in strategic and technical roles across various sectors. He possesses extensive knowledge in Frontend, Backend, Cloud, APIs, and Databases, enabling him to manage complex architectures in high-performance enterprise environments. He emphasizes an end-to-end vision in solution delivery, ensuring that solutions are not only current but also scalable and adaptable for future needs. With experience in mentoring less experienced professionals, he actively contributes to the tech community through articles and presentations at events like Commit Conf and OpenExpo. His role as a Principal Solutions Architect at Paradigma Digital involves participation in pre-sales, designing end-to-end architectures, developing PoCs for new technologies, and standardizing best practices across the organization. He has led significant projects such as the digital transformation of document management for Mercadona and the regulatory adaptation for BME. His technical expertise spans a wide array of technologies, including Angular, Spring, Kubernetes, AWS, and GCP, and he holds multiple relevant certifications. José is committed to continuous learning and sharing knowledge, reflected in his publications and talks on modern architecture.",
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
                {"skill": "Angular", "level": 2},
                {"skill": "Spring", "level": 2},
                {"skill": "Kubernetes", "level": 2},
                {"skill": "Cloud", "level": 2},
                {"skill": "API Design", "level": 2},
                {"skill": "AWS", "level": 2},
                {"skill": "Software Architecture", "level": 2},
                {"skill": "Terraform", "level": 2},
                {"skill": "Kafka", "level": 2},
                {"skill": "Java", "level": 2},
                {"skill": "Microservices", "level": 2},
                {"skill": "CI/CD", "level": 2}
            ],
            "softSkills": [
                {"skill": "Communication", "level": 3},
                {"skill": "Leadership", "level": 3},
                {"skill": "Mentoring", "level": 3},
                {"skill": "Problem-solving", "level": 3},
                {"skill": "Team collaboration", "level": 3}
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
        }
    }