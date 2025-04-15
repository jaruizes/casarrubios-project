import logging

from numpy import array

from src.infrastructure.qdrant.QdrantService import QdrantService

logger = logging.getLogger(__name__)


class VectorStorageService:
    def __init__(self, vector_storage_client: QdrantService):
        self.vector_storage_client = vector_storage_client


    def create_collection(self, collection_name: str, vector_size: int = 1536):
        try:
            self.vector_storage_client.create_collection(collection_name=collection_name, vector_size=vector_size)
            logger.info(f"Collection {collection_name} created.")
        except Exception as e:
            logger.error(f"Error creating collection: {e}")
            raise


    def add_embeddings(self, collection: str, embeddings: array, metadata: dict):
        try:
            self.vector_storage_client.insert_vectors(collection_name=collection, vectors=embeddings, metadata=metadata)
            logger.info(f"Embeddings added to vector storage. Metadata: {metadata}")
        except Exception as e:
            logger.error(f"Error adding document to vector storage: {e}")
            raise

    def get_embeddings(self, collection: str, metadata_filter: list[tuple[str, str]]):
        try:
            vectors = self.vector_storage_client.get_vectors(collection_name=collection, conditions=metadata_filter)
            logger.info(f"Vectors retrieved from vector storage. Metadata filter: {metadata_filter}")
            return vectors
        except Exception as e:
            logger.error(f"Error retrieving document from vector storage: {e}")
            raise