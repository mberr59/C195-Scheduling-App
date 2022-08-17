package Controller;

import DAO.QueryExecutions;
import DAO.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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


    public void AddCustSaveHandler() { addingCustomerData(); }

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
            int country_ID = countryRS.getInt("Country_ID");
            PreparedStatement fldStatement = conn.prepareStatement(QueryExecutions.getStatesQuery());
            fldStatement.setInt(1, country_ID);
            ResultSet fldRS = fldStatement.executeQuery();

            while (fldRS.next()) {
                String result = fldRS.getString("Division");
                stateData.add(result);
            }
            AddCustStateCB.setItems(stateData);
            AddCustStateCB.getSelectionModel().selectFirst();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void selectingCountryHandler() {
        PopulateStateCB();
    }

    public void addingCustomerData() {
        Connection conn = DBConnection.getConn();
        String custName = AddCustNameTF.getText();
        String custPhone = AddCustPhoneTF.getText();
        String custAddr = AddCustAddrTF.getText();
        String custPostal = AddCustPostalTF.getText();
        String custDivision = AddCustStateCB.getSelectionModel().getSelectedItem();
        try{
            PreparedStatement divisionStatement = conn.prepareStatement(QueryExecutions.getDivisionID());
            divisionStatement.setString(1, custDivision);
            ResultSet divisionRS = divisionStatement.executeQuery();
            divisionRS.next();
            int divisionID = divisionRS.getInt("Division_ID");
            PreparedStatement addCustomerData = conn.prepareStatement(QueryExecutions.addCustomerQuery());
            addCustomerData.setString(1, custName);
            addCustomerData.setString(2, custAddr);
            addCustomerData.setString(3, custPostal);
            addCustomerData.setString(4, custPhone);
            addCustomerData.setInt(5, divisionID);
            int updatedRows = addCustomerData.executeUpdate();
            if(updatedRows > 0){
                System.out.println("Insert Successful");
            } else {
                System.out.println("Insert Unsuccessful");
            }

        } catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }
}
