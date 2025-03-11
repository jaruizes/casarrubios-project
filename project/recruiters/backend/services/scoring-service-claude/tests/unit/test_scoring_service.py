import pytest
from src.domain.model.position import Position, Requirement, Task, Benefit
from src.domain.model.application import Application
from src.domain.services.scoring_service import ScoringService


@pytest.fixture
def scoring_service():
    """Crea un servicio de puntuación para pruebas"""
    return ScoringService()

@pytest.fixture
def sample_position():
    """Crea una posición de ejemplo para pruebas"""
    position = Position(
        id=1,
        title="Python Developer",
        description="We are looking for a Python developer with experience in web development",
        status=1,
        created_at="2023-01-01T00:00:00",
        published_at="2023-01-02",
        tags="python,backend,api",
        requirements=[
            Requirement(id=1, key="programming", value="python", description="Python programming", mandatory=True),
            Requirement(id=2, key="framework", value="fastapi", description="FastAPI framework", mandatory=True),
            Requirement(id=3, key="database", value="postgresql", description="PostgreSQL database", mandatory=False)
        ],
        tasks=[
            Task(id=1, description="Develop REST APIs with Python"),
            Task(id=2, description="Write unit and integration tests")
        ],
        benefits=[
            Benefit(id=1, description="Flexible working hours")
        ]
    )
    return position


@pytest.fixture
def sample_application():
    """Crea una aplicación de ejemplo para pruebas"""
    application = Application(
        id="app-123",
        position_id=1,
        candidate_description="I am a Python developer with 5 years of experience in web development. I have worked with FastAPI and Django frameworks.",
        requirements={
            "programming": "python, javascript",
            "framework": "fastapi, django",
            "database": "postgresql, mongodb"
        },
        experiences={
            "previous_job": "Developed REST APIs using Python and FastAPI",
            "education": "Computer Science degree",
            "projects": "Created a test automation framework"
        }
    )
    return application


class TestScoringService:
    """Pruebas unitarias para el servicio de puntuación"""

    def test_calculate_description_score(self, scoring_service, sample_position, sample_application):
        """Verifica que la puntuación de descripción sea calculada correctamente"""
        score = scoring_service._calculate_description_score(sample_position, sample_application)

        # La puntuación debe ser un valor entre 0 y 100
        assert 0 <= score <= 100

        # La descripción del candidato contiene palabras clave relevantes, debería tener una puntuación alta
        assert score > 50

    def test_calculate_requirements_score(self, scoring_service, sample_position, sample_application):
        """Verifica que la puntuación de requisitos sea calculada correctamente"""
        score = scoring_service._calculate_requirements_score(sample_position, sample_application)

        # La puntuación debe ser un valor entre 0 y 100
        assert 0 <= score <= 100

        # El candidato cumple con todos los requisitos, debería tener una puntuación alta
        assert score > 70

    def test_calculate_tasks_score(self, scoring_service, sample_position, sample_application):
        """Verifica que la puntuación de tareas sea calculada correctamente"""
        score = scoring_service._calculate_tasks_score(sample_position, sample_application)

        # La puntuación debe ser un valor entre 0 y 100
        assert 0 <= score <= 100

        # La experiencia del candidato está relacionada con las tareas, debería tener una puntuación decente
        assert score > 40

    def test_calculate_scores(self, scoring_service, sample_position, sample_application):
        """Verifica que todas las puntuaciones sean calculadas correctamente"""
        final_score, desc_score, req_score, task_score, time_spent = scoring_service.calculate_scores(
            sample_position, sample_application
        )

        # Todas las puntuaciones deben ser valores entre 0 y 100
        assert 0 <= final_score <= 100
        assert 0 <= desc_score <= 100
        assert 0 <= req_score <= 100
        assert 0 <= task_score <= 100

        # El tiempo de procesamiento debe ser un valor positivo
        assert time_spent >= 0.0

        # La puntuación final debe ser una media ponderada de las otras puntuaciones
        # (0.3 * desc_score) + (0.5 * req_score) + (0.2 * task_score)
        expected_score = round((0.3 * desc_score) + (0.5 * req_score) + (0.2 * task_score), 2)
        assert final_score == expected_score

    def test_empty_position_data(self, scoring_service):
        """Verifica el comportamiento con datos vacíos"""
        empty_position = Position(
            id=1,
            title="Empty Position",
            description=None,
            status=1,
            created_at="2023-01-01T00:00:00",
            published_at=None,
            tags=None
        )

        application = Application(
            id="app-123",
            position_id=1,
            candidate_description="Some description",
            requirements={},
            experiences={}
        )

        final_score, desc_score, req_score, task_score, time_spent = scoring_service.calculate_scores(
            empty_position, application
        )

        # Con datos vacíos, los valores por defecto deberían aplicarse
        assert desc_score == 0.5 * 100  # Convertido a escala 0-100
        assert req_score == 50.0
        assert task_score == 50.0