import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Scanner;

public class DBActions {
    public void UI(JSONObject user, String fileName) throws Exception {
        Scanner in = new Scanner(System.in);
        //!!!!!проверяем, есть ли у пользователя доступ для работы с ней!!!!!
        boolean exit = false;
        while(!exit){
            System.out.print(fileName+"->");
            String inputString = in.nextLine();
            String name,json;
            String action = inputString.split("\\(", 2)[0];
            String parameters = inputString.substring(inputString.indexOf("(")+1, inputString.lastIndexOf(")"));
            switch (action) {//разбираем стоку, введенную пользователем
                case ("insert") -> {
                    name = parameters.split(":", 2)[0].replace(" ", "").replace("\"", "");
                    json = parameters.split(":", 2)[1].replace(" ", "");
                    if (!createCollection(fileName))
                        System.out.println("already exists");
                    if (insert(fileName, name, json)) {
                        exit = true;
                    }
                }
                case ("change") -> {
                    if (change(parameters, fileName)) {
                        System.out.println("Changed successful");
                    }
                }
                case ("delete") -> {
                    if (deleteDoc(parameters, fileName)) {
                        System.out.println("Deleted successful");
                    }
                }
                case ("show") ->
                    showDocList(fileName);
                case ("find") -> {
                    String key = parameters.split(",")[0].trim();
                    String value = parameters.split(",")[1].trim();
                    if (!find(fileName, key, value)) {
                        System.out.println("Nothing founded");
                    }
                }
            }
        }
    }
    public void showDocList(String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject.keySet());
        }
        catch(Exception exp){
            System.out.println("Collection is empty or doesn't exists");
        }
    }
    public boolean change(String changedJSON, String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader file = new FileReader(fileName+".json")) {
            JSONObject changedJsonObject = (JSONObject) jsonParser.parse(changedJSON);
            JSONObject collection = (JSONObject) jsonParser.parse(file);
            if(changedJsonObject.keySet().size()>1){
                System.out.println("Only one doc may be changed");
                return false;
            }
            Object docName = changedJsonObject.keySet().toArray()[0];
            if((collection).containsKey(docName)){
                collection.put(docName, changedJsonObject.get(docName));
                    if(saveFile(collection, fileName)){
                        System.out.print("Changed successfully");
                    }
            }
            else{
                System.out.println("Collection doesn't contains doc like that. Wanna add it? Y/N");
                Scanner in = new Scanner(System.in);
                String action = in.nextLine();
                if(action.equals("Y")){
                    collection.put(docName, changedJsonObject.get(docName));
                    System.out.print("Added successfully");
                }
            }
        }
        catch(ParseException parseException){
            System.out.println("Wrong JSON string");
            return false;
        } catch(IOException fileNotFound){
            System.out.println("Collection is empty or doesn't exists");
            return false;
        } catch (Exception exp) {
            System.out.println("error in change function");
            return false;
        }
        return false;
    }
    public boolean deleteDoc(String docNameToDelete, String fileName){
        JSONParser jsonParser = new JSONParser();
        try (FileReader file = new FileReader(fileName+".json")) {
            JSONObject doc = (JSONObject) jsonParser.parse(file);
            if(doc.containsKey(docNameToDelete)) {
                JSONObject newDoc = new JSONObject();
                for (Object docName:doc.keySet()) {
                    if(!(docName.equals(docNameToDelete))){
                        newDoc.put(docName, doc.get(docName));
                    }
                }
                return saveFile(newDoc, fileName);//сохраняем файл
            }
            else{
                System.out.println("Collection doesn't contains that doc");
                return false;
            }
        }
        catch(Exception exp){
            System.out.println("Collection is empty or doesn't exists");
            return false;
        }
    }

    public boolean insert(String fileName, String newDocName, String stringToInsert){
        Scanner in = new Scanner(System.in);
        JSONParser jsonParser = new JSONParser();
        try (FileReader file = new FileReader(fileName+".json"))
        {
            JSONObject collection = (JSONObject) jsonParser.parse(file);
            String action = "Y";
            if(collection.containsKey(newDocName)){
                System.out.println("Doc already exists. Wanna change it?(Y/N)");
                action = in.nextLine();
            }
            if(action.equals("Y")){
                try{
                    JSONObject jsonToInsert = (JSONObject) jsonParser.parse(stringToInsert);
                    collection.put(newDocName, jsonToInsert);
                } catch (Exception exp) {
                    if (stringToInsert.contains("[") && (stringToInsert.contains("]"))) {
                        JSONArray jsonToInsert = (JSONArray) jsonParser.parse(stringToInsert);
                        collection.put(newDocName, jsonToInsert);
                    } else{
                        System.out.println("Parse error");
                    }
                }
                if(saveFile(collection, fileName)){
                    System.out.println("Saved");
                    return true;
                }
            }
        }
        catch(Exception exp){
            System.out.println("insert function error");
        }
        return false;
    }

    public boolean find(String fileName, String key, String value) throws Exception {
        JSONParser parser = new JSONParser();
        JSONAware obj = (JSONAware) parser.parse(new FileReader(fileName + ".json"));
        if(obj.getClass() == JSONObject.class) {
            JSONObject jsonObject = (JSONObject) obj;
            for (Object object: jsonObject.keySet()) {
                 if (findMap((JSONAware) jsonObject.get(object), key, value)){
                    System.out.println(jsonObject.get(object));
                 }
            }
            return true;
        }
        else{
            JSONArray jsonArray = (JSONArray) obj;
            for (Object  tmpObject: jsonArray) {
                JSONObject jsonObject = (JSONObject) tmpObject;
                for (Object object: jsonObject.keySet()) {
                    if (findMap((JSONAware) jsonObject.get(object), key, value)){
                        System.out.println(jsonObject.get(object));
                    }
                }
            }
            return true;
        }
    }

    public boolean count(String fileName) throws Exception {//количество вхождений(?)
        JSONParser parser = new JSONParser();
        JSONAware obj = (JSONAware) parser.parse(new FileReader(fileName + ".json"));
        Scanner in = new Scanner(System.in);
        System.out.println("Key:");
        Object key = in.nextLine();
        System.out.println("Value:");
        Object value = in.nextLine();
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

        return false;
    }

    public boolean saveFile(JSONAware obj, String fileName) throws Exception {
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
        return true;
    }

    public boolean findMap(JSONAware obj, Object key, Object value){
        if(obj.getClass() == JSONObject.class) {
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.containsKey(key)&&((jsonObject).get(key).toString().equals(value.toString()))){
                return true;
            }
            else{
                try{
                    for (Object object: jsonObject.keySet()) {
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

    public boolean createCollection(String fileName) throws Exception {
        File file = new File(fileName+".json");
        return file.createNewFile();
    }
}
