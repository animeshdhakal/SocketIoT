version := $(shell grep -oPm1 "(?<=<version>)[^<]+" "pom.xml")

run: build
	java -jar server/launcher/target/server-$(version).jar

build:
	mvn package


.PHONY: clean

clean:
	mvn clean
