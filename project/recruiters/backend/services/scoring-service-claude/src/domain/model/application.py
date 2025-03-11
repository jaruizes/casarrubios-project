from dataclasses import dataclass
from typing import Dict, Any, Optional


@dataclass
class Application:
    id: str
    position_id: int
    candidate_description: str
    requirements: Dict[str, str]
    experiences: Dict[str, Any]