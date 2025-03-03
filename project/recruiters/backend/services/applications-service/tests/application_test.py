import pytest
from fastapi.testclient import TestClient

from app.db.models import Application
from app.main import app
from app.db.database import get_db
from tests.database_test import test_db

client = TestClient(app)

@pytest.fixture(scope="module", autouse=True)
def setup_db(test_db):
    app.dependency_overrides[get_db] = test_db

@pytest.fixture(scope="module")
def db_session(test_db):
    db = next(test_db())
    yield db
    db.close()


def test_get_applications_empty():
    response = client.get("/applications?positionId=1&pageSize=5&page=10")
    assert response.status_code == 200
    assert response.json()["applications"] == []
    assert response.json()["totalElements"] == 22

def test_get_application_by_id_right(db_session):
    response = client.get("/applications/1")

    application = db_session.query(Application).filter(Application.id == 1).first()
    assert application is not None, "Error getting application with id 1 from the database"

    assert response.status_code == 200
    assert response.json() == {
        "candidate": {
            "name": application.name,
            "email": application.email,
            "phone": application.phone,
        },
        "positionId": application.position_id,
        "applicationId": 1,
        "creationDate": application.created_at.isoformat(),
        "cvFile": application.cv,
    }

def test_get_application_by_id_not_found():
    response = client.get("/applications/9999")
    assert response.status_code == 404
    assert response.json() == {"detail": "Application with ID 9999 not found"}


