# Health Care Fitness

## Introduction

**Health Care Fitness** is a backend API built with **Spring Boot** (Java 17) for managing health and workout routines. The system allows users to track nutrition, build daily menus, create workout plans, store health metrics and provide analytics. It integrates **Cloudinary** for image storage and **Gemini** (Google Vertex AI) to suggest daily menus.

## Main Features

### User Management & Authentication

- **Sign up, login and change password** via `/api/v1/auth/signup`, `/api/v1/auth/login` and `/api/v1/auth/change-password`. JWT tokens secure the protected endpoints.
- Manage user profiles, including activity level, personal goals and roles.
- **Record health information** such as height, weight, BMI, BMR and body fat percentage for each entry.
- Associate each user with a training plan (**UserPlan**) and track its status (ACTIVE, COMPLETED, GIVEN_UP).

### Nutrition Management

- **Ingredients**: create, view, update and delete ingredients with nutritional values, description and image; download Excel templates and import data with images.
- **Meals and Recipes**: CRUD operations for meals and recipes; manage recipe ingredients; update ingredient quantities within a recipe.
- **Menus**: create today’s menu, view menus by ID, list historical menus; automatically fill menus or ask Gemini AI to suggest menus based on nutritional needs.
- **Chat history**: save conversations with the AI and retrieve menus from previous chats.

### Workout Management

- **Exercises**: manage name, description, muscle groups, activity type (CARDIO/STRENGTH), difficulty and images; download a template and import exercises from Excel files.
- **Muscle Groups & Goals**: list muscle groups (`/muscle-groups`) and workout goals (`/goals`).
- **Plans & Sessions**: create, read, update and delete workout plans; each plan contains multiple sessions; manage the exercises in a session via `/session-exercises`.
- **Run Sessions & Calories Stats**: record run sessions and view calories burned between dates.

### Quiz & Analytics

- **Quiz**: provide a set of health-related questions and process user answers.
- **Dashboard statistics**: monthly user metrics, exercise rankings and recipe rankings.

### Data Import/Export

- Download Excel templates for ingredients and exercises; import bulk data via file upload.
- Export ingredient, recipe and plan data to Excel for offline storage and sharing.

## Architecture & Technologies

The project follows a simple REST service architecture built on Spring Boot and PostgreSQL. The diagram above shows the API communicating with the database, image storage and AI service.

- **Spring Boot 3 & Java 17**: core framework using starters such as `spring-boot-starter-data-jpa`, `spring-boot-starter-web` and `spring-boot-starter-security`
- **Spring Data JPA**: data access and entity mapping to PostgreSQL.
- **JWT**: API security with custom authentication filter and token service.
- **Cloudinary**: image storage for ingredients, meals and exercises
- **Apache POI**: reading/writing Excel files for data import/export
- **Spring AI & Gemini (Vertex AI)**: integrate the Gemini model for menu generation and conversations; configure project, location and model in `application.yml`
- **Springdoc OpenAPI**: generates API documentation and Swagger UI.
- **Docker & Docker Compose**: deploy containers for PostgreSQL and the application, with environment variables defined in `docker-compose.yml`

## Installation

### Requirements

- Java 17 and Maven 3.8+
- Docker and Docker Compose (optional for quick setup)
- Cloudinary and Google Cloud accounts for API keys

### Clone the repository

```bash
git clone https://github.com/MinhPhuc2k3/health-care-fitness.git
cd health-care-fitness
```

### Configure environment variables

Create a `.env` file in the project root to supply environment variables. Use the configuration in `docker-compose.yml` and `application.yml` as a reference. Example:

```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=health_care_fitness
DB_HOST=postgres
DB_PORT=5432
DB_NAME=health_care_fitness
DB_USERNAME=postgres
DB_PASSWORD=postgres

SECRET_KEY=your-jwt-secret
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

SPRING_AI_VERTEX_AI_GEMINI_PROJECT_ID=<your-project-id>
SPRING_AI_VERTEX_AI_GEMINI_LOCATION=asia-southeast1
SPRING_AI_VERTEX_AI_GEMINI_MODEL_NAME=gemini-pro
SPRING_AI_VERTEX_AI_GEMINI_JSON_KEY=<service-account-json>
```

You may also set these variables directly in `docker-compose.yml` or the deployment environment.

### Running with Docker Compose

To spin up the environment with Docker, run:

```bash
docker-compose up --build
```

### Running manually

If you prefer not to use Docker:

1. Install and start PostgreSQL, creating a database and user matching your environment variables.
2. Build and install dependencies:

   ```bash
   ./mvnw clean install
   ```

3. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on port 8000.
## API Usage

1. **Authentication**:  
   Use `POST /api/v1/auth/signup` to register a new account and  
   `POST /api/v1/auth/login` to authenticate and obtain a JWT token.

2. **Authorization**:  
   All protected endpoints require the header  
   `Authorization: Bearer <token>`.

3. **User & Health Data**:  
   Use `GET /api/v1/user` to retrieve user profile information,  
   `POST /api/v1/health-info` to create health records, and  
   `GET /api/v1/health-info/latest` to fetch the most recent health data.

4. **Nutrition Management**:  
   Manage ingredients via `GET /api/v1/ingredients` and `POST /api/v1/ingredients`.  
   Download Excel templates from `/api/v1/ingredients/template` and import data using  
   `POST /api/v1/ingredients/import`.

5. **Meals, Recipes & Menus**:

Use `GET /api/v1/meals` and `GET /api/v1/recipes` to retrieve data.  
   Get today’s menu with `GET /api/v1/menus/today` or generate menus automatically via  
   `POST /api/v1/menus/auto-fill`.

6. **AI-generated Menus**:  
   Use `POST /api/v1/menus/chat` to receive menu suggestions powered by the Gemini AI model.

7. **Workout & Training Plans**:  
   Retrieve exercises using `GET /api/v1/exercises`,  
   create workout plans via `POST /api/v1/plans`, and  
   record running sessions with `POST /api/v1/run-sessions`.

8. **Quiz & Analytics**:  
   Fetch quiz questions from `GET /api/v1/quizzes` and submit answers via  
   `POST /api/v1/quizzes/submit`.  
   Access system analytics using `GET /api/v1/dashboard/analytics`.

9. **API Documentation**:  
   Explore and test all available endpoints using Swagger UI at  
   `http://localhost:8000/swagger-ui.html`.
