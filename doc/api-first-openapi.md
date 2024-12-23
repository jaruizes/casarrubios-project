# API First with OpenAPI

API-First is a design and development approach where APIs are treated as the primary building blocks of an application. What does this mean? Let me try to explain it as simply as possible: in many projects or companies, when an integration is needed between systems, applications or even, third-parties, third parties, the process often involves coding the API first and then generating the documentation for the consumer to be able to invoke it. 

This approach usually generates conflicts because the consumers needs more data or they are not agree with the implementation because it's not useful.

How can we work better? Defining contracts.



## API Contract

The idea is to design and define the API before coding it. In this process, a contract is generated. What does this contact include?

- Operational data: how is the information shared? that means channels, methods, security, errors, quotas, throughput and so on.
- Informational data: what information is going to be shared? that means content and format and schemas



Usually, when we work with  synchronous APIs, we talk about swagger or Open API. **OpenAPI** is a widely adopted specification for designing, documenting, and describing RESTful APIs. It provides a structured way to define an API's operations, endpoints, parameters, responses, authentication methods, and more.

Let's see the project "project/candidates/backend/services/positions-service". If we go to resources folder, we find the file "api-spec/positionsservice-openapi.yaml". This file is the definition and the contract for the Positions API.

You can import this file in the [Swagger Editor](https://editor.swagger.io/) to see the definition in a more readable way that the YAML file. For instance:



- **Operational contract**

  ![operational-contract](img/operational-contract.jpg)



- **Informational contract**

  ![informational-contract](img/informational-contract.jpg)





## Generating code (Spring Boot)

Once we've defined the contract, we can generate code from the YAML file. Why? Because by this way, we are assuring that what we are agreeing in the contract is what we are going to build. Then, if we have a CI/CD pipelines, when the contract changes (the YAML file), those changes will be applied automatically in the code (and probably, it breaks the code)

The first step is configuring the Maven plugin:

```
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <id>generate-api-code</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
              <inputSpec>${project.basedir}/src/main/resources/api-spec/positionsservice-openapi.yaml</inputSpec>
              <output>${project.build.directory}/generated-sources/openapi/</output>
              <generateApiDocumentation>false</generateApiDocumentation>
              <generateModelDocumentation>false</generateModelDocumentation>
              <generatorName>spring</generatorName>
              <generateApiTests>false</generateApiTests>
              <generateModelTests>false</generateModelTests>
              <skipOperationExample>true</skipOperationExample>
              <generateSupportingFiles>false</generateSupportingFiles>
              <configOptions>
                  <useSpringBoot3>true</useSpringBoot3>
                  <booleanGetterPrefix>is</booleanGetterPrefix>
                  <interfaceOnly>true</interfaceOnly>
                  <skipDefaultInterface>true</skipDefaultInterface>
                  <readOnly>true</readOnly>
                  <useTags>true</useTags>
                  <apiPackage>com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest</apiPackage>
                  <modelPackage>com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto</modelPackage>
              </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```



You can check the configuration options [here](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin). The most important options are:

- inputSpec: location of the openapi file
- output: location where it's going to generate classes
- apiPackage: package for the interface
- modelPackage: package for the dots



Now, if you excute, for instance:

```
mvn clean compile
```



Code will be generated in the folder /generated-sources/openapi/ :

![openapi-classes](img/openapi-classes.jpg)



It's generated an interface:

```
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-28T23:52:03.512036+01:00[Europe/Madrid]", comments = "Generator version: 7.10.0")
@Validated
@Tag(name = "positions", description = "Operations about positions")
public interface PositionsApi {
...
}
```



The name of the interface corresponds to the parameter tag in the definition:

```yaml
tags:
  - name: positions
    description: Operations about positions
```



If we define more than one tag, multiple interfaces will be generated.



The name of the method corresponds to the "operationId" parameter in the definition file. For instance, as we've defined this operationId:

```
paths:
    /positions:
      get:
        summary: Find all the open positions
        description: Returns all the open positions for candidates to apply
        operationId: getAllPositions
        tags:
          - positions
        responses:
          '200':
            description: successful operation
            content:
              application/json:
                schema:
                  type: array
                  items:
                    $ref: '#/components/schemas/PositionDTO'
```





A method with the same name is added to the API Interface:

```java
/**
 * GET /positions : Find all the open positions
 * Returns all the open positions for candidates to apply
 *
 * @return successful operation (status code 200)
 */
@Operation(
    operationId = "getAllPositions",
    summary = "Find all the open positions",
    description = "Returns all the open positions for candidates to apply",
    tags = { "positions" },
    responses = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PositionDTO.class)))
        })
    }
)
@RequestMapping(
    method = RequestMethod.GET,
    value = "/positions",
    produces = { "application/json" }
)

ResponseEntity<List<PositionDTO>> getAllPositions()
```



So, once we have generated the interface and the DTOs, we only have to reference it in the implementation and coding the methods:

```java
@RestController
public class PositionsRestController implements PositionsApi {
  
  public ResponseEntity<PositionDetailDTO> getPositionDetail(Long positionId) {
    ...
  }
  
   public ResponseEntity<List<PositionDTO>> getAllPositions() {
     ...
   }  
}
```



## Generating code (Quarkus) -- Review with OpenAPI Generator (Pos Manager) -- Both

Some services within this repository are built using Quarkus. The plugin "openapi-generator-maven-plugin" doesn't work well with Quarkus so we have to use other tool: [Quarkus Openapi Generator](https://github.com/quarkiverse/quarkus-openapi-generator/tree/main). This tool generates code as Quarkus expect to define an endpoint, that's annotated with annotations defined by Jakarta RESTful Web Services (Jakarta.ws.rs.*)



The first step is to add this dependency in pom.xml

```xml
<!-- OpenAPI -->
<dependency>
    <groupId>io.quarkiverse.openapi.generator</groupId>
    <artifactId>quarkus-openapi-generator-server</artifactId>
    <version>2.7.0</version>
</dependency>
```



Then, we can create a folder "/resources/openapi" and put our specifications there but, if we want to use other path, we have to set a property in "applications.properties"

```properties
quarkus.openapi.generator.input-base-dir=<folder containing openapi specs>
```



It's necessary to set another property to specify the file containing the API spec:

```properties
quarkus.openapi.generator.spec=positions-manager-service-openapi.yaml
```



Finally, we have to specify the package for the code generated. It's also set by a property:

```
quarkus.openapi.generator.base-package=com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest
```



If we executed

```
mvn clean compile
```



The code associated with the Openapi definition will be generated in the folder "target/generated-sources/jaxrs". For instance:

```java
/**
 * A JAX-RS interface. An implementation of this interface must be provided.
 */
@Path("/positions")
public interface PositionsResource {
  /**
   * <p>
   * Returns all positions
   * </p>
   * 
   */
  @GET
  @Produces("application/json")
  List<PositionDTO> getAllPositions();

  /**
   * <p>
   * This method creates a new position with the given body information
   * </p>
   * 
   */
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  PositionDetailDTO createPosition(@NotNull PositionDetailDTO data);
```



The last step is to used the code generated:

```java
public class PosManagerRestService implements PositionsResource {

    private final PositionManagerService positionService;

    public PosManagerRestService(PositionManagerService positionService) {
        this.positionService = positionService;
    }

    @Override public List<PositionDTO> getAllPositions() {
        return List.of();
    }

    @Override public PositionDetailDTO createPosition(PositionDetailDTO data) {
        return null;
    }

    @Override public PositionDetailDTO getPositionDetail(long positionId) {
        return null;
    }

    @Override public PositionDetailDTO updatePosition(long positionId, PositionDetailDTO data) {
        return null;
    }
}
```







## Tips

- OpenAPI versions < 3.1.0, don't support 'const' in OneOf, AllOf, AnyOf. If you need to use 'const' in your schema, you can use 'enum' instead.
- SwaggerEditor (v4) deployed to https://editor.swagger.io does not support OAS 3.1. If you're looking for 3.1 support, then please utilize SwaggerEditor (v5) 
which is deployed to https://editor-next.swagger.io/


## References

- [OpenAPI](https://swagger.io/specification/)
- [Zalando API Guidelines](https://opensource.zalando.com/restful-api-guidelines/#100)
- [Quarkus Openapi Generator](https://github.com/quarkiverse/quarkus-openapi-generator/tree/main)



