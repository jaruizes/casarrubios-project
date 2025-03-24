from dataclasses import dataclass, field
from typing import List, Optional


@dataclass
class Requirement:
    id: int
    key: str
    value: str
    description: str
    mandatory: bool


@dataclass
class Task:
    id: int
    description: str


@dataclass
class Benefit:
    id: int
    description: str


@dataclass
class Position:
    id: int
    title: str
    description: Optional[str]
    status: int
    created_at: str
    published_at: Optional[str]
    tags: Optional[str]
    requirements: List[Requirement] = field(default_factory=list)
    tasks: List[Task] = field(default_factory=list)
    benefits: List[Benefit] = field(default_factory=list)

    def get_requirements_summary(self):
        ## - Requirement: {key} / Level: {value} / Mandatory: {mandatory}
        return "\n".join([f"\t- Requirement: {req.key} / Level: {req.value} / Mandatory: {req.mandatory}"
                          for req in self.requirements])

    def get_tasks_summary(self):
        return "\n".join([f"\t- Task: {task.description}" for task in self.tasks])
