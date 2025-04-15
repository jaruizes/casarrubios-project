FROM node:20 AS build

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:20-slim

WORKDIR /app
COPY package*.json ./
RUN npm install --omit=dev  # Instala solo dependencias de producción
COPY --from=build /app/dist /app/dist
EXPOSE 3000
CMD ["node", "dist/main"]