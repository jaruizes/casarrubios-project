import logging
import sys
from pathlib import Path

import pytest
from testcontainers.postgres import PostgresContainer

from src.infrastructure.core.config import load_config
from src.infrastructure.db.sqlalchemy_connection import SQLAlchemyConnection

logging.basicConfig(
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG,
    handlers=[logging.StreamHandler(stream=sys.stdout)],
)

logger = logging.getLogger(__name__)


@pytest.fixture(scope="module")
def postgres_container(request):
    script = Path(__file__).parent / "sql" / "init.sql"
    postgres = (PostgresContainer("postgres:14")
                .with_env("POSTGRES_PORT", "5432")
                .with_env("POSTGRES_USER", "test")
                .with_env("POSTGRES_PASSWORD", "test")
                .with_env("POSTGRES_DB", "test")
                .with_volume_mapping(host=str(script), container=f"/docker-entrypoint-initdb.d/{script.name}"))

    postgres.start()

    def remove_container():
        postgres.stop()
    # request.addfinalizer(remove_container)

    logger.info(f"PostgreSQL container started: {postgres.get_connection_url()}")
    yield postgres
    postgres.stop()
    logger.info("PostgreSQL container stopped")


@pytest.fixture(scope="module")
def setup_environment_variables(postgres_container):
    import os
    os.environ["DB_HOST"] = postgres_container.get_container_host_ip()
    os.environ["DB_PORT"] = str(postgres_container.get_exposed_port(5432))

    yield

@pytest.fixture(scope="module")
def setup_config(setup_environment_variables):
    config = load_config()
    yield config

@pytest.fixture(scope="module", autouse=True)
def db_session(setup_config):
    db_connection = SQLAlchemyConnection(
        host="localhost",
        port=setup_config.port,
        user="test",
        password="test",
        database="test"
    )
    session_factory = db_connection.connect()
    db_session = session_factory()
    yield db_session
    db_connection.disconnect()