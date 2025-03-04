from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy import Column, String, Integer, DateTime
from sqlalchemy.sql import func
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Application(Base):
    __tablename__ = "applications"
    __table_args__ = {"schema": "recruiters"}

    id = Column(UUID(as_uuid=True), primary_key=True, name="id")
    name = Column(String, nullable=False, name="name")
    email = Column(String, nullable=False, name="email")
    phone = Column(String, nullable=False, name="phone")
    cv = Column(String, nullable=False, name="cv")
    position_id = Column(Integer, nullable=False, name="position_id")
    created_at = Column(DateTime, default=func.now(), name="created_at")