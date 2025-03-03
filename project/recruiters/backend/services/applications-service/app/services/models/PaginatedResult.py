from typing import List, Type

from app.db.models import Application


class PaginatedResult:
    def __init__(self, applications: List[Application], total_elements: int):
        self.data = applications
        self.total_elements = total_elements