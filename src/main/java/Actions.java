import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Scanner;

public class Actions {
    public int UI() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter collection name");
        String fileName = in.nextLine();//Пользователь вводит коллекцию, с которой собирается работать

        //проверяем, есть ли у пользователя доступ для работы с ней
        boolean exit = false;
        while(!exit){
            System.out.print(fileName+"->");
            String action = in.nextLine();
            String name;
            String json;
            String[] splitString = action.split("\\(", 2);
            switch(splitString[0]){
                case("insert"):
                    String newString = action.substring(action.indexOf("(")+1, action.lastIndexOf(")"));
                    splitString = newString.split(":", 2);
                    name = splitString[0].replace(" ", "");
                    name = name.replace("\"", "");
                    json = splitString[1].replace(" ", "");
                    createCollection(fileName);
                    if(insert(fileName, name, json)==1){
                        exit = true;
                    }
                    return 1;
                case("change"):
                    String changedJson = action.substring(action.indexOf("(")+1, action.lastIndexOf(")"));
                    if(change(changedJson, fileName)==1){
                        System.out.println("11");
                    }
                    return 1;
                case("delete"):
                    String docName = action.substring(action.indexOf("(")+1, action.lastIndexOf(")"));
                    if(deleteDoc(docName, fileName)==1){
                        System.out.println("Deleted successful");
                        return 1;
                    }
                    else{
                        return 0;
                    }
                case("show"):
                    return showDocList(fileName);
                case("find"):
                    String key = action.substring(action.indexOf("(")+1, action.lastIndexOf(")")).split(",")[0].trim();
                    String value = action.substring(action.indexOf("(")+1, action.lastIndexOf(")")).split(",")[1].trim();
                    if(find(fileName, key, value)==1){

                    }else{
                        System.out.println("Nothing founded");
                    }
            }
        }

        return 1;
        //find();
        // count();
    }
    public int showDocList(String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject.keySet());
        }
        catch(Exception exp){
            System.out.println("Collection is empty or doesn't exists");
            return 0;
        }
        return 1;
    }
    public int change(String changedJSON, String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) {
            Object obj = jsonParser.parse(reader);
            Object changedObj = jsonParser.parse(changedJSON);
            JSONObject changedJsonObject = (JSONObject) changedObj;
            JSONObject jsonObject = (JSONObject) obj;
            if(changedJsonObject.keySet().size()>1){
                System.out.println("Only one doc may be changed");
                return 0;
            }
            if((jsonObject).keySet().contains(changedJsonObject.keySet().toArray()[0])){
                System.out.println(changedJsonObject.keySet().toArray()[0]);
                System.out.println(changedJsonObject.get((changedJsonObject.keySet().toArray()[0])));
                jsonObject.put(changedJsonObject.keySet().toArray()[0], changedJsonObject.get(changedJsonObject.keySet().toArray()[0]));
                    if(saveFile(jsonObject, fileName) == 1){
                        System.out.printf("Changed successfully");
                    }
            }
            else{
                System.out.println("Collection doens't contains doc like that. Wanna add it? Y/N");
                Scanner in = new Scanner(System.in);
                String action = in.nextLine();
                if(action.equals("Y")){
                    jsonObject.put(changedJsonObject.keySet().toArray()[0], changedJsonObject.get(changedJsonObject.keySet().toArray()[0]));
                    System.out.printf("Added successfully");
                }
            }
        }
        catch(ParseException parseException){
            System.out.println("Wrong JSON string");
            return 0;
        }
        catch(FileNotFoundException fileNotFound){
            System.out.println("Collection is empty or doesn't exists");
            return 0;
        } catch (IOException e) {
            System.out.println("Collection is empty or doesn't exists");
            return 0;
        }
        catch (Exception exp) {
            System.out.println("error");
            return 0;
        }
        return 0;
    }
    public int deleteDoc(String docName, String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.containsKey(docName)) {
                JSONObject newJsonObject = new JSONObject();
                for (Object object:jsonObject.keySet()
                ) {
                    if((object.equals(docName))==false){
                        newJsonObject.put(object, jsonObject.get(object));
                    }

                }
                return saveFile(newJsonObject, fileName);


            }
            else{
                System.out.println("Collection doesn't contains that document");
                return 0;
            }
        }
        catch(Exception exp){
            System.out.println("Collection is empty or doesn't exists");
            return 0;
        }
    }
    public void createCollection(String fileName) throws Exception {
        File file = new File(fileName+".json");
        file.createNewFile();
    }
    public int insert(String fileName, String name, String str) throws Exception {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) // Если в файле уже есть записи
        {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            String cont;
            if(jsonObject.containsKey(name)) {
                System.out.println("Already exist. Wanna change?(Y/N)");
                Scanner in = new Scanner(System.in);
                cont = in.nextLine();
            }
            else {
                cont = "Y";
            }
            if(cont.equals("Y")){
                JSONObject newJsonObject = (JSONObject) obj;
                for (Object object:jsonObject.keySet()
                ) {
                    newJsonObject.put(object, jsonObject.get(object));
                }
                JSONParser parser = new JSONParser();
                JSONObject json = new JSONObject();
                try {
                    json = (JSONObject) parser.parse(str);
                    newJsonObject.put(name, json);
                } catch (Exception exp) {
                    if (str.contains("[") && (str.contains("]"))) {
                        JSONArray array = (JSONArray) parser.parse(str);
                        newJsonObject.put(name, array);
                    } else {
                        System.out.println("Parse error");
                    }
                }
                if(saveFile(newJsonObject, fileName) == 1){
                    System.out.println("Saved");
                }
                return 1;
            }
            return 0;
        }
        catch(Exception exp){ // Если файл пустой
            JSONObject newJsonObject = new JSONObject();
            JSONParser parser = new JSONParser();
            JSONObject json = new JSONObject();
            try {
                json = (JSONObject) parser.parse(str);
                newJsonObject.put(name, json);
            } catch (Exception exp1) {
                if (str.contains("[") && (str.contains("]"))) {
                    JSONArray array = (JSONArray) parser.parse(str);
                    newJsonObject.put(name, array);
                } else {
                    System.out.println("Parse error");
                }
            }
            if(saveFile(newJsonObject, fileName) == 1){
                System.out.println("Saved");
                return 1;
            }
        }
        return 0;
    }

    public int find(String fileName, String key, String value) throws Exception {
        JSONParser parser = new JSONParser();
        JSONAware obj = (JSONAware) parser.parse(new FileReader(fileName + ".json"));
        if(obj.getClass() == JSONObject.class) {
            JSONObject jsonObject = (JSONObject) obj;
            for (Object object: jsonObject.keySet()
            ) {
                 if (findMap((JSONAware) jsonObject.get(object), key, value)){
                     System.out.println((JSONAware) jsonObject.get(object));
                 }
            }
            return 1;
        }
        return 0;
    }

    public int count(String fileName) throws Exception {
        JSONParser parser = new JSONParser();
        JSONAware obj = (JSONAware) parser.parse(new FileReader(fileName + ".json"));
        Scanner in = new Scanner(System.in);
        System.out.println("Key:");
        Object key = in.nextLine();//разобраться с типом
        System.out.println("Value:");
        Object value = in.nextLine();//разобраться с типом
        int count = 0;
        if(obj.getClass() == JSONObject.class) {
            JSONObject jsonObject = (JSONObject) obj;
            for (Object object: jsonObject.keySet()
            ) {
                if (findMap((JSONAware) jsonObject.get(object), key, value)){
                    count++;
                }
            }
        }
        System.out.println("Count=" + count);

        return 0;
    }

    public boolean findMap(JSONAware obj, Object key, Object value) throws Exception {
        boolean finded = false;
        if(obj.getClass() == JSONObject.class) {
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.containsKey(key)&&((jsonObject).get(key).toString().equals(value.toString()))){
                return true;
            }
            else{
                try{
                    for (Object object: jsonObject.keySet()
                    ) {
                        if (findMap((JSONAware) jsonObject.get(object), key, value)){
                            return true;
                        }
                    }
                }
                catch(Exception ignored){
                }
            }
        }
        return false;
    }
    public static int count(String str, String target) {
        return (str.length() - str.replace(target, "").length()) / target.length();
    }

    public int saveFile(JSONAware obj, String fileName) throws Exception {
        FileWriter file = new FileWriter(fileName + ".json", false);
        try {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}