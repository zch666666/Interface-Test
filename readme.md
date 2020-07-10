
Here is the description of the interface automation test access platform, which is developed based on Swagger's Json interface. 
Use Swagger's interface description file to automatically generate test cases and perform interface tests.

Configuration:
Before running this platform, you need to configure related services:
1. Create a database with DDL below.
2. Configure the db.properties database information.
3. Configure the service host to be tested in projectConfig.properties

Q. How to run this platform?
1. mvn compile , mvn exec:java   
(This command is used to execute ApiTestApplication.java, the purpose is to automatically access the Swagger document interface and generate test cases to be stored in the database.)
2. mvn test
(This command is used to read test cases from the database, and call different test functions according to the Http method in the test cases, and then generate a test report.)

The following are some of the functions currently implemented。
1. Grab the interface document information from the Swagger interface, and convert it into a json file and save it in the same directory of the project.
2. Read the saved json file and parse it, then convert it to ApiData and store it in the database.
3. Use TestNG+ExtentsReport to visualize test report

Long-term pending development plan
1. Develop more kinds of Http method test functions.
2. Support specific and correct interface access parameter generation, 
currently only supports random generation of parameters in the specified format.
3. Complete the parser for nested object parameters, which will take a certain amount of time and effort.
4. Use Jenkins and Maven commands for fully automated operation and testing of the platform.

DDL:
create table if not exists interface_cases
(
	id bigint auto_increment
		primary key,
	url varchar(100) not null,
	method varchar(10) not null,
	parameters varchar(10000) null,
	statusCode int default 0 not null,
	summary varchar(1000) null,
	produces varchar(100) null,
	operationId varchar(500) null,
	consumes varchar(100) null,
	tags varchar(100) null,
	createTime timestamp not null,
	isRun tinyint(1) default 0 not null
);

