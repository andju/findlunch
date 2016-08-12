# FindLunch
This client server based application allows restaurants to register offers (e.g. for lunch) and helps customer to find these via their smartphone (based on their location).

## Webapp
The web application is based on several technologies:
  * Spring Boot
  * Spring Data JPA with Hibernate
  * Spring MVC
  * Thymeleaf Template Engine
  * Bootstrap
  
### Import into eclipse

In order to import the project into Eclipse, please follow these steps:

1. Open Eclipse
2. Right click on your project explorer and select "Import" --> "Import"
3. In the following menu select "Maven" --> "Existing Maven Projects"
4. Select the extracted "webapp" folder and click "Finish"

### Configure application

Before you can start the application please set up the MariaDB database with the database schema.
Afterwards, please open the "application.properties" file within eclispe (found under src/main/resources) and edit the database and tomcat configuration to match your environment.

### Run application

To start the application, right click on the "App" class found within the base package and select "Run as" --> "Java Application"

## Android App
The Android application is based on the following technologies:
  * Spring for Android
  
### Import into Android Studio

In order to import the project into Android Studio, please follow these steps:

1. Open Android Studio
2. Navigate to "File" and select "Open...".
3. In the following menu select the extracted "FindLunchApp" folder and click "Ok"

### Clean Project

Before you start the application, please consider to clean the project to avoid issues. To do this, you have to navigate to "Build" and select "Clean Project". 

### Run application

To start the application, navigate to "Run" and select "run 'App'". 

### Using a custom Webapp

The Android app gives the ability to define host and port for the connection with the findlunch Webapp in a configuration file named "connection.txt", 
that is located on the external storage of your Android device and must contain the following information:

	host=findlunch.biz.tm
	port=8444
