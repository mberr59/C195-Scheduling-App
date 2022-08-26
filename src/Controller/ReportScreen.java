package Controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class is the controller for the Report screen. All of the reports are saved as String ArrayLists and passed to
 * this screen to be displayed within the text area.
 */
public class ReportScreen {
    public TextArea reportTF;
    public Button exitButton;

    /**
     * Closes the Report screen.
     */
    public void onExitHandler() {
        reportTF.clear();
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
