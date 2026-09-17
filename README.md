# MeetMap - an event discovery platform

---
## Prerequisites

- **Java** - 24 or above
- **Node.js** - 26 or above
- **npm** - 11.19.0 or above
- **Docker** - 29 or above
- **Docker Compose** - v5.3.1 or above

---
## Starting the application

### 1. Environment

Navigate to the root of the project.

Create a .env file (the defaults from .env.example are a good starting point)

```shell
cp .env.example .env
````
#### Environment variables:

**DATABASE_URL** :JDBC URL of the postgres server<br>
**POSTGRES_USER**: Username of the postgres user<br>
**POSTGRES_PASSWORD**: Password of the postgres user<br>
**POSTGRES_DB**: Name of the specific database on the postgres server<br>
**JWT_SECRET**: Secret used for generating [JWT tokens](https://www.rfc-editor.org/info/rfc7519/)<br>
**SYNAPSE_URL**: URL of the Synapse Matrix server<br>
**SYNAPSE_REGISTRATION_SHARED_SECRET**: Secret used for
[user registration](
https://element-hq.github.io/synapse/latest/usage/configuration/config_documentation.html#registration_shared_secret)
on the Synapse Matrix server. **Note:
the ```registration_shared_secret``` field in ```SYNAPSE_CONFIG_PATH```*must* match this value.**<br>
**SYNAPSE_SERVER_NAME**:
[Server name](
https://element-hq.github.io/synapse/latest/usage/configuration/config_documentation.html#server_name
) for the Synapse Matrix server<br>
**SYNAPSE_REPORT_STATS**: Controls whether
[usage data is reported](
https://element-hq.github.io/synapse/latest/usage/configuration/config_documentation.html#report_stats
) to Synapse.<br>
**SYNAPSE_CONFIG_PATH**: Path to the ```homeserver.yaml``` file used when initializing Synapse<br>
**SYNAPSE_ADMIN_TOKEN**: Secret token used for Synapse
[Admin API](
https://element-hq.github.io/synapse/latest/usage/administration/admin_api/#making-an-admin-api-request) operations<br>
**CORS_ALLOWED_ORIGINS**: Sets the
[allowed origins](
https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Origin) for API requests.

### 2. Backend

All backend related code is located in the ```./backend``` directory.

After setting up the env, run docker compose to initialize the postgres database and the matrix server.

```shell
docker compose up -d
```

This will create a ```./postgres/``` directory with the postgres database
and populate the ```./synapse/``` directory with the Synapse data.

In IDEA > Services tab, add a new Spring Boot profile, if it doesn't already exist.
A default ```BackendApplication``` configuration is usually generated after the Gradle project is synced.
If not, ensure the classpath is set to ```meet-map.backend.main```,
with main class ```cloudflight.integra.backend.BackendApplication```.
If database seeding fails, make sure the ```dev``` profile is added to *active profiles*.

To start the backend, start the ```BackendApplication``` configuration.

Sample API requests can be found in ```./backend/requests/```.

### 3. Frontend

All frontend related code is located in the ```./frontend``` directory.

Navigate to ```./frontend/``` and install the npm packages:
```shell
npm install
```
The frontend relies on a generated OpenApi client. <br>
Run the ```meet-map [openApiGenerate]``` Gradle task before running the frontend
and whenever the API endpoints suffer changes (e.g. checking out another branch).

To start the frontend, run either
```shell
ng serve
```
or
```shell
npm start
```
while in the ```./frontend/``` directory.

Alternatively, you can add a ```npm```run configuration in IDEA.
Set *package.json* to ```./frontend/package.json```,
*scripts* to ```ng```, *command* to ```start```.

The frontend can now be accessed with a browser, by default at ```http://localhost:4200```.

### A. Seeding the database with mock data

Seeding the database is really useful in development.

This is done using the ```POST /api/seed/``` endpoint.<br>
The ```dev``` profile must be active in Spring Boot beforehand. Refer to the backend section for setup.

An user account is also necessary. Create one using ```POST /api/auth/register/```,
then login with ```POST /api/auth/login```.

Using the requests in ```./backend/requests``` is highly recommended, as the Bearer token is automatically inserted in
API requests. (register/login in ```./backend/requests/user.http```, seeding in ```./backend/requests/seed.http```).
