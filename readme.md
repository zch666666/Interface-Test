Hi， @Bin @Brown .This is my plan for the interface autotest platform.

5.29-6.15
Complete Swagger interface document parser and convert into json cases for proper interface testing.

6.16-6.30
To convert json files into test parameters, a suitable bean and convert utils needs to be constructed to match the standard of interface test calls.

7.1-7.15
Complete the process of automatic call of interface test, cooperate with httpclient & TestNG test and generate test report.

Goal: The final form is that the platform is packaged into a jar package, which is monitored by Jenkins. After the code reposity is updated, Jenkins grabs the code and starts the service. The automatic test platform accesses the swagger document page, grabs the document and reads the interface and parameter information,then it will auto generate test params then performs interface testing and outputs a test report.