import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Scanner;
import java.util.UUID;

public class Login {
    public int adminLogin() throws Exception {
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
        return 1;
    }
    public boolean usersExists(){
        return false;
    }
    public int changeLoginPassword(String oldLogin, String oldPassword){
        //найти в файле oldLogin-oldPassword, затем поменять их на новые
        return 0;
    }
    public int registerUser() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Login:");
        String login = in.nextLine();
        System.out.print("Password:");
        String password = in.nextLine();
        System.out.print("Uroven dostupa:(1-3)");
        int accessLevel = in.nextInt();
        JSONObject user = new JSONObject();
        JSONObject info = new JSONObject();
        info.put("login", login);
        info.put("password", password);
        info.put("access", accessLevel);
        UUID uuid = UUID.randomUUID();
        user.put(uuid, info);
        Actions actions = new Actions();
        actions.saveFile(user, "users");
        return 0;
    }
}
