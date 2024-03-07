# obd_back

**Changes**
- Reimplemented the logic of initialization from synchronous stack to async stack
- Solved problems with application running and validation of engine_info_fields

**How to startup on server:**

1. Make sure that JAVA_HOME environment in your server machine is exists
2. Transfer .jar archive and docker-compose.yml to work directory on server (for example, in /opt/isyb)
3. `docker compose up db -d` (if first time `docker compose up db --build`)
4. `java -jar name-app.jar`