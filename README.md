# etas
Employee Transport Allocation System

javaVersion   := "1.8"
SBTVersion    := 1.0
scalaVersion := "2.12.8"
Play Version := "1.7"
MYSQL DB := Latest

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
