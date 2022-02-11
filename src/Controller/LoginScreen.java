package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreen implements Initializable {
    public TextField userNameText;
    public TextField passwordText;
    public Button loginButton;

    public void userNameTextHandler(ActionEvent actionEvent) {
    }

    public void passwordTextHandler(ActionEvent actionEvent) {
    }

    public void loginButtonHandler(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");
    }
}
