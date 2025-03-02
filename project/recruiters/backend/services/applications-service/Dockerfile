FROM python:3.9-slim

# Definir el directorio de trabajo
WORKDIR /code

# Instalar dependencias
COPY ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt

# Copiar el código fuente
COPY ./app /code/app

# Exponer el puerto
EXPOSE 9081

# Iniciar la aplicación
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "9081"]