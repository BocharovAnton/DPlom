import org.json.simple.JSONObject;

import java.util.Scanner;

public class Menu {
    private boolean logged = false;
    private Scanner in = new Scanner(System.in);
    void login() throws Exception {
        JSONObject user = new JSONObject();
        MAC mac = new MAC();
        while(true){
            String input = in.nextLine();
            switch (input) {
                case "exit" -> {
                    System.out.println("Exiting...");
                }
                case "login" -> {
                    if (!mac.usersExists()) {
                        System.out.println("No users registered. Only admin can login.");
                    }
                    System.out.print("Login:");
                    String _login = in.nextLine();
                    System.out.print("Password:");
                    String password = in.nextLine();
                    if (mac.usersExists()) {
                        user = (JSONObject) mac.loginUser(_login, password);
                    } else {
                        user = (JSONObject) mac.loginNoUsers(_login, password);
                    }
                }
            }
            afterLogin(user);
            return;
        }

    }
    void afterLogin(JSONObject user) throws Exception {
        MAC mac = new MAC();
        while(true){

            String input = in.nextLine();
            switch (input) {
                case "json" -> {
                    if (!user.get("access").equals(4)) {
                        DBActions actions = new DBActions();
                        System.out.println("Enter collection name");
                        String fileName = in.nextLine();//Пользователь вводит коллекцию, с которой собирается работать
                        actions.UI(user, fileName);
                    } else {
                        System.out.println("Admin can only register users");
                    }
                    return;
                }
                case "register" -> {
                    System.out.println(user.get("access"));
                    if (Long.parseLong(user.get("access").toString()) == 4){
                        System.out.print("Login:");
                        String login = in.nextLine();
                        System.out.print("Password:");
                        String password = in.nextLine();
                        System.out.print("Access level(1-3):");
                        int accessLevel = in.nextInt();

                        mac.registerUser(login, password, accessLevel);

                    } else {
                        System.out.println("Only admin can register users");
                    }
                }
                case "change password" -> {
                    System.out.print("Login:");
                    String _login = in.nextLine();
                    System.out.print("Password:");
                    String password = in.nextLine();

                    mac.changeLoginPassword(user, _login, password);
                }
            }
        }
    }
}
