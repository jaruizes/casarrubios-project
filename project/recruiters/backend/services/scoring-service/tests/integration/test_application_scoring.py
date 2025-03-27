import json
import logging
import os
from datetime import datetime
from typing import Any

import pytest

from src.application.api.output.dto.application_scored_event_dto import ApplicationScoredEventDTO
from src.infrastructure.kafka.kafka_producer import KafkaProducer

logger = logging.getLogger(__name__)
pytestmark = pytest.mark.asyncio


@pytest.mark.asyncio
async def test_score(setup_e2e, kafka_container, setup_topics, wait_for_result_in_kafka):
    producer = KafkaProducer(bootstrap_servers=kafka_container)
    application_analysed_event = __build_application_analysed_event_2()
    input_topic = os.getenv("KAFKA_INPUT_TOPIC")
    producer.send(input_topic, application_analysed_event, application_analysed_event['applicationId'])

    result_topic = os.getenv("KAFKA_OUTPUT_TOPIC")
    result = await wait_for_result_in_kafka(result_topic, timeout=20)
    assert result is not None
    __assert_event_received(result)


def __assert_event_received(message: Any):
    assert message is not None
    if hasattr(message, 'value'):
        application_scored_event = ApplicationScoredEventDTO(**json.loads(message.value()))
    else:
        application_scored_event = ApplicationScoredEventDTO(**message)

    assert application_scored_event.applicationId is not None
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
        "applicationId": f"test-app-{datetime.now().timestamp()}",
        "positionId": 1,
        "analysis": {
            "summary": "Luis Martínez García is a seasoned professional with over 12 years of experience in developing and implementing artificial intelligence solutions, with a recent focus on Generative AI. He is a recognized speaker at international events on AI, emphasizing how these technologies are transforming industries. Passionate about research and practical applications of advanced AI models, his goal is to lead innovative projects that promote the responsible adoption of artificial intelligence in businesses and organizations. His expertise includes machine learning and deep learning model design, along with proficiency in frameworks such as TensorFlow, PyTorch, and Hugging Face. Additionally, he has experience in cloud data management and MLOps, and is knowledgeable about AI ethics and regulation.",
            "strengths": [
                "Over 12 years of experience in AI solutions development.",
                "Expertise in Generative AI and NLP.",
                "Recognized speaker at international AI conferences.",
                "Strong background in machine learning and deep learning.",
                "Experience in MLOps and cloud environments."
            ],
            "concerns": [
                "Limited experience outside AI and machine learning.",
                "No formal management experience mentioned.",
                "Potential overemphasis on Generative AI despite broad AI experience.",
                "Focus primarily on technical skills with less emphasis on soft skills.",
                "May require upskilling in emerging AI regulations."
            ],
            "hardSkills": [
                {"skill": "Machine Learning", "level": "Expert"},
                {"skill": "Deep Learning", "level": "Expert"},
                {"skill": "Machine Learning", "level": "Expert"},
                {"skill": "Generative AI", "level": "Expert"},
                {"skill": "Natural Language Processing (NLP)", "level": "Expert"},
                {"skill": "MLOps", "level": "Expert"}
            ],
            "softSkills": [
                {"skill": "Communication", "level": "Expert"},
                {"skill": "Leadership", "level": "Expert"},
                {"skill": "Teamwork", "level": "Expert"},
                {"skill": "Problem-solving", "level": "Expert"},
                {"skill": "Research", "level": "Expert"}
            ],
            "keyResponsibilities": [
                "Lead development projects based on Generative AI for various sectors.",
                "Create custom models for text and image generation.",
                "Implement AI systems in production platforms using Kubernetes and Docker.",
                "Develop advanced machine learning algorithms for predictive analysis.",
                "Research NLP models and contribute to scientific publications.",
                "Supervise research projects in collaboration with universities.",
                "Design and maintain data pipelines for machine learning projects.",
                "Train models for classification and data analysis."
            ],
            "interviewQuestions": [
                "Can you describe a successful project you led in Generative AI?",
                "How do you approach the ethical considerations in AI?",
                "What challenges have you faced when deploying AI solutions in production?",
                "How do you keep your skills updated in the rapidly evolving AI landscape?",
                "Can you provide an example of how you've collaborated with cross-functional teams?"
            ],
            "totalYearsExperience": 12,
            "averagePermanency": 3,
            "tags": [
                "Artificial Intelligence",
                "Generative AI",
                "Machine Learning",
                "Deep Learning",
                "NLP",
                "MLOps"
            ]
        }
    }

def __build_application_analysed_event_2():
    return {
        "applicationId": f"test-app-{datetime.now().timestamp()}",
        "positionId": 7,
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
        }
    }