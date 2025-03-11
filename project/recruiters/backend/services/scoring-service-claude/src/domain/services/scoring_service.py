# src/domain/services/scoring_service.py
import time
from typing import Dict, Any, Tuple
import logging

import numpy as np
import openai
import json
from sklearn.metrics.pairwise import cosine_similarity

from src.application.adapters.db.sqlalchemy_repository import PositionRepository
from src.domain.model.application import Application
from src.domain.model.events import ApplicationScoredEvent
from src.infrastructure.kafka.kafka_producer import KafkaProducer

logger = logging.getLogger(__name__)

class ScoringService:
    def __init__(self, position_repository: PositionRepository, producer: KafkaProducer, output_topic: str):
        self.position_repository = position_repository
        self.producer = producer
        self.output_topic = output_topic

    def compute_cv_score(self, application: Application):
        logger.info(f"Computing CV score for position {application.position_id} and application {application.id}")
        final_score = 0.0
        desc_score = 0.0
        requirement_score = 0.0
        task_score = 0.0
        start_time = time.time()
        position = self.position_repository.get_position_by_id(application.position_id)

        # cv_skills = application['hardSkills'] + application['softSkills'] + application['tags']
        #
        # desc_score = round(self.__compute_semantic_similarity(position['description'], str(application['keyPoints'])), 2)
        # requirement_score = round(self.__score_requirements(position['requirements'], cv_skills), 2)
        # task_score = round(self.__score_tasks(position['tasksAndResponsabilities'], application['tasksAndResponsabilities']), 2)
        # final_score = round((0.1 * desc_score) + (0.6 * requirement_score) + (0.3 * task_score), 2)

        time_spent = round(time.time() - start_time, 2)

        logger.info(f"Score computed for position {position.id} and application {application.id}")
        logger.info(f"[{position.id} //  {application.id}] Time spent: {time_spent}")
        logger.info(f"[{position.id} //  {application.id}] Score: {final_score}")
        logger.info(f"[{position.id} //  {application.id}] Desc score: {desc_score}")
        logger.info(f"[{position.id} //  {application.id}] Requirement score: {requirement_score}")
        logger.info(f"[{position.id} //  {application.id}] Tasks score: {task_score}")

        application_scored_event = ApplicationScoredEvent(
            applicationId=application.id,
            score=final_score,
            descScore=desc_score,
            requirementScore=requirement_score,
            tasksScore=task_score,
            timeSpent=time_spent
        )

        self.producer.send(self.output_topic, application_scored_event.__dict__, application.id)
        logger.info(f"[{application.id} //  {position.id}] Scoring {final_score}. Event sent to Kafka")

    def __get_embedding(self, text: str):
        response = openai.embeddings.create(input=text, model="text-embedding-3-small")
        return np.array(response.data[0].embedding).reshape(1, -1)

    def __compute_semantic_similarity(self, text1: str, text2: str):
        embedding1 = self.__get_embedding(text1)
        embedding2 = self.__get_embedding(text2)
        score = cosine_similarity(embedding1, embedding2)[0][0]
        return score

    def __score_requirements(self, requirements, cv_skills):
        total_score = 0
        max_score = 0

        for req in requirements:
            skill = req["skill"]
            mandatory = req["mandatory"]

            best_match = max((self.__compute_semantic_similarity(skill, cv_skill) for cv_skill in cv_skills), default=0)
            weight = 2 if mandatory.lower() == 'true' else 1
            total_score += best_match * weight
            max_score += weight

        return total_score / max_score if max_score > 0 else 0

    def __score_tasks(self, tasks, cv_tasks):
        cv_tasks = [task.lower() for task in cv_tasks]
        tasks = [task.lower() for task in tasks]
        if not tasks:
            return 0
        total_score = 0
        for task in tasks:
            best_match = max((self.__compute_semantic_similarity(task, cv_task) for cv_task in cv_tasks), default=0)

            total_score += best_match

        return total_score / len(tasks)
