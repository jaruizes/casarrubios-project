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