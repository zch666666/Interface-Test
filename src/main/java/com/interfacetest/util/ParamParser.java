package com.interfacetest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParamParser {
    private static ObjectMapper objectMapper=new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ParamParser.class);
    private static Map<String,JsonNode> paramJsonMap;

    static {
        paramJsonMap=paramMapParser();
    }

    //接收swagger接口信息json，解析并返回可以直接发起api请求的json参数
    public static JsonNode interfaceParamParser(JsonNode interfaces) throws IOException {
        //case1: no param is sent in
        //case2: has param,[]0 1 2 3
        // case 2.1 object,has key "schema" :   key:"schema" value:"   "$ref":"#/definitions/???"  "
        //case 2.2 string: has key "type":"string":
        //  special case,has key "type":"string" and  "format":"uuid"
        //case 2.3 integer has key "type":"integer":

        JsonNode resultNode=objectMapper.createObjectNode();

        if (interfaces.findValue("parameters") == null)
            return resultNode;

        //Extract parameters array
        ArrayNode params = (ArrayNode) interfaces.findValue("parameters");
        JsonNode tempNode=objectMapper.createObjectNode();
        for(JsonNode objectNode:params){
            //param is object
            if(objectNode.get("schema")!=null){
                //读取$ref并根据他的值找出需要的类的json，并根据相应的类的json文件生成类的各项参数。
                JsonNode schemaNode=objectNode.get("schema");

                //example:{"$ref":"#/definitions/User"}
                String objectPath=schemaNode.get("$ref").toString().replace("\"","");
                String objectName=objectPath.split("/")[2];
                //convert swagger interface schema in to json for API test
                tempNode=paramJsonMap.get(objectName);
            }

            //param is not object
            else if(objectNode.get("type")!=null){

                //"type":"integer"
                if (objectNode.get("type").asText().equals("integer")){
                    JSONObject integerJson=new JSONObject();
                    integerJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomInt());
                    tempNode=objectMapper.readTree(integerJson.toString());
                }

                //"type":"string"
                else if (objectNode.get("type").asText().equals("string")){

                    //"format":"uuid"
                    if (objectNode.path("format").asText().equals("uuid")|objectNode.path("format").asText().equals("UUID")){
                        JSONObject uuidJson=new JSONObject();
                        uuidJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomUUID());
                        tempNode=objectMapper.readTree(uuidJson.toString());
                        //resultNode=merge(resultNode,tempNode);
                    }else{
                        //no key "format" just normal string
                        JSONObject strJson=new JSONObject();
                        strJson.put(objectNode.get("name").textValue(),RandomUtil.getRandomStr());
                        tempNode=objectMapper.readTree(strJson.toString());
                    }
                }
            }
            resultNode=merge(resultNode,tempNode);
        }
        //return result;
        return resultNode;
    }

    //read swagger object map and output param json map.
    private static  Map<String,JsonNode> paramMapParser() {
        Map<String,JsonNode> modelsSwaggerMap=FileUtil.getFilesToMap(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.models"));
        //Store classes without object-type member variables
        Map<String,JsonNode> swaggerModelsWithOutObj=new HashMap<>();
        //Store classes with object-type member variables
        Map<String,JsonNode> swaggerModelsWithObj=new HashMap<>();
        Map<String,JsonNode> resultMap=new HashMap<>();

        //Split param map into two map,One has object-type member variables and the other does not.
        for (Map.Entry<String, JsonNode> entry : modelsSwaggerMap.entrySet()) {
            JsonNode jsonNode=entry.getValue();
            String jsonStr=jsonNode.toString();
            if (jsonStr.contains("$ref")){
                swaggerModelsWithObj.put(entry.getKey(),entry.getValue());
            }
            else{
                swaggerModelsWithOutObj.put(entry.getKey(),entry.getValue());
            }
        }

        Map<String,JsonNode>  modelsJsonWithOutObj=mapWithOutObjParser(swaggerModelsWithOutObj);
        resultMap.putAll(modelsJsonWithOutObj);

        //Obj With Obj JsonMap need further develop

//        Map<String,JsonNode> ObjWithObjJsonMap=mapWithObjParser(swaggerModelsWithObj);
//        resultMap.putAll(ObjWithObjJsonMap);

        return resultMap;
    }

    private static Map<String,JsonNode> mapWithOutObjParser(Map<String,JsonNode> swaggerModelsWithOutObj)  {
        Map<String,JsonNode> resultMap=new HashMap<>();

        for (Map.Entry<String, JsonNode> entry : swaggerModelsWithOutObj.entrySet()) {
            Map<String,JsonNode> tempMap=new HashMap<>();
            String nodeName=entry.getKey();
            JsonNode modelSWJsonNode=entry.getValue();
            JsonNode properties=modelSWJsonNode.get("properties");
            JsonNode resultNode;
            JSONObject propertiesJson=new JSONObject(properties.toString());
            JSONObject resultJson=new JSONObject();

            for (Iterator<String> it = propertiesJson.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject property= propertiesJson.getJSONObject(key);
                String typeValue= property.getString("type");
                switch (typeValue){
                    case "array":
                        JSONObject arrayInfo=property.getJSONObject("items");
                        String elementType=arrayInfo.getString("type");

                        if(elementType.equals("string")){
                            List<String> array=new ArrayList<>();
                            array.add(RandomUtil.getRandomStr());
                            resultJson.put(key,array);
                        }else if (elementType.equals("integer")){
                            List<Integer> array=new ArrayList<>();
                            array.add(RandomUtil.getRandomInt());
                            resultJson.put(key,array);
                        }
                    break;

                    case "string":
                        //special string case,like date or uuid etc...
                        if (property.has("format")){
                            String format= property.getString("format");
                            if (format.equals("date-time")){
                                SimpleDateFormat sdf1 =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                                resultJson.put(key,sdf1.format(new Date()));
                            }else if (format.equals("uuid")){
                                resultJson.put(key,RandomUtil.getRandomUUID());
                            }
                        }
                        //just normal string
                        else
                            resultJson.put(key,RandomUtil.getRandomStr());

                        break;
                    case "integer":
                        resultJson.put(key,RandomUtil.getRandomInt());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + typeValue);
                }

            }

            try {
                resultNode=objectMapper.readTree(resultJson.toString());
                tempMap.put(nodeName,resultNode);
                resultMap.putAll(tempMap);
            }catch (JsonProcessingException e){
                logger.info("error occur in mapWithOutObj Parser");
            }
        }
        return resultMap;
    }


    //need further develop
    /*
    private static Map<String,JsonNode> mapWithObjParser(Map<String,JsonNode> swaggerModelsWithObj){
        Map<String,JsonNode> resultMap;
        for (Map.Entry<String, JsonNode> entry : swaggerModelsWithObj.entrySet()) {

        }
        return null;
    }
     */

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
//                    ((ObjectNode) mainNode).put(fieldName, value);
                    ((ObjectNode) mainNode).replace(fieldName, value);
                }
            }

        }
        return mainNode;
    }
}
