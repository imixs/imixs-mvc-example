# imixs-mvc-example

#### NOTE: THIS IS AN EARLY DRAFT VERSION 

The Imixs-mvc-example provides a simple web application using the Imixs-Workflow engine.
You can take this application as a scaffolding for your own web business application based on the [Imixs-Workflow project](http://www.imixs.org).

## Run on Wildfly

This project is based on wildfly. As [Ozark](https://github.com/mvc-spec/ozark) is based on Jersey, the web application need to replace the Wildly RestEasy implementation with the Jersey implementation of JAX-RS. The deployment is described [here](ozark_wildfly.md).

Find also additional help here: 

	http://javaakademie.de/blog/java-ee-8-mvc-ozark-wildfly-tomcat
	
	
## 1. Build the Application

The Imixs-mvc-example  is based on Maven to build the project from sources run

    mvn clean install
    
You can also download the application from the [latest release](https://github.com/imixs/imixs-mvc-example/releases).    


## 2. Run the Application
After deployment you can start the sample application from:

	http://localhost:8080/imixs-mvc-example-0.0.1-SNAPSHOT/getting-started/hello




<br><br><img src="small_h-trans.png">


The Imixs-JSF-Example includes a Docker Container to run the sample application in a Docker container. 
The docker image is based on the docker image [imixs/wildfly](https://hub.docker.com/r/imixs/wildfly/).

To run Sample Application in a Docker container, the container need to be linked to a postgreSQL database container. The database connection is configured in the Wildfly standalone.xml file and can be customized to any other database system. 

## 1. Build the Application
Before you can start the container, build the application from sources


	mvn clean install
	
## 2. Build the Docker Image

After you have build the application, you can build the Docker image with the following command:

	docker build --tag=imixs/imixs-mvc-sample .
 
## 3. Starting the Application in a Docker Container

Now you can start the application. The workflow engine needs a SQL Database. Both containers can be started with one docker-compose command

	docker-compose up

See the docker-compose.yml file for details


