from dataclasses import dataclass

from src.application.api.input.dto.application_analyzed_event_dto import ResumeAnalysisDTO

@dataclass
class ScoringDTO:
    score: float
    descScore: float
    requirementScore: float
    tasksScore: float
    timeSpent: float

    def __getitem__(self, item):
        return self[item]

@dataclass
class ApplicationScoredEventDTO:
    applicationId: str
    positionId: int
    analysis: ResumeAnalysisDTO
    scoring: ScoringDTO

    def __getitem__(self, item):
        return self[item]
