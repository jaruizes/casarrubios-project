from dataclasses import dataclass
from datetime import datetime
from typing import List, Optional
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
    explanation: str

    def __getitem__(self, item):
        return self[item]

@dataclass
class Candidate:
    candidate_id: UUID
    name: Optional[str]
    email: Optional[str]
    phone: Optional[str]
    cv: Optional[str]

    def __getitem__(self, item):
        return self[item]

@dataclass
class CandidateDetail(Candidate):
    analysis: ResumeAnalysis


@dataclass
class ApplicationDetail:
    application_id: UUID
    position_id: int
    candidate: CandidateDetail
    scoring: Scoring
    created_at: Optional[datetime] = None

    def __getitem__(self, item):
        return self[item]

@dataclass
class CandidateApplication:
    application_id: UUID
    position_id: int
    candidate: Candidate
    created_at: datetime

    def __getitem__(self, item):
        return self[item]

class PaginatedResult:
    def __init__(self, applications: List[CandidateApplication], total_elements: int):
        self.data = applications
        self.total_elements = total_elements