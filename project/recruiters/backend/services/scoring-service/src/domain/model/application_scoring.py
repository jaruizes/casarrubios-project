from dataclasses import dataclass

from src.domain.model.application_analysis import ResumeAnalysis


@dataclass
class Scoring:
    application_id: str
    score: float
    desc_score: float
    requirement_score: float
    tasks_score: float
    time_spent: float
    explanation: str

    def __getitem__(self, item):
        return self[item]

@dataclass
class ApplicationScoring:
    application_id: str
    candidate_id: str
    position_id: int
    analysis: ResumeAnalysis
    scoring: Scoring

    def __getitem__(self, item):
        return self[item]


