# Contributing Guidelines

Since this is a collaborative university project with multiple microservices, please follow this GitHub workflow to ensure we don't overwrite each other's code.

## Git Workflow

1.  **Clone the Repository**:
    ```bash
    git clone <repository_url>
    cd threat-intel-platform
    ```

2.  **Create a Branch for Your Service**:
    Before making changes, always create a new branch. Name it according to the service or feature you are working on.
    ```bash
    git checkout -b feature/ingestion-service-update
    ```

3.  **Make Your Changes and Commit**:
    Only commit the files related to your service. Avoid committing system files like `.idea`, `target/`, or `application.properties` (if it contains your personal API keys or DB passwords).
    ```bash
    git add <your-modified-files>
    git commit -m "Updated fetching logic in ingestion service"
    ```

4.  **Push to GitHub**:
    ```bash
    git push origin feature/ingestion-service-update
    ```

5.  **Create a Pull Request (PR)**:
    Go to GitHub and open a Pull Request from your branch to the `main` branch. Ask another team member to review it before merging.

## Working with Database Credentials
Since everyone might have different MySQL passwords (e.g., `root`/`blank` vs `root`/`123`), do NOT commit your `application.properties` if you change the DB password. The `.gitignore` is already set up to ignore `application.properties`. 
If you add a new configuration that the team needs, share it via Discord/Slack or create an `application.properties.example` file.
