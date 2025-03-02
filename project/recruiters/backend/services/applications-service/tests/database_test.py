import pytest
from testcontainers.postgres import PostgresContainer
from sqlalchemy import create_engine, text
from sqlalchemy.orm import sessionmaker

from app.core.config import settings
from app.db.database import Base

postgres = PostgresContainer("postgres:14").with_env("POSTGRES_USER", settings.DATABASE_USER)\
                                        .with_env("POSTGRES_PASSWORD", settings.DATABASE_PASSWORD)\
                                        .with_env("POSTGRES_DB", settings.DATABASE_NAME)

@pytest.fixture(scope="module", autouse=True)
def test_db(request):
    postgres.start()

    def remove_container():
        postgres.stop()

    request.addfinalizer(remove_container)
    engine = create_engine(postgres.get_connection_url())
    testing_session_local = sessionmaker(autocommit=False, autoflush=False, bind=engine)

    Base.metadata.create_all(bind=engine)

    with engine.connect() as connection:
        with open("sql/init.sql", "r") as file:
            connection.execute(text(file.read()))
            connection.commit()

    def override_get_db():
        db = testing_session_local()
        try:
            yield db
        finally:
            db.close()

    yield override_get_db
