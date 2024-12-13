# Handling Exceptions in the REST Layer with Spring Boot

## **What is `@ControllerAdvice`?**

`@ControllerAdvice` is a Spring annotation that acts as a global interceptor for handling exceptions in controllers (`@RestController` or `@Controller`). 
It provides a centralized way to capture and process exceptions across the application, ensuring consistent error handling.

---



## **Benefits of Using `@ControllerAdvice`**

- Centralized error handling makes the code cleaner.
- Reusable and consistent responses across controllers.
- Easy integration with logging and monitoring tools (e.g., Sentry, ELK stack).
- Extensible to handle new use cases easily.



## **Setting Up Exception Handling with `@ControllerAdvice`**

### **Steps to Configure:**

1. **Create a global advisor class :**
   For instance, in this project, "project/candidates/backend/services/applications-manager-service/src/main/java/com/jaruiz/casarrubios/candidates/services/applications/adapters/api/rest/ExceptionHandlerController.java"

   This class is annotated with `@ControllerAdvice`. This signals Spring to use this class for handling exceptions.

2. **Define methods for specific exceptions:**
   The methods in this class are annotated with `@ExceptionHandler` to specify which exception each method will handle.

3. **Optionally combine with `@ResponseStatus` or return a `ResponseEntity` object.**

---

### **Implementation Example**

```java
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    public static final String APPLICACION_INCOMPLETE = "INCOMPLETE";
    public static final String PDF_ERROR = "PDFERROR";
    public static final String MISSING_PARAMS = "MISINGPARAMS";

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        logger.error("Exception captured uploading CV: missing params");
        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setErrorCode(MISSING_PARAMS);
        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ApplicationsGeneralException.class)
    protected ResponseEntity<ApplicationErrorDTO> handleApplicationsGeneralException(ApplicationsGeneralException ex) {
        logger.error("Exception captured uploading CV [applicationId = {}, error = {}]", ex.getApplicationId(), ex.getCode());

        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setApplicationId(ex.getApplicationId().toString());
        applicationErrorDTO.setErrorCode(ex.getCode());

        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationIncompleteException.class)
    protected ResponseEntity<ApplicationErrorDTO> handleApplicationIncompleteException(ApplicationIncompleteException ex) {
        logger.error("Exception captured uploading CV: application is incomplete");

        final ApplicationErrorDTO applicationErrorDTO = new ApplicationErrorDTO();
        applicationErrorDTO.setErrorCode(APPLICACION_INCOMPLETE);

        return new ResponseEntity<>(applicationErrorDTO, HttpStatus.BAD_REQUEST);
    }

}
```

---



## **Key Considerations**

### **Exception Hierarchy**
If you have handlers for both generic (`Exception`) and specific exceptions (e.g., `ApplicationsGeneralException`), Spring will prioritize the more specific handler.



### **Consistent Error Responses**

It’s helpful to define a common structure for error responses, such as an `ApplicationErrorDTO` object containing standard error fields like error code, message, and timestamp.



### **Handling Validation Exceptions**
If you use `@Valid` for input validation, you can handle `MethodArgumentNotValidException` like this:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
```

