from dataclasses import dataclass
from typing import Dict, Any, Optional


@dataclass
class ApplicationAnalysedEvent:
    applicationId: str
    positionId: int
    candidateDescription: str
    requirements: Dict[str, str]
    experiences: Dict[str, Any]


@dataclass
class ApplicationScoredEvent:
    applicationId: str
    score: float
    descScore: float
    requirementScore: float
    tasksScore: float
    timeSpent: float