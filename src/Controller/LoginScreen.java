package Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import Helper.DBConnection;
import Helper.QueryExecutions;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private final ZoneId z = ZoneId.systemDefault();
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // TODO Change this path to root folder of project before submitting!
    private final File loginLogs = new File("C:\\Users\\Micah\\Logs\\login_activity.txt");
    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public void userNameTextHandler() {
        String pass = passwordText.getText();
        LocalDateTime timestamp = LocalDateTime.now();
        try {
            Connection conn = DBConnection.getConn();
            boolean nameFound = false;
            String providedUsername = userNameText.getText();
            PreparedStatement usernameStatement = conn.prepareStatement(QueryExecutions.getUsernames());
            ResultSet usernameRS = usernameStatement.executeQuery();
            while (usernameRS.next()) {
                String result = usernameRS.getString("User_Name");
                if (providedUsername.equals(result)) {
                    nameFound = true;
                    passwordTextHandler(providedUsername, timestamp);
                }
            }
            if (!nameFound) {
                Alert usernameAlert = new Alert(Alert.AlertType.ERROR);
                usernameAlert.setTitle("Incorrect Username");
                usernameAlert.setContentText("Username provided not found.");
                usernameAlert.showAndWait();
                try {
                    FileWriter writer = new FileWriter(loginLogs, true);
                    writer.write("Login Failed with\nUsername: " + providedUsername + "\nPassword: " + pass +
                            "\nDate/Time: " + timestamp.atZone(ZoneId.systemDefault()) + "\n\n");
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void passwordTextHandler(String username, LocalDateTime timestamp) {
        try {
            Connection conn = DBConnection.getConn();
            boolean passFound = false;
            String providedPassword = passwordText.getText();
            PreparedStatement passwordStatement = conn.prepareStatement(QueryExecutions.getPassword());
            passwordStatement.setString(1, username);
            ResultSet passwordRS = passwordStatement.executeQuery();
            while (passwordRS.next()) {
                String result = passwordRS.getString("Password");
                if (providedPassword.equals(result)) {
                    passFound = true;
                    Parent root;
                    try {
                        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/AppointmentScreen.fxml")));
                        Stage appStage = new Stage();
                        appStage.setTitle("Appointment Screen");
                        appStage.setScene(new Scene(root));
                        appStage.show();
                        FileWriter writer = new FileWriter(loginLogs, true);
                        writer.write("Login Successful with\nUsername: " + username + "\nPassword: " + providedPassword +
                                "\nDate/Time: " + timestamp.atZone(ZoneId.systemDefault()) + "\n\n");
                        writer.close();
                    } catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            }
            if (!passFound) {
                Alert passwordAlert = new Alert(Alert.AlertType.ERROR);
                passwordAlert.setTitle("Incorrect Password");
                passwordAlert.setContentText("Password provided not found.");
                passwordAlert.showAndWait();
                try {
                    FileWriter writer = new FileWriter(loginLogs, true);
                    writer.write("Login Failed with\nUsername: " + username + "\nPassword: " + providedPassword +
                            "\nDate/Time: " + timestamp.atZone(ZoneId.systemDefault()) + "\n\n");
                    writer.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loginButtonHandler() {
        userNameTextHandler();
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

