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

    mvn clean install -Pdocker-build
    
You can also download the application from the [latest release](https://github.com/imixs/imixs-mvc-example/releases).    

## 2. Starting the Application in a Docker Container

After you have build the application and the Docker image you can start the application. The workflow engine needs a SQL Database. Both containers can be started with one docker-compose command

	docker-compose up
	

## 3. Run the Application
After deployment you can start the sample application from:

	http://localhost:8080/workflow/api/hello




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

## Development

During development you can use the docker-compose-dev.yml file. This stack maps the src/docker/deployments folder to the wildfly auto deploy directory. 

	$ docker-compose -f docker-compose-dev.yml up
	
you may have to grant the deployment folder first to allow the docker non privileged user to access this location.

	$ sudo chmod 777 src/docker/deployments/

	
	
## Upload the BPMN Model:

After you have successful deployed your application you can upload the Ticket workflow model via the [Imixs-REST Service API](http://www.imixs.org/doc/restapi/index.html). 

<br><br><img src="model-ticket.png">

Use the following curl command to upload the model from your workspace:

    curl --user admin:adminpassword --request POST -Tsrc/workflow/ticket.bpmn http://localhost:8080/app/model/bpmn

The BPMN Model is part of the project and located under /src/workflow/ticket.bpmn
	