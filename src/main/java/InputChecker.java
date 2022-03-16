import java.util.Scanner;

public class InputChecker {
    boolean logged = false;
    boolean login() throws Exception {
        Scanner in = new Scanner(System.in);
        Login login = new Login();
        Integer accessLevel = -1;
        while(true){
            String input = in.nextLine();
            switch(input){
                case ".exit":
                    System.out.println("Exiting...");
                    return false;
                case ".login":
                    if(login.usersExists()){
                        accessLevel = Integer.parseInt(login.loginUser().toString());
                    }
                    else{
                        //логиним админа, который создает пользователей
                        login.adminLogin();
                        accessLevel = 4;
                    }
            }
            afterLogin(accessLevel);
            return true;
        }

    }
    boolean afterLogin(Integer accessLevel) throws Exception {
        Login login = new Login();
        while(true){
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            switch(input) {
                case ".json":
                    Actions jsonCreator = new Actions();
                    return jsonCreator.UI();//ПЕРЕДАВАТЬ ACCESS LEVEL ПОЛЬЗОВАТЕЛЯ
                case ".register"://ТОЛЬКО ЕСЛИ АДМИН ЗАЛОГИНЕН
                    if(accessLevel==4){
                        return login.registerUser();
                    }
                    else{
                        System.out.println("Only admin can register users");
                    }
            }
        }
    }
}
