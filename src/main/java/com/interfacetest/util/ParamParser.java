package com.interfacetest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParamParser {
    private static ObjectMapper objectMapper=new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ParamParser.class);
    private static Map<String,JsonNode> paramJsonMap=new HashMap<>();

    /*
    "parameters": [
    {
        "schema": {
        "$ref": "#/definitions/User"
    },
        "in": "body",
            "name": "user",
            "description": "user",
            "required": true
    }
		],


	{
  "userId": "string",
  "userInfo": {
    "sex": 0,
    "userAge": 0,
    "userInfoDetail": {
      "userGrades": [
        0
      ]
    },
    "userName": "string"
  }
}
    */

    //接收swagger接口信息json，解析并返回可以直接发起api请求的json参数
    public static JsonNode interfaceParamParser(JsonNode interfaces) throws IOException {
        //case1: no param is sent in
        //case2: has param,[]0 1 2 3
        // case 2.1 object,has key "schema" :   key:"schema" value:"   "$ref":"#/definitions/???"  "
        //case 2.2 string: has key "type":"string":
        //  special case,has key "type":"string" and  "format":"uuid"
        //case 2.3 integer has key "type":"integer":

        JsonNode resultNode=objectMapper.createObjectNode();
        //Extract parameters array
        ArrayNode params = (ArrayNode) interfaces.get("parameters");

        for(JsonNode objectNode:params){
            //param is object
            if(objectNode.get("schema")!=null){
                //读取$ref并根据他的值找出需要的类的json，并根据相应的类的json文件生成类的各项参数。
                JsonNode schemaNode=objectNode.get("schema");
                //example:{"$ref":"#/definitions/User"}
                String objectPath=schemaNode.get("$ref").toString();
                String objectName=objectPath.split("/")[2];
//                System.out.println(schemaNode.toString());
//                System.out.println(objectName);
//                System.out.println("schema YES!");
                //convert swagger interface schema in to json for API test
                //schemaNode= paramParse(objectName);
                if(paramJsonMap.isEmpty())
                    paramJsonMap=paramMapParser();
                else
                    schemaNode=paramJsonMap.get(objectName);

                resultNode=merge(resultNode,schemaNode);
            }

            //param is not object
            else if(objectNode.get("type")!=null){

                //"type":"integer"
                if (objectNode.get("type").asText().equals("integer")){
                    JSONObject integerJson=new JSONObject();
                    integerJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomInt());
                    JsonNode integerNode=objectMapper.readTree(integerJson.toString());

                    resultNode=merge(resultNode,integerNode);
                }

                //"type":"string"
                else if (objectNode.get("type").asText().equals("string")){

                    //"format":"uuid"
                    if (objectNode.path("format").asText().equals("uuid")|objectNode.path("format").asText().equals("UUID")){
                        JSONObject uuidJson=new JSONObject();
                        uuidJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomUUID());
                        System.out.println("uuidjson="+uuidJson.toString());
                        JsonNode uuidNode=objectMapper.readTree(uuidJson.toString());

                        resultNode=merge(resultNode,uuidNode);
                    }
                    //no key "format" just normal string
                    JSONObject strJson=new JSONObject();
                    strJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomStr());
                    JsonNode uuidNode=objectMapper.readTree(strJson.toString());
                    resultNode=merge(resultNode,uuidNode);
                }

            }
        }
        //return result;
        return resultNode;
    }

    public static void main(String[] args) {
//        System.out.println( interfaceParamParser(FileUtil.getInterfaceJson("-demo-addUserInfo"))
//);
        System.out.println(paramMapParser().toString());



    }

//    public static JsonNode paramParse(String objectName)  {
//        Map<String,JsonNode> paramJsonMap;
//        JsonNode resultNode;
////        Map<String,JsonNode> mamm=FileUtil.getFilesToMap(System.getProperty("user.dir") +"\\target\\jsonfiles\\models");
//        Map<String,JsonNode> modelsSwaggerMap=FileUtil.getFilesToMap(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.models"));
//        paramJsonMap=paramMapParser(modelsSwaggerMap);
//        System.out.println(modelsSwaggerMap);
//        System.out.println(paramJsonMap);
//        resultNode=paramJsonMap.get(objectName);
//        return resultNode;
//    }

    //read swagger object map and output param json map.
    private static  Map<String,JsonNode> paramMapParser(){
        Map<String,JsonNode> modelsSwaggerMap=FileUtil.getFilesToMap(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.models"));
        //Store classes without object-type member variables
        Map<String,JsonNode> swaggerModelsWithOutObj=new HashMap<>();
        //Store classes with object-type member variables
        Map<String,JsonNode> swaggerModelsWithObj=new HashMap<>();
        Map<String,JsonNode> resultMap=new HashMap<>();

        //Split param map into two map,One has object-type member variables and the other does not.
        for (Map.Entry<String, JsonNode> entry : modelsSwaggerMap.entrySet()) {
            JsonNode jsonNode=entry.getValue();
//            JsonNode properties=jsonNode.get("properties");
//            JsonNode objRef=properties.findValue("$ref");
            //if (objRef!=null){
            String jsonStr=jsonNode.toString();
            if (jsonStr.contains("$ref")){
                swaggerModelsWithObj.put(entry.getKey(),entry.getValue());
            }
            else{
                swaggerModelsWithOutObj.put(entry.getKey(),entry.getValue());
            }
        }

        //Map<String,JsonNode>  modelsJsonWithOutObj=mapWithOutObjParser(swaggerModelsWithOutObj);
        //resultMap.putAll(modelsJsonWithOutObj);

        //Map<String,JsonNode> ObjWithObjJsonMap=mapWithOutObjParser(mapWithObj);
        //resultMap.putAll(ObjWithObjJsonMap);

        System.out.println("mapWithoutObj: "+swaggerModelsWithOutObj.toString());
        System.out.println("mapWithObj: "+swaggerModelsWithObj.toString());

        return resultMap;
    }

    /**
     * {
     *   "type": "object",
     *   "title": "UserInfoDetail",
     *   "properties": {
     *     "userGrades": {
     *       "type": "array",
     *       "items": {
     *         "format": "int32",
     *         "type": "integer"
     *       }
     *     }
     *   }
     * }
     * @param swaggerModelsWithOutObj
     * @return Map<String,JsonNode>
     */
    private static Map<String,JsonNode> mapWithOutObjParser(Map<String,JsonNode> swaggerModelsWithOutObj){
        Map<String,JsonNode> resultMap;
        for (Map.Entry<String, JsonNode> entry : swaggerModelsWithOutObj.entrySet()) {

        }
        return null;
    }

    private static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {

            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, updateNode.get(fieldName));
            }
            else {
                if (mainNode instanceof ObjectNode) {
                    // Overwrite field
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).put(fieldName, value);
                }
            }

        }

        return mainNode;
    }
}
