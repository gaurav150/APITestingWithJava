# APITestingDemoProject

Java API automation project using **Rest Assured**, **TestNG**, and **Jackson** for REST API testing. Tests call public practice APIs (for example, [Rahul Shetty Academy](https://rahulshettyacademy.com)).

## Prerequisites

Install these before running the project:

| Tool | Required Version |
|------|------------------|
| **Java JDK** | 21 |
| **Apache Maven** | 3.6+ (3.9+ recommended) |

Verify your setup:

```bash
java -version
# Expected: java version "21.x.x"

mvn -version
# Expected: Apache Maven 3.x and Java version: 21
```

If `java -version` does not show Java 21, set `JAVA_HOME` to your JDK 21 installation before running Maven:

```bash
# macOS example
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

## Install Dependencies

Clone the repository and install all Maven dependencies from the project root:

```bash
git clone <repository-url>
cd APITestingDemoProject
mvn clean install
```

This downloads dependencies, compiles the project, and runs the default Maven lifecycle. To only download dependencies without running tests:

```bash
mvn dependency:resolve
```

Dependencies are defined in `pom.xml` and managed by Maven automatically.

## Project Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| [Rest Assured](https://rest-assured.io/) | 6.0.1 | REST API requests and assertions |
| [TestNG](https://testng.org/) | 7.12.0 | Test framework and annotations |
| [Hamcrest](https://hamcrest.org/) | 3.0 | Matchers for response validation |
| [Jackson Databind](https://github.com/FasterXML/jackson-databind) | 3.2.1 | JSON serialization and deserialization |
| [Lombok](https://projectlombok.org/) | 1.18.46 | Boilerplate reduction for POJO classes |

## Project Structure

```
APITestingDemoProject/
├── pom.xml                          # Maven config and dependency versions
├── README.md
└── src/main/java/org/example/
    ├── EcommerceAPITest.java        # E-commerce API flow (login, product, order)
    ├── SpecBuilderTest.java         # RequestSpecBuilder examples
    ├── SerializationTest.java       # POJO serialization examples
    ├── OAuthExampleTest.java        # OAuth flow example
    ├── StaticJsonHandling.java      # Static JSON parsing
    ├── DynamicJson.java             # Data-driven JSON tests
    ├── SumValidationsOfResponse.java
    └── ...                          # POJO model classes
```

> **Note:** Test classes live under `src/main/java`, not `src/test/java`. Use an IDE or the TestNG CLI commands below to run them.

## How to Run Tests

### Option 1: IntelliJ IDEA (Recommended)

1. Open the project folder in IntelliJ IDEA.
2. Wait for Maven to import dependencies.
3. Enable **Annotation Processing** for Lombok:  
   `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing`
4. Right-click a test class or method (for example, `EcommerceAPITest#createOrder`) and choose **Run**.

### Option 2: TestNG from the Command Line

Compile the project and build the classpath:

```bash
mvn clean compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
```

Run a single test class:

```bash
java -cp "target/classes:$(cat target/cp.txt)" org.testng.TestNG \
  -testclass org.example.EcommerceAPITest
```

Run a single test method:

```bash
java -cp "target/classes:$(cat target/cp.txt)" org.testng.TestNG \
  -testclass org.example.EcommerceAPITest \
  -methods createOrder
```

Run another test class:

```bash
java -cp "target/classes:$(cat target/cp.txt)" org.testng.TestNG \
  -testclass org.example.SpecBuilderTest
```

### Option 3: Compile Only

To compile without running tests:

```bash
mvn clean compile
```

## Test Classes Overview

| Test Class | Description |
|------------|-------------|
| `EcommerceAPITest` | Full e-commerce flow: login, add product, create order, delete product and order |
| `SpecBuilderTest` | Reusable request specification with Rest Assured |
| `SerializationTest` | Serialize/deserialize Java objects to JSON |
| `OAuthExampleTest` | OAuth authorization code flow |
| `StaticJsonHandling` | Parse and validate static JSON responses |
| `DynamicJson` | Data-driven tests with TestNG `@DataProvider` |
| `SumValidationsOfResponse` | Response validation examples |

## E-Commerce Test Setup

`EcommerceAPITest` calls live APIs and requires:

1. **Internet access** — tests hit `https://rahulshettyacademy.com`.
2. **Valid login credentials** — configured in `getLoginCredentials()` inside `EcommerceAPITest.java`.
3. **Product image file** — `addNewProduct()` uploads an image. Update the path in the test to a valid file on your machine:

```java
.multiPart("productImage", new File("/path/to/your/productImage.jpeg"));
```

Suggested test order for the full e-commerce flow:

1. `loginToWebsite`
2. `addNewProduct`
3. `createOrder`
4. `deleteProductFromUI`
5. `deleteOrderFromCart`

Or run `deleteOrderFromCart` alone — it chains the earlier steps internally.

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `Unable to locate a Java Runtime` | Install JDK 21 and set `JAVA_HOME` |
| Lombok errors (`cannot find symbol`) | Enable annotation processing in your IDE |
| `Wrong Product ID` on create order | Ensure the request body uses `productOrderedId`, not `productOrderId` |
| Product add fails | Check that the image file path exists and login credentials are valid |
| `No tests matching pattern` with `mvn test` | Tests are in `src/main/java`; use IntelliJ or the TestNG CLI commands above |

## Build Commands Reference

```bash
# Download dependencies and compile
mvn clean compile

# Full build (compile + package)
mvn clean package

# Clean build output
mvn clean
```
