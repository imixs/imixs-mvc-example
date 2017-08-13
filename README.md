# imixs-mvc-example

#### NOTE: THIS IS AN EARLY DRAFT VERSION 

The Imixs-mvc-example provides a simple web application using the imixs workflow engine.
You can take this application as a scaffolding for your own web business application based on the [Imixs-Workflow project](http://www.imixs.org).

## Run on Wildfly

See: 

	http://javaakademie.de/blog/java-ee-8-mvc-ozark-wildfly-tomcat
	
	
## 1. Build the Application

The Imixs-mvc-example  is based on Maven to build the project from sources run

    mvn clean install
    
You can also download the application from the [latest release](https://github.com/imixs/imixs-mvc-example/releases).    

## 2. Deploy the Application
To deploy the application successfully, the application sever need to provide a valid database pool named 'jdbc/workflow-db' and a JAAS security configuration named 'imixsrealm'. You will find an installation guide [here](http://www.imixs.org/doc/sampleapplication.html).

### Setup Security Roles
The security concept of imixs-workflow defines default roles:

* org.imixs.ACCESSLEVEL.NOACCESS
* org.imixs.ACCESSLEVEL.READACCESS
* org.imixs.ACCESSLEVEL.AUTHORACCESS
* org.imixs.ACCESSLEVEL.EDITORACCESS
* org.imixs.ACCESSLEVEL.MANAGERACCESS

Each user accessing the Imixs-Workflow engine should be mapped to one of these roles. The user roles can be mapped by configuration from the application server. You will find more information about the general ACL concept of the [Imixs-Workflow Deployent guide](http://www.imixs.org/doc/deployment/security.html).

__NOTE:__ The Imixs-jsf-example is tested with JBoss/Wildfly and GlassFish4/Payara Servers.

## 3. Run the Application
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

After you have build the application, you can build the Docker image with the follwong command:

	docker build --tag=imixs/imixs-mvc-sample .
 
## 3. Starting the Application in a Docker Container

Now you can start the application. The workflow engine needs a SQL Database. Both containers can be started with one docker-compose command

	docker-compose up

See the docker-compose.yml file for details

The Docker container creates user accounts for testing with the following userid/password:

    admin=adminpassword
    manfred=password
    anna=password

After your application was started, upload the ticket.bpmn exampl model:

	curl --user admin:adminpassword --request POST -Tticket.bpmn http://localhost:8080/workflow/rest-service/model/bpmn

and run the application in a web browser:	

	http://localhost:8080/workflow/