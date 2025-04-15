from minio import Minio

class MinioClient():
    def __init__(self, endpoint: str, access_key: str, secret_key: str):
        self.endpoint = endpoint
        self.access_key = access_key
        self.secret_key = secret_key
        self.client = Minio(
            endpoint=endpoint,
            access_key=access_key,
            secret_key=secret_key,
            secure=False
        )

    def get_object(self, bucket: str, object_name: str):
        return self.client.get_object(bucket, object_name)

    def get_client(self):
        return self.client