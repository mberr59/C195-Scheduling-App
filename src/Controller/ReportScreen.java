package Controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReportScreen {
    public TextArea reportTF;
    public Button exitButton;

    public void onExitHandler() {
        reportTF.clear();
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
