# Info about the project

This directory contains the source code about the project developed during the Web Application course.

## Description of the project:

The primary objective of our web application, named PL8S, is to simplify the food ordering process at festivals by providing a user-friendly platform that eliminates the need for long queues at the counter. This web service is designed to let users autonomously compile their order, similar to the touch-screen ordering systems found in popular fast-food chains, but with the added convenience of being accessible directly from the user's device.

The application allows customers to place orders at multiple restaurants through a unified interface that can be easily accessed from home or on-site, thereby making the food ordering process more efficient and accessible.

The project is developed in PostgreSQL, Java, HTML, JavaScript and CSS.

The developers of the project are:

- Antonutti Manuel 2130332
- Carlesso Daniel 2088626
- Frigione Luigi 2060685
- Shams Mahshid 2122316
- Ursino Nicola 2119984

# Instructions to compile and run the application

## 1. Install docker
https://docs.docker.com/engine/install/

## 2. Build the images
The *docker-compose.yaml* file contains 4 services:  

- mvn
- web
- db
- pgadmin

### 2.1 The service *mvn* 
It can be used to **compile** the code (in general to execute any maven command) if you don't have a maven installation on your system. Please uncomment it if it is commented on the docker-compose.yaml file!
```
docker compose run --rm mvn clean package
```

### 2.2 The service *web* 
It is the *application* service.
If you modified the source code and you want to run the new version just run the following commands:

```
docker compose build web
docker compose up web
```

### 2.3 The service *db* 
It is the *database service*.
You don't need to manually run it since it is a dependency for both the web and pgadmin services.
Docker will run it for you whenever you need either web nor pgadmin.  
**WARNING:** The first time you run this service a new database is created. This operation will be performed just the first time you run it (actually the init scripts will be run every time the directory *'./volumes/pl8sdb/data'* is an empty directory).  
If you need to work on the database initialization you will need to compile the database image too
```
docker compose build db
```
and to clean the *'data'* directory by
```
make clean_db
```
Make sure you have copied your scripts in the *'/docker-entrypoint-initdb.d'* folder of the image.  
You can see an example in the *'./dockerfiles/db.dockerfile'* where in the COPY instruction the left path is on your host machine and the right one is the one inside the docker image.  
If you don't have make installed you can install it or just delete the *'./volumes'* directory (that is what actually *make clean* does).  
**Note** that adding a new COPY instruction into the dockerfile with your script has the following implication:
all the scripts into the *'/docker-entrypoint-initdb.d'* directory will be executed in lexicographical order (means that 10 comes before 2). Make sure to rename them properly i.e. if you have two scripts, one creating the tables and the other inserting into them you need to execute first the creation script and then the insert one.  
**Name them properly**.

### 2.4 The service *pgadmin* 
It is the *database interface service*.
It is just a user interface to interact with the database.
To run it, as before:  
```
docker compose up pgadmin
```

## 3. Navigate
Once you have built and run your images you can find

- The application available on *'localhost:80/pl8s'* (you can just navigate *localhost* since port 80 is the default port and tomcat is configured to redirect *'localhost/'* to *'localhost/pl8s'*).
- The pgadmin interface on *'localhost:8080'*

# IMPORTANT!!!
If you build the application without using docker (using your local maven installation) you have to be careful. The default war file name is PL8S-1.0 while docker renames it pl8s. This can change the way you navigate the application resources.

## 4. Testing with Postman
The repository contains *pl8s_postman_collection.json* and *pl8s_postman_environment.json* files into the code folder.
The first one contains all the API calls definitions, the second instead contains useful variables definitions (credentials, tokens etc.).

They can be both imported.

The protected resources need a valid JWT token to be served.

To generate the token you have to call the Login API with the correct user and password.
Depending on the role you need, you can use the appropriate credentials stored in the variables:  

- **Admin**: *{{admin_username}}*, *{{admin_pwd}}*
- **Manager**: *{{manager_username}}*, *{{manager_pwd}}*
- **Customer**: *{{customer_username}}*, *{{customer_pwd}}*

Those are pre-initialized users that you can use for testing the application (whose definition can be found in the festival-insert.sql).
The result of the login operation is a JWT token that you can store in the current value of the correct variable: *{{admin_token}}*, *{{manager_token}}*, *{{customer_token}}*.
In this way all the other APIs are ready to run authenticated.
