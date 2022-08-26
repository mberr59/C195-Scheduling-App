package Controller;

import Helper.QueryExecutions;
import Helper.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

/**
 * This is the Add Customer controller class. This class is responsible for taking in all of the user input and creating
 * a new Customer using the data entered if the data passes validation.
 */
public class AddCustomerScreen {
    public TextField AddCustNameTF;
    public TextField AddCustAddrTF;
    public TextField AddCustPostalTF;
    public TextField AddCustPhoneTF;
    public ComboBox<String> AddCustCountryCB;
    public ComboBox<String> AddCustStateCB;
    public Button AddCustSaveBn;
    public Button AddCustCancelBn;

    /**
     * Calls the Saving Customer Data method when clicked.
     */
    public void AddCustSaveHandler() { savingCustomerData(); }

    /**
     * Closes the Add Customer screen.
     */
    public void AddCustCancelHandler() {
        Stage stage = (Stage) AddCustCancelBn.getScene().getWindow();
        stage.close();
    }

    /**
     * Populates the Country Combo Box by connecting to the database, pulling the info and storing it in a list
     * , then populates the Combo Box with the data.
     */
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

    /**
     * Populate First-level division Combo box. Checks to see which country was selected then connects to the database
     * to populate the data depending on the matching Country_ID.
     */
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

    /**
     * Selecting Country Handler. If the country is changed after the first time, this method will repopulate the
     * First-level division ComboBox.
     */
    public void selectingCountryHandler() {
        PopulateStateCB();
    }

    /**
     * Saving Customer Data method. This method takes in the entered data from the user, connects to the database and
     * adds the entered data into the database if the data passes validation.
     */
    public void savingCustomerData() {
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
        } catch (NumberFormatException numberFormatException) {
            Alert nfeAlert = new Alert(Alert.AlertType.ERROR);
            nfeAlert.setTitle("Numeric Error");
            nfeAlert.setContentText("Please enter a valid User ID and Customer ID");
            return;
        } catch (NullPointerException npe) {
            Alert npeAlert = new Alert(Alert.AlertType.ERROR);
            npeAlert.setTitle("Input Error");
            npeAlert.setContentText("Error n entered data. Please check data provided.");
            return;
        }
    }
}
