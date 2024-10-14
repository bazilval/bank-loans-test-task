# Application for processing bank loans

## Overview
Web application for managing various types of bank loans

## Development

``` bash
./mvnw test
```

``` bash
./mvnw clean spring-boot:run
#http://localhost:8080
```

## Dependencies

The project uses various dependencies managed by Maven. Key dependencies include:

-   Spring Boot and its various starters (web, data JPA, validation, test)
-   SQLite driver
-   Liquibase for database migration
-   SpringDoc OpenAPI for API documentation
-   Lombok for reducing boilerplate code
-   JUnit5 and JSON Unit for testing

## APIs

OpenAPI documentation: `http://localhost:8080/swagger-ui/index.html`
The application exposes the following RESTful APIs:

### Loan Types management API

-   **GET /loan-types**: List all loan types
-   **GET /loan-types/search?query='name**: List all loan types with names matching query 
-   **GET /loan-types/{id}**: Get a loan type by ID
-   **POST /loan-types**: Create a new loan type
-   **PATCH /loan-types/{id}**: Update a loan type by ID
-   **DELETE /loan-types/{id}**: Delete a loan type by ID


### Loan Requests management API

-   **GET /loan-requests**: List all loan requests
-   **GET /loan-requests/search?status='processing'**: List all loan requests with queried status
-   **GET /loan-requests/{id}**: Get a loan request by ID
-   **POST /loan-requests**: Create a new loan request
-   **DELETE /loan-requests/{id}**: Delete a loan request by ID

### Request Steps management API

-   **GET /loan-requests/{requestId}/steps/{orderNum}**: Get a request step by requestId and orderNum
-   **PATCH /loan-requests/{requestId}/steps/{orderNum}/status**: Update status of request step by requestId and orderNum

-------------------------------------------

# Application for processing bank loans

The task involves creating a web application for managing various types of bank loans. <br/>
This will involve building a RESTful service using the Java programming language and the SpringBoot framework. <br/>
It will also require connecting to a relational database and modeling the database itself. SQLite database must be used. <br/>
Development of the UI(Frontend) is not part of the task.

### Managing loan types (required)

A bank employee should have the ability to create different loan types, where each type must have a unique name. Number of different loan types that can be created is not limited or defined in advance.    
Examples of loan types:
- Cash loan
- Real estate loan
- Real estate loan for young people
- Business investment loan
- Loan for solar panels
- etc

When creating a loan type, the bank employee should also define the procedure for processing that loan type.
The procedure consists of a list of arbitrarily defined steps, where each step must have a name, order number, and expected duration in days.
A single step can only be associated with one loan type.

> The procedure can differ significantly for different loan types, primarily based on the risk involved, or the amount borrowed from the bank. So, for example, a cash loan may consist of only one or two steps such as 'Collection of required documents' and 'Salary verification', while real estate loans will probably require several additional steps that may be related to validation of the client's creditworthiness, or real estate insurance.

In addition to creating different loan types, the bank employee should have the ability to search for loan types by name, as well as display individual loan type details, which include the total expected duration of all steps, as well as a list of defined steps for processing that loan type.

**Optional**: implement functionalities for updating and deleting loan types.
> Users(bank employees) creation and maintenance are not part of the requirements.

> You can optionally define additional information for loan types and loan processing steps.

### Issuing loans (optional)

After the client submits a request for a loan, the bank employee creates a request for loan processing by filling in information about the loan request (loan type, first name, last name, loan amount, ...).    
The loan request also contains status, which is changed automatically based on the status of its steps, according to the following rules:
- When a loan request is created, it is initially in the `processing` status, and all its steps are marked as `pending`. Predefined steps are taken from loan type that a request is submitted for, and their expected duration and order cannot be changed.
- It remains in the `processing` status as long as there are uncompleted steps, or until any step changes to the `failed` status.
- The loan request status is changed to `approved` only if all steps are moved to the `successful` status.
  - automatically when the last step changes to `successful` status
- The loan request status is changed to `rejected` as soon as any step changes to the `failed` status.
  - automatically when any step goes into `failed` status

When changing the status of a step, it is necessary to provide the duration of that step expressed in the number of days, as well as the new status.
The status of a step can be changed from `pending` to `successful` or `failed` only if all previous steps (steps with a lower order number) have been successfully implemented.
Once a step is completed(`successful` or `failed`), it cannot be changed afterwards.

> Loan request statuses: [`processing`, `approved`, `rejected`] <br/>
> Loan request step statuses: [`pending`, `successful`, `failed`]

Bank employees should be able to search information about loan requests. This includes information about the status of the request, the total expected and spent time for the request, as well as information about the statuses of all steps, and expected and spent times for each of them.

> The loan type cannot be updated or deleted if there is any loan request for it, regardless of the request status

**Optional**: implement functionalities for searching loan requests by a certain status.

## Setup
- Java 17 or newer is required
- Maven Wrapper (`mvnw`) is part of the application, so you don't need to install Maven
- Run application: `./mvnw clean spring-boot:run`
- By default application will run on port 8080
- OpenAPI(Swagger) is included in project by default
  - OpenAPI UI default path: `http://localhost:8080/swagger-ui/index.html`