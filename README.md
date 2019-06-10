# etas
Employee Transport Allocation System

Dependencies:-
javaVersion   := "1.8"
SBTVersion    := 1.0
scalaVersion := "2.12.8"
Play Version := "1.7"
MYSQL DB := Latest

API Collections: https://www.getpostman.com/collections/ad88395ce5d127e79f92

Developement build:
sbt run

Production build:
1. Reach to play console with below command.
     => sbt
2. Now Generate a secret key in play console.
     => playGenerateSecret
3. update the secret key
     => playUpdateSecret in the Play console OR Manually update into application.conf.
4. Create jar files, using dist command in play console.
     => dist
5. Exit from play console.
6. Goto target/universal location where zip file is created.
7. Now, unzip the etas-1.0-SNAPSHOT.zip file.
8. copy run.sh file from main project direcory to etas-1.0-SNAPSHOT file(unzip folder).
9. Execute the run.sh file in root mode. eg: sudo sh run.sh
10. Application will run on port 82 which is configure into run.sh file. 

DB installation and connection:-
sudo apt-get install mysql-server
sudo mysql_secure_installation -->You need to check MySQL servics running or not.
1. Start the mysql Server ---> sudo service mysql start
2. Stop the mysql Server --->  sudo service mysql stop
3. Status the mysql Server ---> sudo service mysql status
4. Get mysql commnad promt ---> sudo mysql -u root -p
5. CREATE DATABASE etas;
6. use etas
7. Show tables in mysql ---> SHOW TABLES;
8. describe table in mysql ---> DESCRIBE pet;
9. INSERT INTO loction VALUES ('Puffball','Diane','hamster','f','1999-03-30',NULL);
10. ALTER TABLE cab CHANGE `varancy` `vacancy` INT;
