from sqlalchemy import Column, String, Integer, Float, ForeignKey, DateTime, Text, UUID
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
import uuid

Base = declarative_base()


class CandidateDB(Base):
    __tablename__ = 'candidates'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    name = Column(String(255), nullable=False)
    email = Column(String(255), nullable=False)
    phone = Column(String(255), nullable=False)
    cv = Column(String(255), nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    applications = relationship("CandidateApplicationsDB", back_populates="candidate", cascade="all, delete")
    analysis = relationship("CandidateAnalysisDB", uselist=False, back_populates="candidate", cascade="all, delete")
    strengths = relationship("CandidateStrengthDB", back_populates="candidate", cascade="all, delete")
    concerns = relationship("CandidateConcernDB", back_populates="candidate", cascade="all, delete")
    hard_skills = relationship("CandidateHardSkillDB", back_populates="candidate", cascade="all, delete")
    soft_skills = relationship("CandidateSoftSkillDB", back_populates="candidate", cascade="all, delete")
    responsibilities = relationship("CandidateKeyResponsibilityDB", back_populates="candidate", cascade="all, delete")
    questions = relationship("CandidateInterviewQuestionDB", back_populates="candidate", cascade="all, delete")
    tags = relationship("CandidateTagDB", back_populates="candidate", cascade="all, delete")


class CandidateApplicationsDB(Base):
    __tablename__ = 'candidate_applications'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(UUID(as_uuid=True), primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id', ondelete='CASCADE'), nullable=False)
    position_id = Column(Integer, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="applications")
    scoring = relationship("ApplicationScoreDB", uselist=False, back_populates="application", cascade="all, delete")


class ApplicationScoreDB(Base):
    __tablename__ = "application_scoring"
    __table_args__ = {"schema": "recruiters"}

    application_id = Column(UUID(as_uuid=True), ForeignKey("recruiters.candidate_applications.id"), primary_key=True)
    score = Column(Float, nullable=False)
    desc_score = Column(Float, nullable=False)
    requirement_score = Column(Float, nullable=False)
    tasks_score = Column(Float, nullable=False)
    explanation = Column(Text, nullable=False)
    time_spent = Column(Float, nullable=False)
    created_at = Column(DateTime, default=func.now())

    application = relationship("CandidateApplicationsDB", back_populates="scoring")



class CandidateAnalysisDB(Base):
    __tablename__ = 'candidate_analysis'
    __table_args__ = {'schema': 'recruiters'}

    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'), primary_key=True)
    summary = Column(Text)
    total_years_experience = Column(Integer)
    average_permanency = Column(Float)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="analysis")


class CandidateStrengthDB(Base):
    __tablename__ = 'candidate_strengths'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    strength = Column(Text, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="strengths")


class CandidateConcernDB(Base):
    __tablename__ = 'candidate_concerns'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    concern = Column(Text, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="concerns")


class CandidateHardSkillDB(Base):
    __tablename__ = 'candidate_hard_skills'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    skill = Column(String(255), nullable=False)
    level = Column(String(50), nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="hard_skills")


class CandidateSoftSkillDB(Base):
    __tablename__ = 'candidate_soft_skills'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    skill = Column(String(255), nullable=False)
    level = Column(String(50), nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="soft_skills")


class CandidateKeyResponsibilityDB(Base):
    __tablename__ = 'candidate_key_responsibilities'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    responsibility = Column(Text, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="responsibilities")


class CandidateInterviewQuestionDB(Base):
    __tablename__ = 'candidate_interview_questions'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    question = Column(Text, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="questions")


class CandidateTagDB(Base):
    __tablename__ = 'candidate_tags'
    __table_args__ = {'schema': 'recruiters'}

    id = Column(Integer, primary_key=True)
    candidate_id = Column(UUID(as_uuid=True), ForeignKey('recruiters.candidates.id'))
    tag = Column(String(255), nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    candidate = relationship("CandidateDB", back_populates="tags")