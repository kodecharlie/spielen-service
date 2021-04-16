# spielen-service

This project illustrates how to assemble a Java microservice that interacts with the AWS SDK.  We leverage Spring Boot for annotation-based service assemblage, maven for dependency management, and Euereka (from Netflix) for the embedded app server that manages the microservice.

## Prerequisites

### Java 11

I've vetted this project against Java 11.  If you want to take your chances against a different version -- Eg, 1.8 -- then visit `pom.xml` and change the following lines accordingly:

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.8.1</version>
      <configuration>
        <source>11</source>
        <target>11</target>
      </configuration>
    </plugin>

### Maven 3.x

Make sure you have installed Maven 3.x, which we use both to build and run the project.

### Set up AWS identity profile

Under `~/.aws/credentials`, set up a profile with AWS access key and secret that this project later will use for calling AWS SDKs.  For example:

    [spielenservice]
    aws_access_key_id = XXXXXXXXXXXXXXXXXXXX
    aws_secret_access_key = XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

Then in `SpielenService.java`, change `AWS_CREDENTIALS_PROFILE_GOES_HERE` with `spielenservice` (ie, your AWS profile).

## Build the project

    $ mvn clean install

The first time you run this command, expect dependencies missing from your environment to be downloaded.

### Port number for service

If you have a preferred port for the service, visit `application.properties` and change the property `server.port` accordingly.  Alternatively, you can specify the command-line option `-Dservice.port=8081`, for example, when you run the service.

### Run the service

    $ mvn spring-boot:run

### Test the service

    $ curl -ivL http://localhost:8081/test-endpoint

The URL request should indicate an HTTP response code of 200 with text reading:

    Done testing World!

That's all for now!