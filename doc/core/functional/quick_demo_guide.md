# Quick Demo Guide

In this section, we will provide a quick demo of the core functionalities of the recruitment platform. This guide is intended to quickly understand how to use the platform without going into too much detail.



## Prerequisites
- Ensure you have Docker and Docker Compose installed on your machine.
- Clone the repository to your local machine.
- Navigate to 'platform/local' folder
- Run the following command to start the local environment:
```bash
docker-compose up -d
```



> **Note:** Be patient. Wait for all services to start and the data to be replicated (in a Macbook Pro M1 with 16gb RAM it takes about 4-5 min)



## Checking replicated data

Once CDC Service is up and running and the Debezium connectors are installed, initial positions loaded into positions table of recruitment context are replicated to positions table of candidates context. 

This process is known as "**Snapshot**" and consists of taking all the data from the tables registered in the connector to the target tables. If you check the  logs by using " docker-compose logs cdc-service", you'll see something like these lines:

![demo-cdc-snapshot-logs](img/demo/demo-cdc-snapshot-logs.jpg)

You can also use the provided "***PgAdmin***" tool to check directly the positions table of the candidates schema. To do that, go to http://localhost:5433/

![demo-pg_admin-1](img/demo/demo-pg_admin-1.jpg)



> **Note:** User is "admin@admin.com" and password is "admin"



Then, create a new server and check positions table of candidates schema:

![demo-pg_admin-2](img/demo/demo-pg_admin-2.jpg)

<br />

Now, in the "Connection" section, introduce the parameters of Postgres database:

- Hostname: postgresql
- Port: 5432
- Database: applications
- Username: postgres
- Password: postgres

![demo-pg_admin-3](img/demo/demo-pg_admin-3.jpg)

<br />

Click in "Save". Once server is created, go to "*applications*" database, then "*schemas*", select "*applications->tables->positions*" and right click to go to "View/Edit Tables"

![demo-pg_admin-4](img/demo/demo-pg_admin-4.jpg)

<br />

You should be "*some positions*" loaded in the table:

![demo-pg_admin-5](img/demo/demo-pg_admin-5.jpg)

<br />

Once this data is loaded, everything is ready for the demo



## Demo



### Recruitment and Candidates apps

The first step is **open the recruitment app to show the list of the positions already loaded** in the system. 

Open this URL in the browser: http://localhost:9070/

![demo-recruitment-positions_list](img/demo/demo-recruitment-positions_list.jpg)

<br />

As we said before, **all the positions data from recruitment context is replicated automatically to the candidates context by "cdc-service" (Debezium)**. 

If we open the candidates app (http://localhost:8081/), we will see the same data shown in recruitment app:

![demo-candidates-positions_list](img/demo/demo-candidates-positions_list.jpg)





### Recruitment application: creating new position

From the Recruitment Home, click the 'Nueva Position' button. You will see an empty screen ready to be filled with all the data regarding the new position.

![demo-recruiting-new_position](img/demo/demo-recruiting-new_position.jpg)

We are going to fill the required fields:

![demo-recruitment-new_position](img/demo/demo-recruitment-new_position.jpg)

<br />

Once the test data is filled, click the "Guardar cambios" button.  

The data will be saved into position table (and associated tables) in the recruitment conext but **the information will be also replicated automatically to the candidates context by "cdc-service"**. If now, we go to the candidates app home and click the last positions page, will see the new position:

![candidates-new_position-replica-1](img/demo/demo-candidates-new_position-replica-1.jpg)

<br />

If we click the position row, we'll get its details:

![demo-candidates-new_position-replica-2](img/demo/demo-candidates-new_position-replica-2.jpg)

<br />

You can also check the "cdc-service logs" and you should see something like this line (execute '*docker-compose logs cdc-service*'):

```
infrastructure.cdc-service  | [2025-04-29 15:25:59,697] INFO [recruiters-positions-cdc-connector|task-0] 1 records sent during previous 01:08:53.575, last recorded offset of {server=cdc} partition is {lsn_proc=25260128, messageType=INSERT, lsn_commit=25252048, lsn=25260128, txId=830, ts_usec=1745940358784236} (io.debezium.connector.common.BaseSourceTask:352)
```



And you can also **check candidates tables** (*using PgAdmin tool for instance*) to really verify that the data has been replicated. 

![demo-candidates-new_position-replica-3](img/demo/demo-candidates-new_position-replica-3.jpg)

<br />

### Recruitment application: showing position detail / edit position

Now, click in a position to show the position detail. For instance, click on the position with id 10, "Cloud Architect":

![demo-recruiting-position-detail-1](img/demo/demo-recruiting-position-detail-1.jpg)

<br />

If you do scroll down, you'll see tasks and benefits. In this section, you can also update the position data and save it. 



> **Note:** As we have seen before, all the updated data will be replicated to candidates context. So, you can open this position in the candidates app and check that changes have been applied



### Recruitment application: candidate view

From the recruitment application is possible to check how a position is going to be shown to the candidates. Click the ![demo-eye_icon](img/demo/demo-eye_icon.jpg) from any row of the positions list to get this feature:

![demo-recruitment-candidate_view](img/demo/demo-recruitment-candidate_view.jpg)

