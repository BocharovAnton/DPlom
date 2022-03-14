import java.util.Scanner;

public class InputChecker {
    boolean logged = false;
    boolean check() throws Exception {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        Login login = new Login();
        switch(input){
            case ".exit":
                System.out.println("Exiting...");
                return false;
            case ".login":
                if(login.usersExists()){
                    return login.loginUser();
                }
                else{
                    //логиним админа, который создает пользователей
                    return login.adminLogin();
                }
            case ".json":
                if(logged == true){
                 Actions jsonCreator = new Actions();
                 return jsonCreator.UI();
                }
                else{
                    System.out.println("You're not logged");
                }
            case ".register":
                return login.registerUser();
        }
        return true;
    }
}
