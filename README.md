# obd_back

Has been done:

- In DatabaseInit now has additional validation of fields from the database
- Has been added the flyway
- Has been set up logging via spring journaling (see application.properties)
- Now use psql version 11

**How to startup on server:**

1. Make sure that JAVA_HOME environment in your server machine is exists
2. Transfer .jar archive and docker-compose.yml to work directory on server (for example, in /opt/isyb)
3. `docker compose up db -d` (if first time `docker compose up db --build`)
4. `java -jar name-app.jar`