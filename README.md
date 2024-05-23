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

| Name                                                                       | Required | Default                                          | Description                                                                                        |
|----------------------------------------------------------------------------|----------|--------------------------------------------------|----------------------------------------------------------------------------------------------------|
| camel.timer                                                                | NO       | 100_000                                          | A scheduled timer for Apache camel to pull the information from external systems, in milliseconds. |
| camel.route.autostart                                                      | NO       | true                                             | A flag to turn Apache Camel routing off. (As usual used for testing purposes. )                    |
| spring.datasource.url                                                      | YES      | jdbc:postgresql://1source-postgres:5432/postgres | The connection url for the relational db. The default is a link to a docker container.             |
| cloudevents.source                                                         | YES      | http://lender-or-borrower.endpoint               | Source of producing cloudevents. Expected the participant's URI                                    |
| cloudevents.specversion                                                    | NO       | 1.0                                              | Spec version for Cloud Events.                                                                     |
| notification.enable                                                        | YES      | false                                            | Turn on/off events notification to SPIRE Kafka topic.                                              |
| notification.timer                                                         | NO       | 100_000                                          | A scheduled timer for the notification process.                                                    |
| onesource.auth.api.client-id                                               | YES      |                                                  | The client-id for the toolkit's API client.                                                        |
| onesource.auth.api.realm                                                   | YES      |                                                  | The configured realm for the toolkit's API client authorization.                                   |
| onesource.auth.api.server-url                                              | YES      |                                                  | The 1Source auth server url for the toolkit's API client.                                          |
| onesource.auth.client-id                                                   | YES      |                                                  | The client-id of the application.                                                                  |
| onesource.auth.credentials.secret                                          | YES      |                                                  | Client secret.                                                                                     |
| onesource.auth.principal-attribute                                         | YES      |                                                  | Token attribute to populate the userPrincipal name with.                                           |
| onesource.auth.public-client                                               | NO       | true                                             | If true, the java-adapter will not send credentials for the client to Keycloak.                    |
| onesource.auth.password                                                    | YES      |                                                  | User password credentials.                                                                         |
| onesource.auth.realm                                                       | YES      |                                                  | The configured realm for authorization.                                                            |
| onesource.auth.server-url                                                  | YES      |                                                  | The 1Source auth server url.                                                                       |
| onesource.auth.username                                                    | YES      |                                                  | User name credentials.                                                                             |
| onesource.base-endpoint                                                    | YES      |                                                  | The endpoint for 1Source environment.                                                              |
| onesource.version                                                          | YES      |                                                  | The current 1Source version.                                                                       |
| route.delegate-flow.contract-initiation.enable                             | NO       | true                                             | Turn on/off Contract Initiation delegate flow (without trade)                                      |
| route.delegate-flow.contract-initiation.timer                              | NO       | 60000                                            | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| route.delegate-flow.contract-initiation.redelivery-policy.max-redeliveries | NO       | 2                                                | The maximum redeliveries for Apache camel exception handler                                        |
| route.delegate-flow.contract-initiation.redelivery-policy.delay-pattern    | NO       | 1:2000;2:30000                                   | The delay pattern for Apache camel exception handler                                               |
| route.delegate-flow.update-position.enable                                 | NO       | true                                             | A turn on/off Update Position route for delegated contract initiation flow.                        |
| route.delegate-flow.update-position.timer                                  | NO       | 60000                                            | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| route.position-listener.enable                                             | NO       | true                                             | Turn on/off Position Listener route.                                                               |
| route.position-listener.timer                                              | NO       | 60000                                            | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| route.position-listener.update-timer                                       | NO       | 60000                                            | A scheduled timer for Apache camel to execute update process in the route, in milliseconds.        |
| route.rerate.enable                                                        | NO       | true                                             | Turn on/off Rerate route                                                                           |
| route.rerate.timer                                                         | NO       | 5000                                             | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| route.rerate.redelivery-policy.max-redeliveries                            | NO       | 2                                                | The maximum redeliveries for Apache camel exception handler                                        |
| route.rerate.redelivery-policy.delay-pattern                               | NO       | 1:2000;2:30000                                   | The delay pattern for Apache camel exception handler                                               |
| route.returns.enable                                                       | NO       | true                                             | Turn on/off Returns route                                                                          |
| route.returns.timer                                                        | NO       | 5000                                             | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| route.returns.redelivery-policy.max-redeliveries                           | NO       | 2                                                | The maximum redeliveries for Apache camel exception handler                                        |
| route.returns.redelivery-policy.delay-pattern                              | NO       | 1:2000;2:30000                                   | The delay pattern for Apache camel exception handler                                               |
| route.unilateral-flow.recall-confirmation.enable                           | NO       | true                                             | A turn on/off Update Position route for delegated contract initiation flow.                        |
| route.unilateral-flow.recall-confirmation.timer                            | NO       | 60000                                            | A scheduled timer for Apache camel to execute the route, in milliseconds.                          |
| spire.base-endpoint                                                        | YES      |                                                  | The endpoint for Spire environment.                                                                |
| spire.kafka.consumer.bootstrap-server                                      | YES      | localhost:29092                                  | A bootstrap server for the Kafka for consumer                                                      |
| spire.kafka.consumer.listener.correction-instruction.group-id              | YES      | it-ci                                            | Kafka group-id  for correction instruction                                                         |
| spire.kafka.consumer.listener.correction-instruction.topic                 | YES      | correctionInstruction                            | Kafka topic  for correction instruction                                                            |
| spire.kafka.consumer.listener.decline-instruction.group-id                 | YES      | it-di                                            | Kafka group-id  for decline instruction                                                            |
| spire.kafka.consumer.listener.decline-instruction.topic                    | YES      | declineInstruction                               | Kafka topic for decline instruction                                                                |
| spire.kafka.consumer.listener.recall-instruction.enable                    | YES      | false                                            | Enable Kafka listener for recall instructions. Should be enabled only for the Lender participant.  |
| spire.kafka.consumer.listener.recall-instruction.group-id                  | YES      | it-ri                                            | Kafka group-id  for recall instruction                                                             |
| spire.kafka.consumer.listener.recall-instruction.topic                     | YES      | recallInstruction                                | Kafka topic for recall instruction                                                                 |
| spire.kafka.consumer.password                                              | YES      | kafka-password                                   | A password for authorization for the Kafka consumer                                                |
| spire.kafka.consumer.username                                              | YES      | kafka-user                                       | A user for authorization for the Kafka consumer                                                    |
| spire.kafka.producer.auth.key                                              | YES      | empty                                            | A key for authorization to the Kafka cluster.                                                      |
| spire.kafka.producer.auth.secret                                           | YES      | empty                                            | A secret for authorization to the Kafka cluster.                                                   |
| spire.kafka.producer.bootstrap-server                                      | YES      | localhost:9092                                   | A bootstrap server for the Kafka.                                                                  |
| spire.kafka.producer.topic                                                 | YES      | TEST.TOPIC                                       | SPIRE Kafka topic to produce events.                                                               |
| spire.password                                                             | YES      |                                                  | Password for Spire connection.                                                                     |
| spire.user-id                                                              | YES      |                                                  | User id for SPIRE connection.                                                                      |
| spire.username                                                             | YES      |                                                  | Username for SPIRE connection.                                                                     |
| spring.datasource.password                                                 | YES      | postgres                                         | Password for the db.                                                                               |
| spring.datasource.username                                                 | YES      | postgres                                         | Username for the db.                                                                               |
| spring.flyway.baseline-on-migrate                                          | NO       | true                                             | Turn of the baseline of the migrations.                                                            |
| spring.flyway.default-schema                                               | YES      | postgres                                         | Default schema name managed by Flyway.                                                             |
| spring.flyway.enabled                                                      | NO       | true                                             | Turn off migrations.                                                                               |
| spring.flyway.locations                                                    | NO       | classpath:db/migration                           | Location of the SQL migration scripts.                                                             |
| spring.flyway.password                                                     | YES      | postgres                                         | Password for the db.                                                                               |
| spring.flyway.schemas                                                      | YES      | postgres                                         | The database scheme names managed by Flyway.                                                       |
| spring.flyway.url                                                          | YES      | jdbc:postgresql://1source-postgres:5432/postgres | The connection url for the relational db.                                                          |
| spring.flyway.user                                                         | YES      | postgres                                         | Username for the db.                                                                               |
| spring.jackson.date-format                                                 | NO       | yyyy-MM-dd'T'HH:mm:ss.SSSX                       | Date format setting.                                                                               |
| starting-trade-event-datetime                                              | YES      | 2023-06-25T09:51:16.111Z                         | A date to retrieve to retrieve the events from.                                                    |

### 4.1 System environments for the build

| Property Name                                                 | System environment                                   |
|---------------------------------------------------------------|------------------------------------------------------|
| camel.timer                                                   | CAMEL_TIMER                                          |
| cloudevents.source                                            | CLOUD_EVENTS_SOURCE                                  |
| notification.enable                                           | NOTIFICATION_ENABLE                                  |
| notification.timer                                            | NOTIFICATION_TIMER                                   |
| route.delegate-flow.contract-initiation.enable                | CI_DELEGATE_FLOW_ENABLE                              |
| route.delegate-flow.contract-initiation.timer                 | CI_DELEGATE_FLOW_TIMER                               |
| route.delegate-flow.update-position.enable                    | UP_DELEGATE_FLOW_ENABLE                              |
| route.delegate-flow.update-position.timer                     | UP_DELEGATE_FLOW_TIMER                               |
| route.position-listener.enable                                | POSITION_LISTENER_ENABLE                             |
| route.position-listener.timer                                 | POSITION_LISTENER_NEW_POSITIONS_TIMER                |
| route.position-listener.update-timer                          | POSITION_LISTENER_UPDATE_POSITIONS_TIMER             |
| route.rerate.enable                                           | RERATE_ROUTE_ENABLE                                  |
| route.rerate.timer                                            | RERATE_ROUTE_TIMER                                   |
| route.returns.enable                                          | RETURN_ROUTE_ENABLE                                  |
| route.returns.timer                                           | RETURN_ROUTE_TIMER                                   |
| route.unilateral-flow.recall-confirmation.enable              | RC_UNILATERAL_FLOW_ENABLE                            |
| route.unilateral-flow.recall-confirmation.timer               | RC_UNILATERAL_FLOW_TIMER                             |
| onesource.auth.api.client-id                                  | 1SOURCE_AUTH_API_CLIENT_ID                           |
| onesource.auth.api.realm                                      | 1SOURCE_AUTH_API_REALM                               |
| onesource.auth.api.server-url                                 | 1SOURCE_AUTH_API_URL                                 |
| onesource.auth.client-id                                      | 1SOURCE_AUTH_CLIENT_ID                               |
| onesource.auth.credentials.secret                             | 1SOURCE_AUTH_CLIENT_SECRET                           |
| onesource.auth.password                                       | 1SOURCE_AUTH_USER_PASSWORD                           |
| onesource.auth.principal-attribute                            | 1SOURCE_AUTH_PRINCIPAL_ATTRIBUTE                     |
| onesource.auth.realm                                          | 1SOURCE_AUTH_REALM                                   |
| onesource.auth.server-url                                     | 1SOURCE_AUTH_URL                                     |
| onesource.auth.username                                       | 1SOURCE_AUTH_USER_NAME                               |
| onesource.base-endpoint                                       | 1SOURCE_ENDPOINT                                     |
| onesource.version                                             | 1SOURCE_VERSION                                      |
| spire.base-endpoint                                           | SPIRE_BASE_ENDPOINT                                  |
| spire.kafka.consumer.bootstrap-server                         | SPIRE_KAFKA_CONSUMER_BOOTSTRAP_SERVER                |
| spire.kafka.consumer.listener.correction-instruction.group-id | SPIRE_KAFKA_CONSUMER_CORRECTION_INSTRUCTION_GROUP_ID |
| spire.kafka.consumer.listener.correction-instruction.topic    | SPIRE_KAFKA_CONSUMER_CORRECTION_INSTRUCTION_TOPIC    |
| spire.kafka.consumer.listener.decline-instruction.group-id    | SPIRE_KAFKA_CONSUMER_DECLINE_INSTRUCTION_GROUP_ID    |
| spire.kafka.consumer.listener.decline-instruction.topic       | SPIRE_KAFKA_CONSUMER_DECLINE_INSTRUCTION_TOPIC       |
| spire.kafka.consumer.listener.recall-instruction.enable       | SPIRE_KAFKA_CONSUMER_RECALL_LISTENER_ENABLE          |
| spire.kafka.consumer.listener.recall-instruction.group-id     | SPIRE_KAFKA_CONSUMER_RECALL_INSTRUCTION_GROUP_ID     |
| spire.kafka.consumer.listener.recall-instruction.topic        | SPIRE_KAFKA_CONSUMER_RECALL_INSTRUCTION_TOPIC        |
| spire.kafka.consumer.password                                 | SPIRE_KAFKA_CONSUMER_PASSWORD                        |
| spire.kafka.consumer.username                                 | SPIRE_KAFKA_CONSUMER_USERNAME                        |
| spire.kafka.producer.auth.key                                 | SPIRE_KAFKA_KEY                                      |
| spire.kafka.producer.auth.secret                              | SPIRE_KAFKA_SECRET                                   |
| spire.kafka.producer.bootstrap-server                         | SPIRE_KAFKA_BOOTSTRAP_SERVER                         |
| spire.kafka.producer.topic                                    | SPIRE_KAFKA_TOPIC                                    |
| spire.password                                                | SPIRE_PASSWORD                                       |
| spire.user-id                                                 | SPIRE_USER_ID                                        |
| spire.username                                                | SPIRE_USERNAME                                       |
| spring.datasource.password                                    | DB_PASSWORD                                          |
| spring.datasource.url                                         | DB_URL                                               |
| spring.datasource.username                                    | DB_USERNAME                                          |
| spring.flyway.default-schema                                  | DB_SCHEMA                                            |
| spring.flyway.password                                        | DB_PASSWORD                                          |
| spring.flyway.schemas                                         | DB_SCHEMA                                            |
| spring.flyway.url                                             | DB_URL                                               |
| spring.flyway.user                                            | DB_USERNAME                                          |
| starting-trade-event-datetime                                 | CAMEL_TIMESTAMP                                      |

## 5.1 Generated DTO based on swagger

For generated 1source DTO classes with OneOf* replace '@JsonTypeInfo(use = JsonTypeInfo.Id.NAME...' with @JsonTypeInfo(
use = Id.DEDUCTION)