from typing import List

from src.adapters.db.models import Application


class PaginatedResult:
    def __init__(self, applications: List[Application], total_elements: int):
        self.data = applications
        self.total_elements = total_elements