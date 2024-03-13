[![Build Status](https://jenkins.equilend.com/buildStatus/icon?job=Onesource_TK_Develop_Build)](https://jenkins.equilend.com/job/Onesource_TK_Develop_Build/)

## Table of contents

* [1. Project Description](#1-project-description)
* [2. Technologies](#2-technologies)
* [3. Setup](#3-setup)
* [4. Application Configuration](#4-application-configuration)

## 1. Project Description

OneSource integration toolkit is a mediator service between 1SOURCE and SPIRE systems. The toolkit aims to enhance the
end-user experience with lending or borrowing securities.

## 2. Technologies

* Java 17
* SpringBoot 2.7.13
* Maven (wrapper)
* Docker
* Docker Compose 3.9
* Apache Camel 2.24.1
* Flyway 8.0.0
* PostgreSQL 15
* Testcontainers 1.18.1

## 3. Setup

The application can be started with Maven or with Docker.

### 3.1. Launch application

<details>
<summary>Local Development</summary>

### 1. Launch OneSource mock using docker compose

OneSource mock API is implemented with [Mockoon](https://github.com/mockoon/mockoon) library.
It uses an environment: `./mockApi/1source-mock.json`. Each endpoint's update should be made
via desktop application and saved to the existed environment for better debug experience.

For the local test use `compose.local.yml` file. It uses `Docker-local` to launch 1source-integration service.
For the local development and local testing with docker container we use two profiles: "local" and "test" accordingly.
Local profile is configured to work with local (or default) variables and local services.
Test profile is configured to work inside Docker network.

1. Check the docker and the docker compose are installed in the system: `docker -v`, `docker compose version`
2. From the project main folder run a terminal command: `docker compose -f compose.local.yml up 1source-mock -d`. A
   container should be started and launched
   a server on the port: `8081`.
   *Example: GET request: `http://localhost:8081/ledger/events` returns mocked 1source events*.
3. To remove the created resources execute `docker compose -f compose.local.yml down 1source-mock`.

### 2. Launch SPIRE mock using docker compose

SPIRE mock API is implemented with [Mockoon](https://github.com/mockoon/mockoon) library.
It uses an environment: `./mockApi/spire-mock.json`. Each endpoint's update should be made
via desktop application and saved to the existed environment for better debug experience.

1. From the project main folder run a terminal command: `docker compose -f compose.local.yml up spire-mock -d`. A
   container should be started and launched
   a server on the port: `8083`.
   *Example: GET request: `http://localhost:8083/trades/search/position` returns mocked SPIRE data*.
2. To remove the created resources execute `docker compose -f compose.local.yml down spire-mock`.

### 3. Launch Keycloak mock using docker compose

1. From the project main folder run a terminal command: `docker compose -f compose.local.yml up keycloak-mock -d`. A
   container should be started and launched
   a server on the port: `8088`.
   *Example: GET request: `http://localhost:8088/admin` returns mocked log-in page*.
2. To remove the created resources execute `docker compose -f compose.local.yml down keycloak-mock`.

### 4. Launch PostgreSQL using docker compose

1. From the project main folder run a terminal command: `docker compose -f compose.local.yml up 1source-postgres -d`. A
   container should be started and launched
   a server on the port: `5432`.
2. To remove the created resources execute `docker compose -f compose.local.yml down 1source-postgres`.

### 5. Launch Integration Toolkit using

To launch application in debug mode - launch the App via IDE.

To launch application in docker:

1. From the project main folder run a terminal command: `docker compose -f compose.local.yml up 1source-integration -d`.
   A container should be started and launched a server on the port: `8080`.
2. To remove the created resources execute `docker compose -f compose.local.yml down 1source-integration`.

</details>

<details>
<summary>Remote env</summary>

1. Launch Postgres database locally or using Docker Compose: `docker compose up postgres -d`
2. Launch IntegrationApp using IDE or Docker Compose: `docker compose up 1source-integration -d`

</details>

### 3.2 Launch tests

Unit tests:
`mvn test`

Integration tests:
`mvn verify`

Integration tests + skip Unit tests:
`mvn verify -DskipUTs=true`

## 4. Application configuration

| Name                                                                     | Required | Default                                          | Description                                                                                        |
|--------------------------------------------------------------------------|----------|--------------------------------------------------|----------------------------------------------------------------------------------------------------|
| camel.timer                                                              | YES      | 100_000                                          | A scheduled timer for Apache camel to pull the information from external systems, in milliseconds. |
| camel.route.autostart                                                    | NO       | true                                             | A flag to turn Apache Camel routing off. (As usual used for testing purposes. )                    |
| spring.datasource.url                                                    | YES      | jdbc:postgresql://1source-postgres:5432/postgres | The connection url for the relational db. The default is a link to a docker container.             |
| spring.datasource.username                                               | YES      | postgres                                         | Username for the db.                                                                               |
| spring.datasource.password                                               | YES      | postgres                                         | Password for the db.                                                                               |
| spring.flyway.locations                                                  | NO       | classpath:db/migration                           | Location of the SQL migration scripts.                                                             |
| spring.flyway.baseline-on-migrate                                        | NO       | true                                             | Turn of the baseline of the migrations.                                                            |
| spring.flyway.url                                                        | YES      | jdbc:postgresql://1source-postgres:5432/postgres | The connection url for the relational db.                                                          |
| spring.flyway.user                                                       | YES      | postgres                                         | Username for the db.                                                                               |
| spring.flyway.password                                                   | YES      | postgres                                         | Password for the db.                                                                               |
| spring.flyway.schemas                                                    | YES      | postgres                                         | The database scheme names managed by Flyway.                                                       |
| spring.flyway.enabled                                                    | NO       | true                                             | Turn off migrations.                                                                               |
| spring.flyway.default-schema                                             | YES      | postgres                                         | Default schema name managed by Flyway.                                                             |
| spring.jackson.date-format                                               | NO       | yyyy-MM-dd'T'HH:mm:ss.SSSX                       | Date format setting.                                                                               |
| onesource.baseEndpoint                                                   | YES      |                                                  | The endpoint for 1Source environment.                                                              |
| onesource.version                                                        | YES      |                                                  | The current 1Source version.                                                                       |
| onesource.auth.server-url                                                | YES      |                                                  | The 1Source auth server url.                                                                       |
| onesource.auth.realm                                                     | YES      |                                                  | The configured realm for authorization.                                                            |
| onesource.auth.public-client                                             | NO       | true                                             | If true, the java-adapter will not send credentials for the client to Keycloak .                   |
| onesource.auth.resource                                                  | YES      |                                                  | The client-id of the application.                                                                  |
| onesource.auth.principal-attribute                                       | YES      |                                                  | Token attribute to populate the userPrincipal name with.                                           |
| onesource.auth.username                                                  | YES      |                                                  | User name credentials.                                                                             |
| onesource.auth.password                                                  | YES      |                                                  | User password credentials.                                                                         |
| onesource.auth.credentials.secret                                        | YES      |                                                  | Client secret.                                                                                     |
| spire.baseEndpoint                                                       | YES      |                                                  | The endpoint for Spire environment.                                                                |
| spire.lenderEndpoint                                                     | YES      |                                                  | Deprecated. Will be changed to spire.baseEndpoint. The endpoint for Lender's Spire environment.    |
| spire.borrowerEndpoint                                                   | YES      |                                                  | Deprecated. Will be changed to spire.baseEndpoint. The endpoint for Borrower's Spire environment.  |
| spire.username                                                           | YES      |                                                  | Username for SPIRE connection.                                                                     |
| spire.userid                                                             | YES      |                                                  | Id of Username.                                                                                        |
| spire.password                                                           | YES      |                                                  | Password for Spire connection.                                                                     |
| cloudevents.specversion                                                  | NO       | 1.0                                              | Spec version for Cloud Events.                                                                     |
| integration-toolkit.starting-trade-event-datetime                        | YES      | 2023-06-25T09:51:16.111Z                         | A date to retrieve to retrieve the events from.                                                    |
| integration-toolkit.route.rerate.enable                                  | YES      | true                                             | Turn on/off Rerate route                                                                           |
| integration-toolkit.route.contract-initiation-trade-started.enable       | YES      | true                                             | Turn on/off Contract Initiation with trade route                                                   |
| integration-toolkit.route.contract-initiation-without-trade-route.enable | YES      | true                                             | Turn on/off Contract Initiation without trade route                                                |
| integration-toolkit.uri                                                  | YES      | http://integration.toolkit                       | The Integration toolkit uri for recording events/exceptions.                                       |
| notification.enable                                                      | NO       | false                                            | Turn on/off events notification to SPIRE.                                                          |
| notification.timer                                                       | NO       | 100_000                                          | A scheduled timer for the notification process.                                                    |
| notification.spire.bootstrap-server                                      | YES      | localhost:9092                                   | A bootstrap server for the Kafka.                                                                  |
| notification.spire.topic                                                 | YES      | TEST.TOPIC                                       | SPIRE Kafka topic to produce events.                                                               |
| notification.spire.auth.key                                              | YES      | empty                                            | A key for authorization to the Kafka cluster.                                                      |
| notification.spire.auth.secret                                           | YES      | empty                                            | A secret for authorization to the Kafka cluster.                                                   |

### 4.1 System environments for build

| Property Name                                                            | System environment               |
|--------------------------------------------------------------------------|----------------------------------|
| camel.timer                                                              | CAMEL_TIMER                      |
| spring.datasource.url                                                    | DB_URL                           |
| spring.datasource.username                                               | DB_USERNAME                      |
| spring.datasource.password                                               | DB_PASSWORD                      |
| spring.datasource.password                                               | DB_PASSWORD                      |
| spring.flyway.url                                                        | DB_URL                           |
| spring.flyway.user                                                       | DB_USERNAME                      |
| spring.flyway.password                                                   | DB_PASSWORD                      |
| spring.flyway.schemas                                                    | DB_SCHEMA                        |
| spring.flyway.default-schema                                             | DB_SCHEMA                        |
| onesource.baseEndpoint                                                   | 1SOURCE_ENDPOINT                 |
| onesource.version                                                        | 1SOURCE_VERSION                  |
| onesource.auth.server-url                                                | 1SOURCE_AUTH_URL                 |
| onesource.auth.realm                                                     | 1SOURCE_AUTH_REALM               |
| onesource.auth.resource                                                  | 1SOURCE_AUTH_CLIENT_ID           |
| onesource.auth.principal-attribute                                       | 1SOURCE_AUTH_PRINCIPAL_ATTRIBUTE |
| onesource.auth.username                                                  | 1SOURCE_AUTH_USER_NAME           |
| onesource.auth.password                                                  | 1SOURCE_AUTH_USER_PASSWORD       |
| onesource.auth.credentials.secret                                        | 1SOURCE_AUTH_CLIENT_SECRET       |
| spire.baseEndpoint                                                       | SPIRE_BASE_ENDPOINT              |
| spire.lenderEndpoint                                                     | SPIRE_LENDER_ENDPOINT            |
| spire.borrowerEndpoint                                                   | SPIRE_BORROWER_ENDPOINT          |
| spire.username                                                           | SPIRE_USERNAME                   |
| spire.password                                                           | SPIRE_PASSWORD                   |
| integration-toolkit.starting-trade-event-datetime                        | CAMEL_TIMESTAMP                  |
| integration-toolkit.route.rerate.enable                                  | RERATE_ROUTE                     |
| integration-toolkit.route.contract-initiation-trade-started.enable       | CI_WITH_TRADE_ROUTE              |
| integration-toolkit.route.contract-initiation-without-trade-route.enable | CI_WITHOUT_TRADE_ROUTE           |
| integration-toolkit.uri                                                  | INTEGRATION_TOOLKIT_URI          |
| notification.enable                                                      | NOTIFICATION_ENABLE              |
| notification.timer                                                       | NOTIFICATION_TIMER               |
| notification.spire.bootstrap-server                                      | SPIRE_BOOTSTRAP_SERVER           |
| notification.spire.topic                                                 | NOTIFICATION_SPIRE_TOPIC         |
| notification.spire.auth.key                                              | NOTIFICATION_SPIRE_KEY           |
| notification.spire.auth.secret                                           | NOTIFICATION_SPIRE_SECRET        |
|                                                                          |                                  |

## 5.1 Generated DTO based on swagger

For generated 1source DTO classes with OneOf* replace '@JsonTypeInfo(use = JsonTypeInfo.Id.NAME...' with @JsonTypeInfo(
use = Id.DEDUCTION)