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
class ApplicationAnalysedEventDTO:
    applicationId: str
    candidateId: str
    positionId: int
    analysis: ResumeAnalysisDTO

    def __getitem__(self, item):
        return self[item]