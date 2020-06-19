本项目是基于Swagger接口文档的接口自动化测试系统
1 自动爬取Swagger接口文档，保存所有的接口和参数信息。
2 解析接口信息，转换成json文件保存在本地，Swagger文档被拆分成Models和Interfaces两部分。
3 自动读取解析出的json文件，并从Swagger的信息描述格式转换成符合接口测试的信息格式，并生成测试用例然后保存在数据库中。
4 配合TestNG从数据库中读取测试用例信息并进行接口自动化测试。

This project is an interface automation test system based on Swagger interface documentation
1 Automatically crawl the Swagger interface document and save all interface and parameter information.
2 Parse the interface information, convert it into a json file and save it locally, the Swagger document is split into two parts: Models and Interfaces.
3 Automatically read the parsed json file, and convert from Swagger's information description format to the information format that conforms to the interface test, and generate test cases and save them in the database.
4 Cooperate with TestNG to read test case information from the database and perform interface automation test.