from pydantic_settings import BaseSettings, SettingsConfigDict

model_config = SettingsConfigDict(env_file=".env")

class Settings(BaseSettings):
    database_user: str = "postgres"
    database_password: str = "postgres"
    database_name: str = "applications"
    database_host: str = "localhost"
    database_port: str = "5432"
    database_schema: str = "recruiters"
    log_level: str = "DEBUG"

    @property
    def database_url(self):
        return f"postgresql://{self.database_user}:{self.database_password}@{self.database_host}:{self.database_port}/{self.database_name}?options=-csearch_path%3D{self.database_schema}"

settings = Settings()
