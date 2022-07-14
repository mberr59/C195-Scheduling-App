package Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyCustomerScreen {
    public TextField ModCustIDTF;
    public TextField ModCustNameTF;
    public TextField ModCustAddrTF;
    public TextField ModCustPostalTF;
    public TextField ModCustPhoneTF;
    public ComboBox<String> ModCustCountryCB;
    public ComboBox<String> ModCustStateCB;
    public Button ModCustSaveBn;
    public Button ModCustCancelBn;

    public void ModCustSaveHandler(ActionEvent actionEvent) {
    }

    public void ModCustCancelHandler() {
        Stage stage = (Stage) ModCustCancelBn.getScene().getWindow();
        stage.close();
    }
}
