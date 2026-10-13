# ---------- Etapa 1: compilar el frontend ----------
FROM node:24-alpine AS frontend
WORKDIR /app/frontend

# Primero solo los manifiestos: esta capa se reusa mientras no cambien las dependencias
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build


# ---------- Etapa 2: compilar el backend (con el frontend adentro) ----------
FROM eclipse-temurin:21-jdk AS backend
WORKDIR /app/backend

# Wrapper y pom primero: las dependencias de Maven quedan en una capa cacheada
COPY backend/mvnw backend/pom.xml ./
COPY backend/.mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY backend/src src
# El build de Vite pasa a ser contenido estático de Spring Boot
COPY --from=frontend /app/frontend/dist src/main/resources/static
RUN ./mvnw -B package -DskipTests


# ---------- Etapa 3: imagen final, solo para correr ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --no-create-home appuser
COPY --from=backend /app/backend/target/household-expenses-0.0.1-SNAPSHOT.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]