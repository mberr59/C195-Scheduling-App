package Controller;

import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginScreen implements Initializable {
    public TextField userNameText;
    public TextField passwordText;
    public Button loginButton;
    public Label passLabel;
    public Label welcomeLabel;
    public Label userLabel;

    public void userNameTextHandler(ActionEvent actionEvent) {
    }

    public void passwordTextHandler(ActionEvent actionEvent) {
    }

    public void loginButtonHandler(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");
        localeCheck(Locale.getDefault());
    }

    public void localeCheck (Locale locale) {
        if (locale.getLanguage().equals("fr")){
            ResourceBundle loginLabels = ResourceBundle.getBundle("Language/Lang", locale);
            System.out.println("Translating to French");
            passLabel.setText(loginLabels.getString("Password"));
            welcomeLabel.setText(loginLabels.getString("Welcome"));
            userLabel.setText(loginLabels.getString("Username"));
            loginButton.setText(loginLabels.getString("Connect"));
        }
    }
}

