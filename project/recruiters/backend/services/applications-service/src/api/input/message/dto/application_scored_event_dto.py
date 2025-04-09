from dataclasses import dataclass
from typing import List


@dataclass
class SkillDTO:
    skill: str
    level: str

    def __getitem__(self, item):
        return self[item]

@dataclass
class ResumeAnalysisDTO:
    summary: str
    strengths: List[str]
    concerns: List[str]
    hardSkills: List[SkillDTO]
    softSkills: List[SkillDTO]
    keyResponsibilities: List[str]
    interviewQuestions: List[str]
    totalYearsExperience: int
    averagePermanency: float
    tags: List[str]

    def __getitem__(self, item):
        return self[item]

@dataclass
class ScoringDTO:
    score: float
    descScore: float
    requirementScore: float
    tasksScore: float
    timeSpent: float
    explanation: str

    def __getitem__(self, item):
        return self[item]

@dataclass
class ApplicationScoredEventDTO:
    applicationId: str
    candidateId: str
    positionId: int
    analysis: ResumeAnalysisDTO
    scoring: ScoringDTO

    def __getitem__(self, item):
        return self[item]
