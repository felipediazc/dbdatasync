README

This program synchronizes update data from database A to database B, using JSON files. This software was created by FELIPE DIAZ CARDONA

* SOFTWARE REQUIREMENTS

You need MAVEN and JAVA 1.8 installed in your PC (MAC/LINUX).

*** HOW TO COMPILE?

    mvn clean install

*** HOW TO PACKAGE AN UPLOAD INTO MAVEN REPOSITORY?

    mvn deploy

*** HOW TO TEST ?

If you want to do the unit and integration test, please use the following instruction:

    mvn test

*** HOW TO DOWNLOAD ALL DEPENDENCIES

    mvn install dependency:copy-dependencies


*** HOW TO CREATE DOCKER CONTAINER FOR UNIT TEST

Go into the sql folder and use the following command:

    docker-compose up

Then, execute the following command to create the tables (in the project root, not into the sql folder)

    mvn sql:execute

    