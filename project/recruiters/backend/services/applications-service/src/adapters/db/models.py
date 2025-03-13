from sqlalchemy import Column, String, Integer, Float, ForeignKey, DateTime, Text, UUID
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import uuid

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

    # Relaciones
    resume_analysis = relationship("ResumeAnalysis", back_populates="application", uselist=False,
                                   cascade="all, delete-orphan")
    scoring = relationship("Scoring", back_populates="application", uselist=False, cascade="all, delete-orphan")
    strengths = relationship("Strength", back_populates="application", cascade="all, delete-orphan")
    concerns = relationship("Concern", back_populates="application", cascade="all, delete-orphan")
    hard_skills = relationship("HardSkill", back_populates="application", cascade="all, delete-orphan")
    soft_skills = relationship("SoftSkill", back_populates="application", cascade="all, delete-orphan")
    key_responsibilities = relationship("KeyResponsibility", back_populates="application", cascade="all, delete-orphan")
    interview_questions = relationship("InterviewQuestion", back_populates="application", cascade="all, delete-orphan")
    tags = relationship("Tag", back_populates="application", cascade="all, delete-orphan")


class ResumeAnalysis(Base):
    __tablename__ = "resume_analysis"
    __table_args__ = {"schema": "recruiters"}

    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), primary_key=True)
    summary = Column(Text)
    total_years_experience = Column(Integer)
    average_permanency = Column(Float)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="resume_analysis")


class Scoring(Base):
    __tablename__ = "scoring"
    __table_args__ = {"schema": "recruiters"}

    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), primary_key=True)
    score = Column(Float, nullable=False)
    desc_score = Column(Float, nullable=False)
    requirement_score = Column(Float, nullable=False)
    tasks_score = Column(Float, nullable=False)
    time_spent = Column(Float, nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="scoring")


class Strength(Base):
    __tablename__ = "strengths"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    strength = Column(Text, nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="strengths")


class Concern(Base):
    __tablename__ = "concerns"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    concern = Column(Text, nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="concerns")


class HardSkill(Base):
    __tablename__ = "hard_skills"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    skill = Column(String(255), nullable=False)
    level = Column(String(50), nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="hard_skills")


class SoftSkill(Base):
    __tablename__ = "soft_skills"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    skill = Column(String(255), nullable=False)
    level = Column(String(50), nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="soft_skills")


class KeyResponsibility(Base):
    __tablename__ = "key_responsibilities"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    responsibility = Column(Text, nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="key_responsibilities")


class InterviewQuestion(Base):
    __tablename__ = "interview_questions"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    question = Column(Text, nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="interview_questions")


class Tag(Base):
    __tablename__ = "tags"
    __table_args__ = {"schema": "recruiters"}

    id = Column(Integer, primary_key=True, autoincrement=True)
    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.applications.id"), nullable=False)
    tag = Column(String(255), nullable=False)
    created_at = Column(DateTime, default=func.now())

    # Relación
    application = relationship("Application", back_populates="tags")