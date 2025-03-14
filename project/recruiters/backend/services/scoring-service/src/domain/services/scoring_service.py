import logging
import time

import numpy as np
import openai
from sklearn.metrics.pairwise import cosine_similarity

from src.application.adapters.db.sqlalchemy_repository import PositionRepository
from src.application.api.output.application_scoring_publisher import ApplicationScoringPublisher
from src.domain.model.application_analysis import ApplicationAnalysis
from src.domain.model.application_scoring import Scoring, ApplicationScoring
from src.infrastructure.kafka.kafka_producer import KafkaProducer

logger = logging.getLogger(__name__)

class ScoringService:
    def __init__(self, position_repository: PositionRepository, applicationScoringPublisher: ApplicationScoringPublisher):
        self.position_repository = position_repository
        self.applicationScoringPublisher = applicationScoringPublisher

    def score(self, application_analysis: ApplicationAnalysis):
        logger.info(f"Computing CV score for position {application_analysis.position_id} and application {application_analysis.application_id}")

        start_time = time.time()
        position = self.position_repository.get_position_by_id(application_analysis.position_id)
        analysis = application_analysis.analysis

        cv_skills = analysis.hard_skills + analysis.soft_skills + analysis.tags

        desc_score = round(self.__compute_semantic_similarity(position.description, str(analysis.strengths)), 2)
        requirement_score = round(self.__score_requirements(position.requirements, cv_skills), 2)
        task_score = round(self.__score_tasks(position.tasks, analysis.key_responsibilities), 2)
        final_score = round((0.1 * desc_score) + (0.6 * requirement_score) + (0.3 * task_score), 2)

        time_spent = round(time.time() - start_time, 2)

        logger.info(f"Score computed for position {position.id} and application {application_analysis.application_id}")
        logger.info(f"[{position.id} //  {application_analysis.application_id}] Time spent: {time_spent}")
        logger.info(f"[{position.id} //  {application_analysis.application_id}] Score: {final_score}")
        logger.info(f"[{position.id} //  {application_analysis.application_id}] Desc score: {desc_score}")
        logger.info(f"[{position.id} //  {application_analysis.application_id}] Requirement score: {requirement_score}")
        logger.info(f"[{position.id} //  {application_analysis.application_id}] Tasks score: {task_score}")

        scoring = Scoring(
            application_id=application_analysis.application_id,
            score=final_score,
            desc_score=desc_score,
            requirement_score=requirement_score,
            tasks_score=task_score,
            time_spent=time_spent
        )

        application_scoring = ApplicationScoring(
            application_id=application_analysis.application_id,
            position_id=application_analysis.position_id,
            analysis=analysis,
            scoring=scoring
        )

        logger.info(f"Scoring computed for position {position.id} and application {application_analysis.application_id}: {final_score}")

        # self.producer.send(self.output_topic, application_scoring.__dict__, application_analysis.application_id)
        self.applicationScoringPublisher.publish_application_scored_event(application_scoring)


    def __get_embedding(self, text: str):
        try:
            response = openai.embeddings.create(input=text, model="text-embedding-3-small")
            return np.array(response.data[0].embedding).reshape(1, -1)
        except Exception as e:
            logger.error(f"Error getting embedding for text: {text}")
            logger.error(str(e))
            raise e

    def __compute_semantic_similarity(self, text1: str, text2: str):
        embedding1 = self.__get_embedding(text1)
        embedding2 = self.__get_embedding(text2)
        score = cosine_similarity(embedding1, embedding2)[0][0]
        return score

    def __score_requirements(self, requirements, cv_skills):
        total_score = 0
        max_score = 0

        for req in requirements:
            skill = req.key
            mandatory = req.mandatory
            value = req.value
            best_match = 0
            skill_and_level = skill + ' (' + str(self.__assignNumericLevel(value)) + ')'
            for cv_skill in cv_skills:
                cv_skill_and_level = cv_skill
                if hasattr(cv_skill, 'level'):
                    cv_skill_and_level = cv_skill.skill + ' (' + str(self.__assignNumericLevel(cv_skill.level)) + ')'

                if skill_and_level == cv_skill_and_level:
                    best_match = 1
                    break

                match = self.__compute_semantic_similarity(skill_and_level, cv_skill_and_level)
                if match > best_match:
                    best_match = match

            weight = 2 if mandatory else 1
            total_score += best_match * weight
            max_score += weight


        return total_score / max_score if max_score > 0 else 0

    def __assignNumericLevel(self, level):
        if level == "Avanzado":
            return 3
        if level == "Intermediate":
            return 2
        if level == "Basico":
            return 1
        return 0


    def __score_tasks(self, tasks, cv_tasks):
        if not tasks:
            return 0
        total_score = 0
        for task in tasks:
            best_match = max((self.__compute_semantic_similarity(task.description, cv_task) for cv_task in cv_tasks), default=0)

            total_score += best_match

        return total_score / len(tasks)
