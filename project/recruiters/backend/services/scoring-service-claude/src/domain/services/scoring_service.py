# src/domain/services/scoring_service.py
import time
from typing import Dict, Any, Tuple
import logging
from src.domain.model.position import Position
from src.domain.model.application import Application

logger = logging.getLogger(__name__)


class ScoringService:
    """
    Servicio para calcular la puntuación de una aplicación basada en la posición
    y los datos del candidato.
    """

    def calculate_scores(self, position: Position, application: Application) -> Tuple[
        float, float, float, float, float]:
        """
        Calcula diferentes puntuaciones para una aplicación en base a la posición.

        Args:
            position: Datos de la posición con requisitos y tareas
            application: Datos de la aplicación del candidato

        Returns:
            Tuple con (score final, desc_score, requirement_score, task_score, tiempo_procesamiento)
        """
        start_time = time.time()

        # Calcula el score basado en la descripción (similaridad entre descripción del candidato y posición)
        desc_score = self._calculate_description_score(position, application)

        # Calcula el score basado en los requisitos
        requirement_score = self._calculate_requirements_score(position, application)

        # Calcula el score basado en las tareas y experiencia
        task_score = self._calculate_tasks_score(position, application)

        # Calcular puntuación final (media ponderada)
        final_score = (0.3 * desc_score) + (0.5 * requirement_score) + (0.2 * task_score)
        final_score = round(final_score, 2)

        # Tiempo de procesamiento en segundos
        time_spent = round(time.time() - start_time, 3)

        logger.info(f"Application {application.id} scored: {final_score} (time: {time_spent}s)")

        return final_score, desc_score, requirement_score, task_score, time_spent

    def _calculate_description_score(self, position: Position, application: Application) -> float:
        """
        Calcula una puntuación basada en la descripción del candidato y la posición.
        En una implementación real, podría utilizar NLP para medir similitud semántica.

        Por simplicidad, aquí implementamos una versión básica.
        """
        # Implementación simplificada - en un caso real usaríamos NLP
        # para medir la similitud entre textos
        if not position.description:
            return 0.5  # Valor por defecto si no hay descripción

        # Cuantas palabras clave de la descripción aparecen en la descripción del candidato
        keywords = [word.lower() for word in position.title.split()]
        if position.description:
            keywords.extend([word.lower() for word in position.description.split()
                             if len(word) > 3])  # Palabras significativas

        matches = sum(1 for keyword in keywords
                      if keyword.lower() in application.candidate_description.lower())

        # Normalizar score (0-1)
        score = min(1.0, matches / max(1, len(keywords) * 0.3))
        return round(score * 100, 2)  # Convertir a escala 0-100

    def _calculate_requirements_score(self, position: Position, application: Application) -> float:
        """
        Calcula una puntuación basada en los requisitos de la posición y los del candidato.
        """
        if not position.requirements:
            return 50.0  # Valor por defecto

        total_reqs = len(position.requirements)
        mandatory_reqs = sum(1 for req in position.requirements if req.mandatory)

        # Verificar requisitos cumplidos
        matches = 0
        mandatory_matches = 0

        for req in position.requirements:
            req_key = req.key.lower()
            if req_key in application.requirements:
                # Verificar si el valor coincide o es compatible
                candidate_value = application.requirements[req_key].lower()
                position_value = req.value.lower()

                # Verificar coincidencia (en un caso real esta lógica sería más sofisticada)
                if candidate_value == position_value or position_value in candidate_value:
                    matches += 1
                    if req.mandatory:
                        mandatory_matches += 1

        # Calcular score ponderando los requisitos obligatorios más que los opcionales
        mandatory_weight = 0.7 if mandatory_reqs > 0 else 0
        optional_weight = 0.3 if total_reqs > mandatory_reqs else 1.0

        mandatory_score = (mandatory_matches / max(1, mandatory_reqs)) if mandatory_reqs > 0 else 1.0
        optional_score = (matches - mandatory_matches) / max(1,
                                                             total_reqs - mandatory_reqs) if total_reqs > mandatory_reqs else 0

        final_req_score = (mandatory_weight * mandatory_score) + (optional_weight * optional_score)
        return round(final_req_score * 100, 2)  # Convertir a escala 0-100

    def _calculate_tasks_score(self, position: Position, application: Application) -> float:
        """
        Calcula una puntuación basada en las tareas de la posición y la experiencia del candidato.
        """
        if not position.tasks:
            return 50.0  # Valor por defecto

        # Implementación simplificada - en un caso real podríamos usar NLP
        # para comparar las descripciones de las tareas con las experiencias

        task_descriptions = " ".join([task.description.lower() for task in position.tasks])
        experience_descriptions = " ".join([str(exp) for exp in application.experiences.values()])

        # Extraer palabras clave de las tareas
        task_keywords = [word for word in task_descriptions.split() if len(word) > 4]

        # Contar coincidencias de palabras clave en experiencias
        matches = sum(1 for keyword in task_keywords
                      if keyword in experience_descriptions.lower())

        # Normalizar score (0-1)
        score = min(1.0, matches / max(1, len(task_keywords) * 0.3))
        return round(score * 100, 2)  # Convertir a escala 0-100