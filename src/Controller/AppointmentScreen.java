package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AppointmentScreen {
    public Button selectButton;

    public void selectButtonHandler(ActionEvent actionEvent) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/CustomerScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Customer Screen");
            appStage.setScene(new Scene(root, 600, 450));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
