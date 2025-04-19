# Agregator Backend

This is the Spring Boot backend service for the Agregator application. It provides APIs for managing AI tools and categories, integrating with a Google Cloud SQL database and Google Cloud Storage for image hosting.

## Technology Stack

*   **Java:** 17
*   **Framework:** Spring Boot 3.x
*   **Build Tool:** Maven
*   **Database:** Google Cloud SQL for MySQL (using JPA/Hibernate)
*   **Storage:** Google Cloud Storage (for tool images)
*   **Authentication (GCP):** Application Default Credentials (ADC) via `gcloud` CLI
*   **Local Config:** `.env` file loaded via `spring-dotenv`

## Local Development Setup

### Prerequisites

1.  **JDK 17:** Ensure you have Java Development Kit 17 installed.
2.  **Maven:** Ensure Apache Maven is installed and configured.
3.  **Google Cloud CLI (`gcloud`):** Install the [Google Cloud CLI](https://cloud.google.com/sdk/docs/install).
4.  **GCP Project & Services:**
    *   A Google Cloud Platform project.
    *   A Cloud SQL for MySQL instance within that project.
    *   A Google Cloud Storage bucket within that project.
    *   The **Cloud SQL Admin API** enabled in your GCP project.
5.  **Database User:** A user created in your Cloud SQL instance with appropriate permissions for the database used by the application.
6.  **GCS Permissions:** The credentials used (ADC or service account) need permissions to write to the specified GCS bucket (e.g., `roles/storage.objectCreator`) and the bucket should allow public reads if desired (e.g., `roles/storage.objectViewer` for `allUsers`).

### Steps

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd agregator-backend
    ```

2.  **Authenticate with GCP:** Log in using the `gcloud` CLI and set up Application Default Credentials. Make sure to configure it for the *correct* GCP project that hosts your Cloud SQL instance and GCS bucket.
    ```bash
    # Replace <your-gcp-project-id> with the actual project ID
    gcloud config set project <your-gcp-project-id>
    gcloud auth application-default login
    gcloud auth application-default set-quota-project <your-gcp-project-id>
    ```

3.  **Create `.env` File:** Create a file named `.env` in the **root directory** of the project (alongside `pom.xml`). **This file should NOT be committed to Git.** Populate it with your specific GCP and database credentials:
    ```dotenv
    # Environment variables for local development
    # IMPORTANT: DO NOT COMMIT THIS FILE TO GIT!

    # Cloud SQL Database Configuration
    DB_INSTANCE_CONNECTION_NAME=<your-instance-connection-name> # e.g., my-project:us-central1:my-instance
    DB_NAME=<your-database-name>
    DB_USER=<your-database-user>
    DB_PASSWORD=<your-database-password>

    # Google Cloud Storage Configuration
    GCS_BUCKET_NAME=<your-gcs-bucket-name>
    ```
    *(Replace the placeholders `<...>` with your actual values.)*

4.  **Build the Project:** (Optional, Spring Boot runs this implicitly)
    ```bash
    mvn clean install
    ```

5.  **Run the Application:**
    ```bash
    mvn spring-boot:run
    ```
    The application should start, connect to the database (potentially creating/updating tables), seed initial categories if the database is empty, and be accessible (by default) at `http://localhost:8080`.

## API Endpoints

The following endpoints are available under the `/api` prefix:

*   **Categories:**
    *   `GET /api/categories`: Retrieves a list of all categories.
*   **Tools:**
    *   `GET /api/tools`: Retrieves a list of tools. Supports optional query parameters:
        *   `categoryName` (String, multiple allowed): Filter by one or more category names (OR condition).
        *   `search` (String): Filter by name or description (case-insensitive LIKE search).
        *   `pricing` (String, multiple allowed): Filter by one or more pricing models (OR condition).
        *   `sortBy` (String): Sort results. Values: `newest` (default), `oldest`, `upvoted`, `name_asc`, `name_desc`.
    *   `POST /api/tools`: Creates a new tool (requires JSON body matching `ToolCreateRequest`).
    *   `GET /api/tools/{id}`: Retrieves details for a specific tool by its ID.
    *   `PATCH /api/tools/{id}`: Partially updates an existing tool (accepts JSON body matching `ToolUpdateRequest`).
    *   `POST /api/tools/{id}/image`: Uploads an image for a specific tool (expects multipart/form-data with a `file` field).

## Configuration

*   **`application.properties`:** Contains main application settings, JPA/Hibernate configuration, and references environment variables for secrets.
*   **`.env`:** (Local development only, **NOT COMMITTED TO GIT**) Stores sensitive credentials (DB password, instance name, etc.) loaded via `spring-dotenv`.

## Deployment

When deploying to a cloud environment (e.g., Cloud Run, App Engine):

*   Do **NOT** include the `.env` file.
*   Configure the required environment variables (e.g., `DB_INSTANCE_CONNECTION_NAME`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `GCS_BUCKET_NAME`) directly within the deployment environment's configuration settings.
*   Ensure the runtime service account has the necessary IAM permissions for Cloud SQL and Cloud Storage. 