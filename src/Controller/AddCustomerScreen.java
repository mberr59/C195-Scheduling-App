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

import java.sql.*;

public class AddCustomerScreen {
    public TextField AddCustNameTF;
    public TextField AddCustAddrTF;
    public TextField AddCustPostalTF;
    public TextField AddCustPhoneTF;
    public ComboBox<String> AddCustCountryCB;
    public ComboBox<String> AddCustStateCB;
    public Button AddCustSaveBn;
    public Button AddCustCancelBn;
    private static int country_ID;




    public void AddCustSaveHandler() {
        Connection conn = DBConnection.getConn();
        String custName = AddCustNameTF.getText();
        String custPhone = AddCustPhoneTF.getText();
        String custAddr = AddCustAddrTF.getText();
        String custPostal = AddCustPostalTF.getText();

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
            ObservableList<String> stateData = FXCollections.observableArrayList();
            String countryName = AddCustCountryCB.getSelectionModel().getSelectedItem();
            Connection conn = DBConnection.getConn();
            PreparedStatement countryStatement = conn.prepareStatement(QueryExecutions.getCountriesIDQuery());
            countryStatement.setString(1, countryName);
            ResultSet countryRS = countryStatement.executeQuery();
            countryRS.next();
            country_ID = countryRS.getInt("Country_ID");
            String stateQuery = QueryExecutions.getStatesQuery(country_ID);
            Statement stateSt = conn.createStatement();
            ResultSet stateRS = stateSt.executeQuery(stateQuery);

            while (stateRS.next()) {
                String result = stateRS.getString("Division");
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
