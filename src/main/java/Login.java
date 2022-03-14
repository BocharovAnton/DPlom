import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class Login {
    private String fileName = "users";
    public boolean adminLogin() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Login:");
        String login = in.nextLine();
        System.out.print("Password:");
        String password = in.nextLine();
        if(login.equals("admin")&&password.equals("admin")){
            System.out.printf("Do you wanna change login/password? (Y/N)");
            String action = in.nextLine();
            if(action.equals("Y")){
                changeLoginPassword(login, password);
            }
            System.out.printf("Do you wanna change create new users? (Y/N)");
            action = in.nextLine();
            if(action.equals("Y")){
                registerUser();
            }
        }
        return true;
    }
    public boolean usersExists(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("users"+".json")) {
            return true;
        }
        catch(Exception exp){
            return false;
        }
    }
    public boolean changeLoginPassword(String oldLogin, String oldPassword){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("users"+".json")) {

            return true;
        }
        catch(Exception exp){
            return false;
        }
    }
    public boolean loginUser(){
        Actions actions = new Actions();
        Object accessLevel;
        Object id;
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileName + ".json"));
            Scanner in = new Scanner(System.in);
            System.out.print("Login:");
            String login = in.nextLine();
            System.out.print("Password:");
            String password = in.nextLine();
            for (Object object: jsonObject.keySet()) {
                 JSONObject user = (JSONObject) jsonObject.get(object);
                 if(user.get("login").equals(login)&&(user.get("password").equals(password))){
                     id = object;
                     System.out.printf(id.toString());
                     accessLevel = user.get("access");
                     System.out.println("\n");
                     System.out.printf(accessLevel.toString());
                     return true;
                 }
            }
            return false;
        }
        catch(Exception exp){
            return false;
        }
    }
    public boolean registerUser() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Login:");
        String login = in.nextLine();
        System.out.print("Password:");
        String password = in.nextLine();
        System.out.print("Access level(1-3):");
        int accessLevel = in.nextInt();
        JSONObject info = new JSONObject();
        info.put("login", login);
        info.put("password", password);
        info.put("access", accessLevel);
        UUID uuid = UUID.randomUUID();

        JSONParser parser = new JSONParser();
        JSONObject oldObject = new JSONObject();
        try{
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileName + ".json"));
            if(jsonObject.keySet().size()>0){
                for (Object object:jsonObject.keySet()){
                    JSONObject oldInf = (JSONObject) jsonObject.get(object);
                    if(oldInf.get("login").equals(login)){
                        System.out.println("Login already used");
                        return registerUser();
                    }
                }
                for (Object object:jsonObject.keySet()) {
                    oldObject.put(object, jsonObject.get(object));
                }
            }
            oldObject.put(uuid, info);
            Actions actions = new Actions();
            actions.saveFile(oldObject, "users");
            System.out.println("Saved");
        }
        catch(Exception ignore){

        }
        return true;
    }
}
