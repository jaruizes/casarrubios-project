# Working with Testcontainers



## What is Testcontainers?

Testcontainers is a library to easily include some pieces of infrastructure that we need in our projects when we want to make integration tests. It's based on containers (Docker).

By using Testcontainers, we can include real databases, message brokers, web browsers, or just about anything that can run in a Docker container.

The official page is this: https://testcontainers.com/



## Testcontainers in practice

You can see that Testcotainers is used in several places within this repository. To show how it works, the first example I'm going to explain is the service associated to open positions for candidates (project/candidates/backend/services/positions-service).



### Dependencies

The first step is to include Testcontainers dependencies in pom.xml:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```



The main dependency is "postgresql". This dependency is when the magic happens because it's provide a postgresql docker container to be used during testing phase. It means that:

- we dont't have to configure an external database
- the container is totally associated to testing phase so we don't care about removing the database when tests are finished



We can check the list of containers availables in [Testcontainers modules](https://testcontainers.com/modules/)



### Initializing the (postgresql) container

Once we have added the dependencies, we can configure an script to initialize Postgresql with the data we need to our tests. It's so easy, just we have to create a schema.sql file in the resources folder:



![schema-sql](../img/schema-sql.jpg)



We also have to include this property in application.properties:

```properties
spring.sql.init.mode=always
```



### Creating integration tests

Now, let's use it in our integration tests. In this case, my integration tests are in the file: src/test/java/com/jaruiz/casarrubios/candidates/services/positionsservice/PositionsServiceIT.java



The first step is to define which image we want to use:

```Java
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
  	"postgres:16-alpine"
);
```



In this case, I've chosen "postgres:16-alpine"



Then, we have to initialize the container when the tests start:

```Java
@BeforeAll
static void beforeAll() {
    postgres.start();
}

@AfterAll
static void afterAll() {
    postgres.stop();
}
```



And that's all. The next step is to code the integration test. For instance:

```Java
@Test
public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
    final Response response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/positions");

    final PositionDTO[] positions = response.getBody().as(PositionDTO[].class);
    Assertions.assertEquals(1, positions.length);
    Arrays.asList(positions).forEach(position -> {
        Assertions.assertTrue(position.getId() > 0);
        Assertions.assertTrue(position.getTitle() != null && !position.getTitle().isEmpty());
        Assertions.assertTrue(position.getDescription() != null && !position.getDescription().isEmpty());
    });
}
```



The first time the test is executed, the image will be downloaded:

![testcontainers-download-image](../img/testcontainers-download-image.jpg)



## Testcontainers with Podman (MacOS)

I'm executing Testcontainers with Podman in my Macbook (Apple Silicon). I realized that there were many containers (Ryuk) running when all the test executions had ended. Those containers were created by Testcontainers:

```
23:28:22.349 [main] INFO tc.testcontainers/ryuk:0.11.0 -- Creating container for image: testcontainers/ryuk:0.11.0
23:28:22.408 [main] INFO tc.testcontainers/ryuk:0.11.0 -- Container testcontainers/ryuk:0.11.0 is starting: 56c7495c1513582600542313277676aeae76ef1a0dc1772a2bd92792b7af6506
23:28:22.914 [main] INFO tc.testcontainers/ryuk:0.11.0 -- Container testcontainers/ryuk:0.11.0 started in PT0.56438S
```



So, each time a test was executed, a new Ryuk container was created.



**What is Ruyk?**

As the Testcontainers documentation said: "is responsible for container removal and automatic cleanup of dead containers at JVM shutdown".

It's funny because it doesn't remove itself...

![ryuk_container](../img/ryuk_container.jpg)



**How I fixed it?**

TL;DR; Reading Testcontainers [documentation](https://java.testcontainers.org/features/configuration/#disabling-ryuk)

Basically, the solution is set an environment variable to disable the creation of ryuk containers (if you need it)

```
TESTCONTAINERS_RYUK_DISABLED=true
```

