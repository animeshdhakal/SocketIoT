
run: build
	java -jar server/launcher/target/server-1.0.jar

build:
	mvn package


.PHONY: clean

clean:
	mvn clean