from fastapi import FastAPI

from src.api.input.rest import applications_rest_api
from fastapi.middleware.cors import CORSMiddleware
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor

app = FastAPI(
    title="Recruiters API",
    version="1.0.0",
    description="API for managing applications in the recruiters system"
)

# Habilitar OpenTelemetry para trazabilidad
FastAPIInstrumentor.instrument_app(app)

# Configurar CORS para permitir peticiones desde cualquier origen (ajustar en producción)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Cambia esto en producción
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Incluir rutas de la API
app.include_router(applications_rest_api.router, prefix="", tags=["applications"])

# Ruta de salud para verificar que la API está corriendo
@app.get("/")
def health_check():
    return {"status": "running"}