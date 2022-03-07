import java.util.Scanner;

public class InputChecker {
    boolean logged = false;
    int check() throws Exception {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        switch(input){
            case ".exit":
                System.out.println("Exiting...");
                return 0;
            case ".login":
                Login login = new Login();
                if(login.usersExists()){
                    //вызвать функцию проверки существования файла с пользователями
                }
                else{
                    //логиним админа, который создает пользователей
                    login.adminLogin();
                }
                logged = true;
                return 1;
            case ".json":
                if(logged == true){
                 Actions jsonCreator = new Actions();
                 jsonCreator.UI();
                }
                else{
                    System.out.println("You're not logged");
                }
        }
        return 0;
    }
}
