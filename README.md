# etas
Employee Transport Allocation System

How many way you can create play application.
1. play new
2. activator new
3. sbt new playframework/play-scala-seed.g8

How many way you can run play application in development mode.
1. play run
2. activator run
3. sbt run

How many way you can run play application in development mode.
1. activator start or activator stage (create a jar file) or activator dist (zip the all runable files)
2. sbt start or sbt stage (create a jar file) or sbt dist (zip the all runable files)

Defference between inject and extends?


Performance pitfalls:
1. Web tier state
2. Unnecessary serialization
3. Blocking
4. Blocking Badly

Web tier state
=> Play is statless web application framework be dafault, it alloww to add new server horizantly 
=> State live in cookies
=> Move state to the client or external data store

production build:
1. generate a secret key 
     => playGenerateSecret
2. update theat secret key
	=> To update the secret in application.conf, run playUpdateSecret in the Play console:
3. In the Play console, simply type dist
4. unzip my-first-app-1.0.zip
5. my-first-app-1.0/bin/my-first-app -Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b
