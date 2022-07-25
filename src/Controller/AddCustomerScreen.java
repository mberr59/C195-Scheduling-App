package Controller;

import DAO.QueryExecutions;
import DAO.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddCustomerScreen {
    public TextField AddCustNameTF;
    public TextField AddCustAddrTF;
    public TextField AddCustPostalTF;
    public TextField AddCustPhoneTF;
    public ComboBox<String> AddCustCountryCB;
    public ComboBox<String> AddCustStateCB;
    public Button AddCustSaveBn;
    public Button AddCustCancelBn;




    public void AddCustSaveHandler() {
    }

    public void AddCustCancelHandler() {
        Stage stage = (Stage) AddCustCancelBn.getScene().getWindow();
        stage.close();
    }

    public void PopulateCountryCB(){
        try {
            ObservableList<String> countryData = FXCollections.observableArrayList();
            Connection conn = DBConnection.getConn();
            String query = QueryExecutions.getCountriesQuery();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String result = rs.getString("Country");
                countryData.add(result);
            }
            AddCustCountryCB.setItems(countryData);
            AddCustCountryCB.getSelectionModel().selectFirst();
            PopulateStateCB();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void PopulateStateCB() {
        try {
            int countryID = 0;
            switch (AddCustCountryCB.getSelectionModel().getSelectedItem()) {
                case "U.S" -> countryID = 1;
                case "UK" -> countryID = 2;
                case "Canada" -> countryID = 3;
                default -> {
                    Alert selectionAlert = new Alert(Alert.AlertType.ERROR);
                    selectionAlert.setTitle("Selection Error");
                    selectionAlert.setContentText("Please select a valid country.");
                    selectionAlert.showAndWait();
                }
            }
            ObservableList<String> stateData = FXCollections.observableArrayList();
            Connection conn = DBConnection.getConn();
            String query = QueryExecutions.getStatesQuery(countryID);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String result = rs.getString("Division");
                stateData.add(result);
            }
            AddCustStateCB.setItems(stateData);
            AddCustStateCB.getSelectionModel().selectFirst();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void SelectingCountryHandler() {
        PopulateStateCB();
    }
}
