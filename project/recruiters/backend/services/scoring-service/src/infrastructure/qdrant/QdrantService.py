import logging

import numpy as np
from numpy import array
from qdrant_client import QdrantClient
from qdrant_client.http.models import PointStruct
from qdrant_client.models import VectorParams, Distance, Filter, FieldCondition, MatchValue


logger = logging.getLogger(__name__)

class QdrantService:

    def __init__(self, host: str = "localhost", port: int = 6333):
        self.qdrant_client = QdrantClient(host=host, port=port)

    def create_collection(self, collection_name: str, vector_size: int):
        if not self.qdrant_client.collection_exists(collection_name):
            self.qdrant_client.create_collection(collection_name=collection_name,vectors_config=VectorParams(size=vector_size, distance=Distance.COSINE))
            logger.info(f"Collection {collection_name} created.")
        else:
            logger.info(f"Collection {collection_name} already exists, skipping creation.")

    def get_collection_info(self, collection_name: str):
        try:
            collection_info = self.qdrant_client.get_collection(collection_name)
            logger.info(f"Collection info: {collection_info}")
            return collection_info
        except Exception as e:
            logger.error(f"Error getting collection info: {e}")
            raise e

    def insert_vectors(self, collection_name: str, vectors: array, metadata: dict):
        logger.info(f"Inserting vectors: {vectors}")
        self.qdrant_client.upsert(
            collection_name=collection_name,
            points=[
                PointStruct(
                    id=idx,
                    vector=vector.tolist(),
                    payload=metadata
                )
                for idx, vector in enumerate(vectors)
            ]
        )

    # conditions = [
    #     ("application_id", "12345"),
    #     ("position_id", "789"),
    #     ("status", "analyzed")
    # ]

    def get_vectors(self, collection_name: str, conditions: list[tuple[str, str]]):
        logger.info(f"Getting vectors: {conditions}")
        field_conditions = []
        for condition in conditions:
            if len(condition) == 2:
                key = condition[0]
                value = condition[1]
                field_conditions.append(FieldCondition(key=key, match=MatchValue(value=value)))

        results = self.qdrant_client.scroll(
            collection_name=collection_name,
            scroll_filter=Filter(must=field_conditions),
            limit=1000,
            with_vectors=True
        )[0]

        vectors = []
        if results:
            vectors = np.array(results[0].vector).reshape(1, -1)
            logger.info(f"Found: {len(results)} vectors found.")

        return vectors