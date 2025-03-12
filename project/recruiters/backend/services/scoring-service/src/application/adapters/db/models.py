from sqlalchemy import Column, Integer, String, Text, Numeric, DateTime, Date, Boolean, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship

Base = declarative_base()


class Position(Base):
    __tablename__ = "positions"
    __table_args__ = {"schema": "recruiters"}  # Cambiado a minúsculas

    id = Column(Integer, primary_key=True)
    title = Column(String(255), nullable=False)
    description = Column(Text)
    status = Column(Numeric(2), nullable=False)
    created_at = Column(DateTime)
    published_at = Column(Date)
    tags = Column(Text)

    requirements = relationship("Requirement", back_populates="position",
                              foreign_keys="Requirement.position_id", cascade="all, delete-orphan")
    tasks = relationship("Task", back_populates="position",
                       foreign_keys="Task.position_id", cascade="all, delete-orphan")
    benefits = relationship("Benefit", back_populates="position",
                          foreign_keys="Benefit.position_id", cascade="all, delete-orphan")


class Requirement(Base):
    __tablename__ = "requirements"
    __table_args__ = {"schema": "recruiters"}  # Cambiado a minúsculas

    id = Column(Integer, primary_key=True)
    position_id = Column(Integer, ForeignKey("recruiters.positions.id", ondelete="CASCADE"), nullable=False)
    key = Column(String(255), nullable=False)
    value = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    mandatory = Column(Boolean, nullable=False)

    position = relationship("Position", back_populates="requirements",
                          foreign_keys=[position_id])


class Task(Base):
    __tablename__ = "tasks"
    __table_args__ = {"schema": "recruiters"}  # Cambiado a minúsculas

    id = Column(Integer, primary_key=True)
    position_id = Column(Integer, ForeignKey("recruiters.positions.id", ondelete="CASCADE"), nullable=False)
    description = Column(Text, nullable=False)

    position = relationship("Position", back_populates="tasks",
                          foreign_keys=[position_id])


class Benefit(Base):
    __tablename__ = "benefits"
    __table_args__ = {"schema": "recruiters"}  # Cambiado a minúsculas

    id = Column(Integer, primary_key=True)
    position_id = Column(Integer, ForeignKey("recruiters.positions.id", ondelete="CASCADE"), nullable=False)
    description = Column(Text, nullable=False)

    position = relationship("Position", back_populates="benefits",
                          foreign_keys=[position_id])