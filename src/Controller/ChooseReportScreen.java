package Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class ChooseReportScreen {
    public Button custAppRep;
    public Button contactRep;
    public Button ftcApp;
    public Button repClose;
    private final File logsFile = new File("C:\\Users\\Micah\\Logs\\Error.txt");

    public void custAppRepHandler() {

    }

    public void contactRepHandler() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ReportScreen.fxml")));
            Parent root = loader.load();
            ReportScreen reportScreen = loader.getController();
            reportScreen.showCustomerReport(logsFile);
            Stage reportStage = new Stage();
            reportStage.setTitle("Contact Report");
            reportStage.setScene(new Scene(root));
            reportStage.show();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ignored) {

        }
    }

    public void ftcAppHandler() {
    }

    public void repCloseHandler() {
    }
}
