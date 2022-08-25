package Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReportScreen {
    public TextArea reportTF;
    public Button exitButton;

    public void onExitHandler() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void showReport(File report) {
        try {
            Scanner scanner = new Scanner(new File(String.valueOf(report))).useDelimiter("----------------------");
            while (scanner.hasNext()){
                reportTF.appendText(scanner.next());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }
}
