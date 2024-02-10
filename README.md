# obd_back

Has been done:

- validation logic
- logic of initialization some components during startup application
- refactoring EngineInfoControllerV1

**How to startup on server:**

1. Make sure that JAVA_HOME environment in your machine is exists
2. `./mvnw clean package -DskipTests` - build a project if .jar archive does not exist in target directory
3. Transfer .jar archive and docker-compose.yml to work directory on server (for example, in /opt/isyb)
4. `cd /opt/isyb`
5. `docker compose up db -d`
6. `java -jar name-app.jar`