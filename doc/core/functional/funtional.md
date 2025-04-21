## Functional Detail

As I said before, there are two separated contexts: recruitment and candidates. The URLs to access to those context are:

- **Candidates**: http://localhost:8081/
- **Recruitment**: http://localhost:9070/



### Candidates functionalities

The main view shows a list of positions which candidates can apply:

![candidates-home](img/candidates-home.png)

If we click on a position, we'll get its detail:

![candidates-position_detail](img/candidates-position_detail.png)



If we want to apply to the position, just click on the "Apply" button, fill the requested data and attach a CV (pdf file):

![candidates-apply_to_position](img/candidates-apply_to_position.png)

*Once the application is submitted and stored in the database, the information is replicated to the recruitment context by Change Data Capture (CDC) and Kafka*



### Recruitment functionalities

This is the application main view. In this view, we see a **list of positions to apply**:

![recrutiment-home](img/recrutiment-home.png)



In this view we also have an **insights area**, where important indicators are shown:

![recrutiment-insights](img/recrutiment-insights.png)



And we also have access to **notifications**, for instance, when a candidate is scored with a percentage greater than 60%;

![recrutiment-notifications](img/recrutiment-notifications.png)

In this version, notifications are not stored, just notified.



From this view, we can access to others functionalities:

- Position View
- Create a new position
- Edit Position
- Manage candidate applications



#### Candidate view

If we click in the "eye icon", we'll go to the "Candidate view". By this functionality, we can see a position like if we were a candidate:

![recrutiment-view_position](img/recrutiment-view_position.png)



#### Create a new position

If we click in the button "New Position", we go to the new position view:

![recrutiment-new_position](img/recrutiment-new_position.png)

We need to fill the form and submit it. 

*Once the position (and its requirements, tasks and benefits) is stored in the database, the information is replicated to the candidates context by Change Data Capture (CDC) and Kafka*



#### Edit a position

This view is similar to the new position but, in this case, all the information associated with an existing position is loaded. We can modify any field of the form and submit it:



![recrutiment-edit_position](img/recrutiment-edit_position.png)

*Once the position (and its requirements, tasks and benefits) is updated in the database, the information is replicated to the candidates context by Change Data Capture (CDC) and Kafka*



#### Manage candidate applications

Clicking on this icon (<img src="img/address_book.jpg" width="20"/>) we can check the candidates applying to a position:

![recrutiment-applications_to_position](img/recrutiment-applications_to_position.png)

In this view we have a list of candidates that have applied to the position. For each candidate we have a list of tags about the candidate and the scoring between the candidate skills and the position applied.

If we click on a candidate, we'll go to the scoring and analysis detail



#### Scoring and Analysis detail

This is the core functionality of the system. In this view we have a complete analysis of the candidate and the score associated with the position applied. The first information we get is the main insights, the scoring and the scoring explanation:



![recrutiment-application_detail-scoring](img/recrutiment-application_detail-scoring.png)

We can navigate through the rest of the topics to know more about the candidate. Even a set of possible interview question, regarding to the candidate, are provided:

![recruitment-scoring_summary](img/recruitment-scoring_summary.jpg)

![recruitment-scoring_strengths](img/recruitment-scoring_strengths.jpg)

![recruitment-scoring_concerns](img/recruitment-scoring_concerns.jpg)

![recruitment-scoring_responibilities](img/recruitment-scoring_responibilities.jpg)

![recruitment-scoring_skills](img/recruitment-scoring_skills.jpg)

![recruitment-scoring_questions](img/recruitment-scoring_questions.jpg)