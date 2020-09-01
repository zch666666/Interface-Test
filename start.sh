
#/bin/bash

echo "------------------test start-------------------"
cd /opt/Interface-Test;
echo "------------------The test platform clears old data------------------"
mvn clean
echo "------------------Platform code compiling------------------"
mvn compile
echo "------------------Start generating test cases------------------"
mvn exec:java
echo "------------------Start executing the test case------------------"
mvn test
cp ./target/test-output/index.html  /temp/interafce-report