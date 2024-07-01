### Build
1. Put file "application.properties" in "src/main/resources"
2. Build the project with
`./mvnw clean install`
3. Build the docker image with
`docker build -t backend .`
4. Run the docker container with
`docker run -p 8080:8080 backend`

### Maven Goals Run
1. `./mvnw clean install`
2. `./mvnw spring-boot:run`

### Jar Run
1. `./mvnw clean package`
2. `java -jar target/your-application-name.jar`

### Username, Ports
- Files: "application.properties" 


