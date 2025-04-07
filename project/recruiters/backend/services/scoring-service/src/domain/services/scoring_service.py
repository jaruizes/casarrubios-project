import json
import logging
import time

import numpy as np
import openai
from openai import embeddings
from sklearn.metrics.pairwise import cosine_similarity

from src.application.adapters.db.sqlalchemy_repository import PositionRepository
from src.application.adapters.vector_storage.vector_storage_service import VectorStorageService
from src.application.api.output.application_scoring_publisher import ApplicationScoringPublisher
from src.domain.model.application_analysis import ApplicationAnalysis, ResumeAnalysis
from src.domain.model.application_scoring import Scoring, ApplicationScoring
from src.domain.model.position import Position
from src.infrastructure.kafka.kafka_producer import KafkaProducer
from opentelemetry import trace

logger = logging.getLogger(__name__)
tracer = trace.get_tracer(__name__)

class ScoringService:
    def __init__(self, position_repository: PositionRepository, applicationScoringPublisher: ApplicationScoringPublisher, vectorStorageService: VectorStorageService):
        self.position_repository = position_repository
        self.applicationScoringPublisher = applicationScoringPublisher
        self.vectorStorageService = vectorStorageService

    def score(self, application_analysis: ApplicationAnalysis):
        with tracer.start_as_current_span("scoring"):
            logger.info(f"Computing CV score for position {application_analysis.position_id} and application {application_analysis.application_id}")

            start_time = time.time()
            position = self.position_repository.get_position_by_id(application_analysis.position_id)
            analysis = application_analysis.analysis

            position_collection = f"position_{position.id}"
            self.vectorStorageService.create_collection(position_collection)

            desc_score = round(self.__score_desc(position=position, position_collection=position_collection, analysis=analysis), 2)
            requirement_score = round(self.__score_requirements_new(position=position, analysis=analysis, position_collection=position_collection), 2)
            task_score = round(self.__score_tasks_new(position=position, analysis=analysis, position_collection=position_collection), 2)
            final_score = round((0.1 * desc_score) + (0.6 * requirement_score) + (0.3 * task_score), 2)

            explanation = self.__generate_explanation(final_score, desc_score, requirement_score, task_score, position, analysis)

            time_spent = round(time.time() - start_time, 2)

            scoring = Scoring(
                application_id=application_analysis.application_id,
                score=final_score,
                desc_score=desc_score,
                requirement_score=requirement_score,
                tasks_score=task_score,
                time_spent=time_spent,
                explanation=explanation
            )

            application_scoring = ApplicationScoring(
                application_id=application_analysis.application_id,
                position_id=application_analysis.position_id,
                analysis=analysis,
                scoring=scoring
            )

            logger.info(f"Scoring computed for position {position.id} and application {application_analysis.application_id}: {final_score}")
            self.__logger_scoring(position.id, application_analysis.application_id, scoring)

            self.applicationScoringPublisher.publish_application_scored_event(application_scoring)

    def __logger_scoring(self, position_id: int, application_id: str, scoring: Scoring):
        logger.info(f"Score computed for position {position_id} and application {application_id}")
        logger.info(f"[{position_id} //  {application_id}] Time spent: {scoring.time_spent}")
        logger.info(f"[{position_id} //  {application_id}] Score: {scoring.score}")
        logger.info(f"[{position_id} //  {application_id}] Desc score: {scoring.desc_score}")
        logger.info(f"[{position_id} //  {application_id}] Requirement score: {scoring.requirement_score}")
        logger.info(f"[{position_id} //  {application_id}] Tasks score: {scoring.tasks_score}")


    def __get_embeddings_from_vector_storage(self, collection:str, position_id: int, key: str, text: str):
        with tracer.start_as_current_span("get_embeddings"):
            try:
                conditions = [
                    ("position_id", position_id),
                    ("key", key)
                ]

                logger.info(f"Getting embeddings for position {position_id} and key {key} from vector storage")
                embeddings = self.vectorStorageService.get_embeddings(collection=collection, metadata_filter=conditions)
                if len(embeddings) == 0 or np.all(embeddings == 0):
                    embeddings = self.__get_embeddings_from_model(text)
                    metadata = {
                        "position_id": position_id,
                        "key": key
                    }

                    self.vectorStorageService.add_embeddings(collection=collection, embeddings=embeddings, metadata=metadata)
                    logger.info(f"Embeddings added to vector storage. Metadata filter: {metadata}")

                return embeddings
            except Exception as e:
                logger.error(f"Error getting embedding for text: {text}")
                logger.error(str(e))
                raise e

    def __get_embeddings_from_model(self, text: str):
        with tracer.start_as_current_span("get_embedding"):
            try:
                response = openai.embeddings.create(input=text, model="text-embedding-3-small")
                return np.array(response.data[0].embedding).reshape(1, -1)
            except Exception as e:
                logger.error(f"Error getting embedding for text: {text}")
                logger.error(str(e))
                raise e

    def __compute_semantic_similarity(self, position_collection: str, position_text: str, cv_text: str):
        with tracer.start_as_current_span("compute_semantic_similarity"):
            position_embeddings = self.__get_embeddings_from_model(position_text)
            cv_embeddings = self.__get_embeddings_from_model(cv_text)
            similarity = cosine_similarity(position_embeddings, cv_embeddings)[0][0]
            return similarity

    def __score_desc(self, position: Position, position_collection: str, analysis: ResumeAnalysis):
        with tracer.start_as_current_span("score_desc"):
            position_desc_embeddings = self.__get_embeddings_from_vector_storage(collection=position_collection,
                                                                                 position_id=position.id,
                                                                                 key="description",
                                                                                 text=position.description)

            cv_embeddings = self.__get_embeddings_from_model(analysis.summary)

            return cosine_similarity(position_desc_embeddings, cv_embeddings)[0][0]


    def __get_requirements_model(self, position: Position, mandatory: bool):
        filtered = [req for req in position.requirements if req.mandatory == mandatory]
        return json.dumps([
                {"key": req.key, "value": req.value}
                for req in filtered
            ], ensure_ascii=False)

    def __get_skills_model(self, analysis: ResumeAnalysis):
        cv_skills = analysis.hard_skills + analysis.soft_skills + analysis.tags

        return json.dumps([
            {"key": skill.skill if hasattr(skill, 'skill') else skill, "value": skill.level if hasattr(skill, 'level') else 2}
            for skill in cv_skills
        ])

    def __score_requirements_new(self, position: Position, analysis: ResumeAnalysis, position_collection: str):
        with tracer.start_as_current_span("score_requirements"):
            mandatory_req_model = self.__get_requirements_model(position, True)
            mandatory_req_embeddings = self.__get_embeddings_from_vector_storage(collection=position_collection,
                                                                                 position_id=position.id,
                                                                                 key="mandatory_requirements",
                                                                                 text=mandatory_req_model)
            additional_req_model = self.__get_requirements_model(position, False)
            additional_req_embeddings = self.__get_embeddings_from_vector_storage(collection=position_collection,
                                                                                 position_id=position.id,
                                                                                 key="additional_requirements",
                                                                                 text=additional_req_model)

            skills_model = self.__get_skills_model(analysis)
            skills_embeddings = self.__get_embeddings_from_model(skills_model)

            mandatory_reqs_and_skills_similarity = cosine_similarity(mandatory_req_embeddings, skills_embeddings)[0][0]
            additional_req_and_skills_similarity = cosine_similarity(additional_req_embeddings, skills_embeddings)[0][0]
            bonus_for_additional_similarity = 0.1 if additional_req_and_skills_similarity >= 0.6 else 0.0
            reqs_similarity = mandatory_reqs_and_skills_similarity * (1.0 + bonus_for_additional_similarity)

            return min(reqs_similarity, 1.0)

    def __score_tasks_new(self, position: Position, analysis: ResumeAnalysis, position_collection: str):
        with tracer.start_as_current_span("score_tasks"):
            tasks = position.tasks
            cv_tasks = analysis.key_responsibilities
            cv_tasks_embeddings_map = {}
            total_similarity = 0

            for task in tasks:
                task_embedding = self.__get_embeddings_from_vector_storage(collection=position_collection,
                                                                             position_id=position.id,
                                                                             key=f"task_{task.id}",
                                                                             text=task.description)
                best_similarity = 0
                for cv_task in cv_tasks:
                    cv_task_embedding = cv_tasks_embeddings_map.get(cv_task)
                    if cv_task_embedding is None:
                        cv_task_embedding = self.__get_embeddings_from_model(cv_task)
                        cv_tasks_embeddings_map[cv_task] = cv_task_embedding

                    similarity = cosine_similarity(task_embedding, cv_task_embedding)[0][0]
                    if similarity > best_similarity:
                        best_similarity = similarity

                total_similarity += best_similarity

            return total_similarity / len(tasks)

    def __generate_explanation(self, score, desc_score, requirement_score, tasks_score, position: Position, candidate: ResumeAnalysis) -> str:
        with tracer.start_as_current_span("generate_explanation"):
            prompt = (
                f"The CV of a candidate has been evaluated for the position '{position.title}'.\n\n"
                f"Position Details:\n"
                f"- Description: {position.description}\n"
                f"- Requirements: {position.get_requirements_summary()}\n"
                f"- Main Tasks: {position.get_tasks_summary()}\n\n"
                f"Candidate Profile:\n"
                f"- Strengths: {candidate.strengths}, "
                f"- Concerns: {candidate.concerns}, "
                f"- Hard Skills: {candidate.get_hard_skills_summary()}\n"
                f"- Soft Skills: {candidate.get_soft_skills_summary()}\n"
                f"- Tags: {', '.join(candidate.tags)}\n"
                f"Scoring Results:\n"
                f"- Position description score ('desc_score'): {desc_score}\n"
                f"- Requirements score ('requirement_score'): {requirement_score}\n"
                f"- Tasks score ('task_score'): {tasks_score}\n"
                f"- Final score ('score'): {score}\n"
                f"- Score formula: 'score = round((0.1 * desc_score) + (0.6 * requirement_score) + (0.3 * task_score), 2)'\n"
                "Please generate a detailed explanation about the score achieved, in natural language addressing the following:\n"
                "1. How the candidate's profile relates to the position's description and requirements.\n"
                "2. Which aspects of the CV strongly match the position's tasks and responsibilities.\n"
                "3. Areas of opportunity or discrepancies between the candidate's profile and the role's expectations.\n"
                "4. How each of these elements contributed to the final score. \n\n"
                "The explanation should be clear and concise and highlights both the strengths and the areas for improvement."
                "It must be written in Spanish, no more than 750 words long. "
                "The output must be valid HTML, using only semantic tags like <div>, <ul>, <ol>, <li>, <p>, <strong>, etc., without any CSS or inline styles"
                "The output must be a <div> element with the class 'explanation'"
            )

            try:
                response = openai.chat.completions.create(
                    model="gpt-3.5-turbo",
                    messages=[
                        {"role": "system", "content": "You are a helpful assistant and expert recruiter that explains scoring results."},
                        {"role": "user", "content": prompt}
                    ],
                    temperature=0.7,
                    top_p=1,
                    n=1
                )
                explanation_text = response.choices[0].message.content.strip()
                logger.info("Explanation generated successfully.")
                return explanation_text
            except Exception as e:
                logger.error("Error generating explanation using the LLM.")
                logger.error(str(e))
                return "Could not generate a detailed explanation for the scoring."
