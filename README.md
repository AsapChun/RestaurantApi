# Restaurant API

A restaurant application that accepts menu items from various serving staff in the restaurant. This application must then store the item along with a cooking time for the item to be completed. The application must be able to give a quick snapshot of any or all items on its list at any time. It must also be able to remove specific orders from the list of orders on demand.

More Information on requirements [here](https://github.com/paidy/interview/blob/master/SimpleRestaurantApi.md)

## Installation

IDE: [IntelliJ](https://www.jetbrains.com/idea/)
Framework: [SpringBoot](https://spring.io/projects/spring-boot)

For building and running the application you need the following:
- [JDK 18](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## Running Locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the Application class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:
```shell
mvn spring-boot:run
```

Tests to Run (Important Note: Please ensure Restuarant API is running before trying Integration tests):
- `RestaurantApiIntegrationTest`: Multi-thread integration test simulating 10 threads interacting with Restaurant Api
- `SingleThreadIntegrationTest`: Single Thread Integration test; testing all endpoint behavior
- `OrderControllerUnitTests`: Basic Unit Tests for Order Controller

## Database Configuration
Database choice: MongoDb. ( I believe a NoSQL styled database best suited this assignments use case)

Dummy Restaurant MongoDb instance configuration parameters has been commited intentionally to `application.properties` for added convenience.

## API Usage Overview
- `GET /orders/getAllItems` - returns all items across tables (`No Parameter Required`)
- `GET /orders/getOrdersForTable`- returns items from a specific table (`Parameter Required: List<Table>`)
- `GET /orders/getItemForTable` - returns specifc item from a specific table (`Parameter Required: Integer & String`)
- `POST /orders/createOrder`- creates a order and stores in MongoDB (`Request Body Required: OrderRequestBody`)
- `POST /orders/deleteOrder` - removes a order with a specific UID from MongoDB instace (`Request Body Required: DeleteOrderRequestBody`)

## Future Improvements
- Planned on adding caching by implementing HazelCast (distributed cache). However, given that this application only expects around 10 simulatenous connections, I decided this was over kill unless requirements change in the future.
- If requirements were to change and cookingTime were to become non-static, thus requiring a update to the cooking. I would explore two options: 
    1) Store the cooking time as a Date Object in the future. Any requests coming into the restuarant would trigger a comparison between the current and stored time. Update accordingly.
    2) Deploy a seperate AWS Lamba or GCP Function job to change the stored values in MongoDB.


