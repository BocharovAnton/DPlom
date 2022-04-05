import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class MAC {
    private final String fileName = "users";

    public void registerUser(String login, String password, int access) throws Exception {
        JSONObject info = new JSONObject();
        info.put("login", login);
        info.put("password", password);
        info.put("access", access);
        saveUser(info);
    }

    public Object loginUser(String login, String password){
        JSONParser parser = new JSONParser();
        try {
            JSONObject usersJSON = (JSONObject) parser.parse(new FileReader(fileName + ".json"));
            for (Object id: usersJSON.keySet()) {
                JSONObject user = (JSONObject) usersJSON.get(id);
                if(user.get("login").equals(login)&&(user.get("password").equals(password))){
                    System.out.println(user);
                    return user;
                }
            }
        }
        catch(Exception exp){
            return false;
        }
        return false;
    }

    public Object loginNoUsers(String login, String password) throws Exception {
        JSONObject info = new JSONObject();
        info.put("login", login);
        info.put("password", password);
        info.put("access", 4);
        JSONObject user;
        if (login.equals("admin") && password.equals("admin")) {
            saveUser(info);
            user = (JSONObject) loginUser(login, password);
        }
        else{
            return false;
        }
        return user;
    }

    public void changeLoginPassword(JSONObject user, String login, String password){
        JSONParser parser = new JSONParser();
        DBActions actions = new DBActions();
        try (FileReader reader = new FileReader("users"+".json")) {
            JSONObject usersJSON = (JSONObject) parser.parse(reader);
            if(usersJSON.keySet().size()>0){
                for (Object id:usersJSON.keySet()){
                    JSONObject info = (JSONObject) usersJSON.get(id);
                    if((info.get("login").equals(user.get("login")))&&(info.get("password")).equals(user.get("password")))
                    {
                        JSONObject newInfo = new JSONObject();
                        newInfo.put("login", login);
                        newInfo.put("password", password);
                        newInfo.put("access", user.get("access"));
                        usersJSON.put(id,newInfo);
                        actions.saveFile(usersJSON, "users");
                        System.out.println("Saved");
                    }
                }
            }
            else{
                System.out.println("User doesn't exists");
            }
        }
        catch(Exception ignored){
        }
    }

    public void saveUser(JSONObject info) throws Exception{
        DBActions actions = new DBActions();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject = (JSONObject) parser.parse(new FileReader(fileName + ".json"));
            if(jsonObject.keySet().size()>0){
                for (Object id:jsonObject.keySet()){
                    JSONObject user = (JSONObject) jsonObject.get(id);
                    if(user.get("login").equals(info.get("login"))){//если пользователь с таким логином уже существует
                        System.out.println("Login already used");
                        registerUser(user.get("login").toString(), user.get("password").toString(), Integer.parseInt(user.get("access").toString()));//возвращаемся к регистрации
                    }
                }
            }
        }
        catch(IOException ignore){
        }
        UUID uuid = UUID.randomUUID();
        jsonObject.put(uuid, info);//добавляем нового пользователя к старым
        actions.saveFile(jsonObject, "users");
        System.out.println("Saved");
    }

    public boolean usersExists(){
        try (FileReader ignored = new FileReader("users"+".json")) {
            return true;
        }
        catch(IOException exp){
            return false;
        }
    }
}
