# Requirements


### Self Requirements

In this section, we can find the personal requirements I've set for this project:

- It must apply AI to a real-world case and integrate AI within a complex system.
- It should allow me to apply and learn multiple technologies and patterns across all layers (frontend, backend, data, etc.).
- Infrastructure as Code (IaC) is mandatory.
- Observability is also mandatory.
- It should be deployable in multiple ways and environments: local, cloud (AWS and Azure).
- It should be evolvable, and every piece or component should be interchangeable at any time.



### Functional Requirements

This section outlines the key functional requirements of the system, organized by context.

#### Candidates

##### Position Browsing and Application

- **View Available Positions**: Candidates must be able to view a list of all available job positions.
- **View Position Details**: Candidates must be able to view detailed information about a specific position, including:
    - Position title and description
    - Required skills and qualifications
    - Responsibilities
    - Benefits offered
- **Apply to Positions**: Candidates must be able to apply to positions by:
    - Submitting personal information (name, email, phone)
    - Uploading a CV/resume (PDF format)
    - Receiving confirmation of successful application submission



#### Recruitment Context

##### Position Management

- **Create Positions**: Recruiters must be able to create new job positions with:
    - Title and description
    - Required skills with proficiency levels (Beginner/Intermediate/Advanced)
    - Responsibilities
    - Benefits
- **Edit Positions**: Recruiters must be able to modify existing positions, updating any field.
- **View Positions**: Recruiters must be able to view all created positions.
- **Candidate View**: Recruiters must be able to preview how a position appears to candidates.



##### Application Management

- **View Applications**: Recruiters must be able to view all applications for a specific position.
- **Application Analysis**: The system must automatically analyze candidate applications, including:
    - Extracting relevant information from resumes
    - Generating a matching score between candidate skills and position requirements
    - Providing a natural language explanation of the score



##### AI-Powered Analysis

- **Candidate Scoring**: The system must calculate a matching percentage between candidates and positions using AI.
- **Detailed Analysis**: For each candidate, the system must provide:
    - Summary of candidate qualifications
    - Strengths relative to the position
    - Potential concerns or gaps
    - Analysis of how well the candidate matches position responsibilities
    - Detailed skill assessment
    - Suggested interview questions

##### Notifications and Insights

- **High-Score Notifications**: Recruiters must receive notifications when candidates score above a certain threshold (e.g., 60%).
- **Recruitment Insights**: The system must provide key metrics and insights about the recruitment process.



#### Cross-Context Requirements

- **Data Synchronization**: Changes in one context must be automatically propagated to the other context:
    - New positions created in the Recruitment context must appear in the Candidates context
    - Applications submitted in the Candidates context must appear in the Recruitment context

For detailed information about the functionality of both applications, please refer to the [functional documentation](doc/core/functional/funtional.md).

<br />



### Technical requirements for working with this project

The technical requirements for working with this project are detailed [here](doc/core/architecture/tech_reqs.md)
