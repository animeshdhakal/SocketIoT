run: build
	java -jar "./server/target/server-1.0.jar"


build:
	mvn clean package shade:shade

test:
	mvn clean test

clean:
	mvn clean -U
