import pytest
from fastapi.testclient import TestClient

from src.adapters.db.models import Application
from src.infrastructure.app.app import app

pytestmark = pytest.mark.asyncio

client = TestClient(app)

# @pytest.fixture(scope="module", autouse=True)
# def setup_db(test_db):
#     app.dependency_overrides[get_db] = test_db

def test_get_applications_empty():
    response = client.get("/applications?positionId=1&pageSize=5&page=10")
    assert response.status_code == 200
    assert response.json()["applications"] == []
    assert response.json()["totalElements"] == 22

def test_get_application_by_id_right(db_session):
    application_stored: Application = db_session.query(Application).filter(Application.position_id == 1).first()
    assert application_stored is not None, "Error getting application with id 1 from the database"

    response = client.get("/applications/" + str(application_stored.id))

    assert response.status_code == 200
    assert response.json() == {
        "candidate": {
            "name": application_stored.name,
            "email": application_stored.email,
            "phone": application_stored.phone,
        },
        "positionId": application_stored.position_id,
        "applicationId": str(application_stored.id),
        "creationDate": application_stored.created_at.isoformat(),
        "cvFile": application_stored.cv,
    }

def test_get_application_by_id_not_found():
    response = client.get("/applications/550e8400-e29b-41d4-a716-446655440000")
    assert response.status_code == 404


