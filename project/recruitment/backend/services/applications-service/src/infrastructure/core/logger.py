import logging
import sys

logging.basicConfig(
    format="%(asctime)s - %(levelname)s - %(message)s",
    level=logging.DEBUG,
    handlers=[logging.StreamHandler(stream=sys.stdout)],
)

logger = logging.getLogger()
