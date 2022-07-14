package Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCustomerScreen {
    public TextField AddCustNameTF;
    public TextField AddCustAddrTF;
    public TextField AddCustPostalTF;
    public TextField AddCustPhoneTF;
    public ComboBox<String> AddCustCountryCB;
    public ComboBox<String> AddCustStateCB;
    public Button AddCustSaveBn;
    public Button AddCustCancelBn;

    public void AddCustSaveHandler(ActionEvent actionEvent) {

    }

    public void AddCustCancelHandler(ActionEvent actionEvent) {
        Stage stage = (Stage) AddCustCancelBn.getScene().getWindow();
        stage.close();
    }
}
