![Project Status: Active](https://img.shields.io/badge/Project%20Status-Active-green)![Version](https://img.shields.io/badge/Version-MVP-blue)![License](https://img.shields.io/badge/License-MIT-yellow)



# Casarrubios' Project 

This repository serves as a personal portfolio, showcasing various topics and concepts about architecture, software development, and artificial intelligence through a practical use case. 

For this reason, the solutions shown in this repository may be more complex than they should be in a production environment, but as mentioned, the goal of this repository is to use it as a didactical and playground tool.

:warning: **This repository is constantly evolving, so solutions shown here may not be complete or might contain some errors.**



<br />

## Current Status

This project is actively being developed as a **portfolio and learning platform**. The current version (MVP) includes:

- ✅ Fully functional Candidates and Recruitment portals developed using Angular
- ✅ AI-powered scoring (embeddings, cosine distance, LLM for explanation) and analysis of resumés (LLM)
- ✅ Business services developed in multiple technologies like Spring, Quarkus or Python
- ✅ EDA and associated technologies like Kafka or Kafka Streams
- ✅ Change Data Capture (CDC) using Debezium for data synchronization between contexts and managing transactions and multiple tables
- ✅ Outbox pattern implementation
- ✅ OpenAPI
- ✅ Traceability using OpenTelemetry and Jaeger
- ✅ Testcontainers fot testing
- ✅ Docker Compose deployment for local execution
- ✅ CI/CD pipeline with GitHub Actions

#### Short-term Goals

- 🔄 Kubernetes deployment (Helm), Infrastructure as Code (IaC) for AWS and GitOps
- 🔄 Include Schema Registry, Avro, AsyncAPI, Event Catalog / Apicurio
- 🔄 Add an API Gateway (Kong for instance) to docker-compose runtime
- 🔄 MCP (Model Context Protocol) and enhance the AI analysis capabilities with more detailed insights
- 🔄 Add an Internal Development Portal (Backstage) 

#### Mid-term Goals

- 📋 Authentication and authorization (Keycloak)
- 📋 Metrics and logs management
- 📋 Develop a candidate recommendation system
- 📋 Add dashboard for recruitment analytics and KPIs
- 📋 Implement configurable scoring algorithms



> **Note:** This roadmap is tentative and subject to change as the project evolves and new learning opportunities are identified.

<br>

## Project Structure

The structure of the project is the following:

- :open_book: ​**doc/** → Documentation
- :rocket: **platform/**
  - **local/** → folders and files associated with docker-compose runtime environment
  - **k8s/** → folders and files associated with Kubernetes runtime environment (in progress)
- :gear: ​**project/**
  - :student: **candidates/** → all the components (frontend, backend, cdc) associated to candidates context
    - frontend →  includes the application and its backend for frontend
    - backend → business services
    - cdc → connectors for change data capture
  - :office_worker: **recruitment/**  → all the components (frontend, backend, cdc) associated to recruitment context
    - frontend →  includes the application and its backend for frontend
    - backend → business services
    - cdc → connectors for change data capture

<br />

## The Business Case

For this project, I've selected a recruitment process as the business case. The scenario involves a company that publishes job positions on its portal where interested candidates can apply and submit their resumes:

![Recruitment Process Use Case](doc/img/use_case.jpg)

Having participated in many recruitment processes from the recruiter's perspective, I understand the challenges of objectively evaluating a large number of resumes without bias. This process is typically time-consuming and labor-intensive.

**The innovative solution implemented in this project uses an AI-powered scoring algorithm to calculate the matching percentage between candidates and the positions they apply for.**

![AI-Enhanced Recruitment Process](doc/img/use_case_ai.jpg)

This approach helps streamline the recruitment process by automatically identifying the most suitable candidates based on their qualifications and the job requirements.

<br />



## Requirements
In [this document](doc/core/functional/requirements.md) you can find a **detailed requirements list** for this project


<br />

## Functional Overview: candidates and recruitment applications

The project includes **two fully functional applications** that work together to create a complete recruitment ecosystem:

<br />

### Candidates App

This application allows candidates to browse available positions and submit their applications with resumes.

![Candidates Application Map](doc/img/candidates_app_map.png)

<br />

### Recruitment App

This application enables recruiters to manage job positions, review applications, and see AI-generated scoring and analysis.

![Recruitment Application Map](doc/img/recruitment_app_map.png)

For detailed information about the functionality of both applications, please refer to the [functional documentation](doc/core/functional/funtional.md).

<br />

## How to Execute It

### Prerequisites

Before you begin, ensure you have the following installed:

- Docker (latest stable version)
- Docker Compose (latest stable version)
- An OpenAI API Key (required for the AI-powered scoring functionality)

> **Note:** You don't need to build any services or images manually. All necessary images are pre-built and published as [GitHub Public Packages](https://github.com/jaruizes?tab=packages).



### Setup and Execution

1. **Set up your OpenAI API Key**

   Open a terminal and set the environment variable:

   ```shell
   export OPENAI_API_KEY=<your OpenAI API key>
   ```

2. **Start the application**

   In the same terminal, navigate to the local platform directory and start the services:

   ```shell
   cd platform/local
   docker-compose up -d 
   ```

   > **Note:** The initial startup may take several minutes as all services and data are being initialized. Please be patient.

3. **Access the applications**

   Once all services are up and running, you can access the following applications:

   | Application                   | URL                                | Description |
   | ----------------------------- | ---------------------------------- | ----------- |
   | Recruitment App               | http://localhost:9070/private/home | Application for recruiters to manage positions and review applications |
   | Candidates App                | http://localhost:8081/home         | Application for candidates to browse positions and submit applications |
   | Kafka UI                      | http://localhost:8001/             | Interface to monitor Kafka topics and messages |
   | Jaeger                        | http://localhost:16686/search      | Distributed tracing system to monitor and troubleshoot transactions |
   | Minio                         | http://localhost:9001/login        | Object storage service (credentials: minioadmin/minioadmin) |

<br />

## Demo Guide and Technical details

In [this document](doc/core/functional/quick_demo_guide.md) you can find a **detailed demo guide** including technical details

<br />


## Architecture

In [this document](doc/core/architecture/architecture.md) you can find **all the architecture details about this project**

<br>

## CI/CD

The project implements a Continuous Integration and Continuous Deployment (CI/CD) pipeline based on **GitHub Actions**. The pipeline automates the process of building, creating Docker images, and publishing them to **GitHub Packages**:



![CI/CD Pipeline Diagram](doc/img/cicd.png)

<br />

This automation ensures consistent builds and deployments, making it easier to maintain and update the project components.

> **Note:** Currently, the MVP is deployed using Docker Compose. Future releases will incorporate Infrastructure as Code (IaC) and deployment to cloud platforms such as AWS and Azure.

<br />

<br />

## Additional Topics Covered

This section explains various concepts, patterns, and technologies implemented in this project. Each topic includes a link to detailed documentation:

#### Testing & Quality Assurance
- [Testcontainers](doc/topics/testcontainers.md) - Integration testing with containerized dependencies
- [Mutation Testing](doc/topics/mutation-tests.md) - Advanced testing technique to evaluate test suite effectiveness

#### API Development
- [API Contract First - OpenAPI](doc/topics/api-first-openapi.md) - Designing APIs before implementation

#### Frameworks & Libraries
- [Hibernate - Eager/Lazy loading](doc/topics/hibernate-lazy-eager.md) - Data loading strategies in Hibernate
- [Spring Boot - Exceptions Handler](doc/topics/spring-exceptions-handler.md) - Centralized exception handling in Spring Boot
- [Angular: Standalone Components vs Modules)](doc/topics/angular-changes.md) - Standalone Components vs Modules in Angular framework

#### Data Integration
- [CDC using Debezium](https://github.com/jaruizes/debezium) - Change Data Capture implementation with Debezium

#### Google Colab & IA

- [Colab "hypothesis" scoring candidates](https://github.com/jaruizes/AI-CVMatcher)



<br />

> 
