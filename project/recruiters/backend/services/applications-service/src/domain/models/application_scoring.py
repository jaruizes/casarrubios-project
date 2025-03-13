from dataclasses import dataclass
from typing import List
from uuid import UUID


@dataclass
class Skill:
    skill: str
    level: str

    def __getitem__(self, item):
        return self[item]

@dataclass
class ResumeAnalysis:
    summary: str
    strengths: List[str]
    concerns: List[str]
    hard_skills: List[Skill]
    soft_skills: List[Skill]
    key_responsibilities: List[str]
    interview_questions: List[str]
    total_years_xperience: int
    average_permanency: float
    tags: List[str]

    def __getitem__(self, item):
        return self[item]

@dataclass
class Scoring:
    application_id: str
    score: float
    desc_score: float
    requirement_score: float
    tasks_score: float
    time_spent: float

    def __getitem__(self, item):
        return self[item]

@dataclass
class ApplicationScoring:
    application_id: UUID
    position_id: int
    analysis: ResumeAnalysis
    scoring: Scoring

    def __getitem__(self, item):
        return self[item]