package Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZoneId;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginScreen implements Initializable {
    public TextField userNameText;
    public TextField passwordText;
    public Button loginButton;
    public Label passLabel;
    public Label welcomeLabel;
    public Label userLabel;
    public Label zoneLabel;
    private ZoneId z = ZoneId.systemDefault();

    public void userNameTextHandler(ActionEvent actionEvent) {
    }

    public void passwordTextHandler(ActionEvent actionEvent) {
    }

    public void loginButtonHandler(ActionEvent actionEvent) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/AppointmentScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Appointment Screen");
            appStage.setScene(new Scene(root, 600, 450));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");
        zoneLabel.setText("Zone: " + z.getId());
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

