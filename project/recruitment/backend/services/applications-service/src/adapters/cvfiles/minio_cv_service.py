import io
import logging
import uuid

from minio import S3Error, Minio
from minio.datatypes import Object

from src.domain.exceptions.CVException import CVException
from src.domain.exceptions.CVFileNotFoundException import CVFileNotFoundException
from src.infrastructure.minio import MinioClient

logger = logging.getLogger(__name__)

class MinioCVService:
    def __init__(self, minio_client: MinioClient, bucket: str):
        self.minio_client: Minio  = minio_client.get_client()
        self.bucket = bucket

    def get_cv_file(self, application_id: uuid) -> bytes:
        self.__check_if_cv_file_exists(application_id)

        try:
            response = self.minio_client.get_object(self.bucket, str(application_id))
            content = io.BytesIO()
            for data in response.stream(32 * 1024):
                content.write(data)

            content_bytes = content.getvalue()
            content.close()
            response.close()
            response.release_conn()

            return content_bytes

        except S3Error as err:
            raise CVException(f"Error de MinIO for application {application_id}: {err}")

    def __check_if_cv_file_exists(self, application_id: uuid) -> Object:
        try:
            return self.minio_client.stat_object(self.bucket, str(application_id))
        except S3Error as err:
            if err.code == 'NoSuchKey':
                raise CVFileNotFoundException(f"CV file not found for application {application_id}", "CV_NOT_FOUND")
            raise CVException(f"Error getting object for application {application_id}: {str(err)}")
        except Exception as e:
            raise CVException(f"Error getting object for application {application_id}: {str(e)}")
