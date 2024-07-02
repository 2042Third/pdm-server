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

### GraalVM
#### Requirements
- GraalVM:
  `-> java -version
  java version "21.0.3" 2024-04-16 LTS
  Java(TM) SE Runtime Environment Oracle GraalVM 21.0.3+7.1 (build 21.0.3+7-LTS-jvmci-23.1-b37)
  Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 21.0.3+7.1 (build 21.0.3+7-LTS-jvmci-23.1-b37, mixed mode, sharing)
`
### Build
1. ./mvnw clean package -Pnative
2. pm2 start ecosystem.config.js


