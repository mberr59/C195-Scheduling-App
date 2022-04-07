package Main;

import Controller.LoginScreen;
import Database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/LoginScreen.fxml"));
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {


        Locale.setDefault(new Locale("fr", "CA"));
        DBConnection.startConn();
        launch(args);
        DBConnection.endConn();
    }

    public static void localeCheck (Locale locale) {
        if (locale.getLanguage().equals("fr")){
            ResourceBundle loginLabels = ResourceBundle.getBundle("Language/Lang", locale);
            System.out.println("Translating to French");
            String passKey = loginLabels.getString("Password");
            String welcomeKey = loginLabels.getString("Welcome");
            String userKey = loginLabels.getString("Username");
            String connKey = loginLabels.getString("Connect");
        }
    }
}
