from dataclasses import dataclass
from typing import List


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
class ApplicationAnalysis:
    application_id: str
    position_id: int
    analysis: ResumeAnalysis

    def __getitem__(self, item):
        return self[item]


