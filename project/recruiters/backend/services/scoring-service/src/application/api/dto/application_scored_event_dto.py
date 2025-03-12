from dataclasses import dataclass


@dataclass
class ApplicationScoredEvent:
    applicationId: str
    score: float
    descScore: float
    requirementScore: float
    tasksScore: float
    timeSpent: float