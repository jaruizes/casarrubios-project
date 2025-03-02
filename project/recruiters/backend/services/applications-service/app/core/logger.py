import logging

from app.core.config import settings

# Configuración del logger
logging.basicConfig(
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG,
)

logger = logging.getLogger("app_logger")
