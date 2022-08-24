package Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReportScreen {
    public TextArea reportTF;
    public Button exitButton;

    public void onExitHandler() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void showReport() {

    }
}
