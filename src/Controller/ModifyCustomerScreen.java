package Controller;

import Helper.DBConnection;
import Helper.QueryExecutions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Model.Customer;

import java.sql.*;

/**
 * This class is the controller for the Modify Customer screen. This screen is called after a user selects one of the
 * customers in the customer list. The screen is used when a user needs to modify one of
 * the customer's data.
 */
public class ModifyCustomerScreen {
    public TextField modCustIDTF;
    public TextField modCustNameTF;
    public TextField modCustAddrTF;
    public TextField modCustPostalTF;
    public TextField modCustPhoneTF;
    public ComboBox<String> modCustCountryCB;
    public ComboBox<String> modCustStateCB;
    public Button modCustSaveBn;
    public Button modCustCancelBn;

    /**
     * Modify Customer Save Handler. This method calls the Updating Customer Data method.
     */
    public void modCustSaveHandler() { updatingCustomerData(); }

    /**
     * Modify Customer Cancel Handler. The method closes the screen.
     */
    public void modCustCancelHandler() {
        Stage stage = (Stage) modCustCancelBn.getScene().getWindow();
        stage.close();
    }

    /**
     * Populate Customer Fields. This method is called from the Customer screen after the user selects one of the
     * customers and clicks the update button. Takes the data from the customer object and populates the user fields
     * with the data.
     * @param customer Customer object passed to the method to populate the user fields with their data.
     */
    public void populateCustomerFields(Customer customer){
        populateCountryCB();
        modCustIDTF.setText(String.valueOf(customer.getId()));
        modCustNameTF.setText(customer.getName());
        modCustAddrTF.setText(customer.getAddress());
        modCustPhoneTF.setText(customer.getPhone());
        modCustPostalTF.setText(customer.getPostal());
        modCustStateCB.getSelectionModel().select(customer.getDivisionName());
        modCustCountryCB.getSelectionModel().select(getCustomerCountry(customer.getDivisionName()));

    }

    /**
     * Populate Country ComboBox. This method connects to the database and populates the Country ComboBox with the
     * data found in the countries table.
     */
    public void populateCountryCB(){
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
            modCustCountryCB.setItems(countryData);
            modCustCountryCB.getSelectionModel().selectFirst();
            populateStateCB();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Populate State ComboBox. The method connects to the database and populates the First-level division ComboBox with
     * the data found in the first-level divisions table with the matching Country_ID.
     */
    public void populateStateCB() {
        try {
            ObservableList<String> stateData = FXCollections.observableArrayList();
            String countryName = modCustCountryCB.getSelectionModel().getSelectedItem();
            Connection conn = DBConnection.getConn();
            PreparedStatement countryStatement = conn.prepareStatement(QueryExecutions.getCountriesIDQuery());
            countryStatement.setString(1, countryName);
            ResultSet countryRS = countryStatement.executeQuery();
            countryRS.next();
            int countryID = countryRS.getInt("Country_ID");
            PreparedStatement fldStatement = conn.prepareStatement(QueryExecutions.getStatesQuery());
            fldStatement.setInt(1, countryID);
            ResultSet fldRS = fldStatement.executeQuery();

            while (fldRS.next()) {
                String result = fldRS.getString("Division");
                stateData.add(result);
            }
            modCustStateCB.setItems(stateData);
            modCustStateCB.getSelectionModel().selectFirst();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    /**
     * Get Customer Country. This method uses the divisionName parameter to make a call to first-level divisions to get
     * the Country_ID. It then uses the Country_ID to get the corresponding name in the countries table.
     * @param divisionName first-level division name used to get the country_ID using a database call.
     * @return The Country_Name is returned.
     */
    private String getCustomerCountry(String divisionName) {
        String countryName = "null";
        try {
            Connection connection = DBConnection.getConn();
            PreparedStatement divisionStatement = connection.prepareStatement(QueryExecutions.getFLDCountriesID());
            divisionStatement.setString(1, divisionName);
            ResultSet countryIDRS = divisionStatement.executeQuery();
            countryIDRS.next();
            int countryID = countryIDRS.getInt("Country_ID");
            PreparedStatement countryNameStatement = connection.prepareStatement(QueryExecutions.getCountriesName());
            countryNameStatement.setInt(1, countryID);
            ResultSet countryNameRS = countryNameStatement.executeQuery();
            countryNameRS.next();
            countryName = countryNameRS.getString("Country");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return countryName;
    }

    /**
     * Updating Customer Data. This method takes in the data that the user provided and attempts to update the database
     * data. The data must pass the validation checks.
     */
    public void updatingCustomerData() {
        Connection conn = DBConnection.getConn();
        int custID = Integer.parseInt(modCustIDTF.getText());
        String custName = modCustNameTF.getText();
        String custPhone = modCustPhoneTF.getText();
        String custAddr = modCustAddrTF.getText();
        String custPostal = modCustPostalTF.getText();
        String custDivision = modCustStateCB.getSelectionModel().getSelectedItem();
        try{
            PreparedStatement divisionStatement = conn.prepareStatement(QueryExecutions.getDivisionID());
            divisionStatement.setString(1, custDivision);
            ResultSet divisionRS = divisionStatement.executeQuery();
            divisionRS.next();
            int divisionID = divisionRS.getInt("Division_ID");
            PreparedStatement addCustomerData = conn.prepareStatement(QueryExecutions.updateCustomerQuery());
            addCustomerData.setString(1, custName);
            addCustomerData.setString(2, custAddr);
            addCustomerData.setString(3, custPostal);
            addCustomerData.setString(4, custPhone);
            addCustomerData.setInt(5, divisionID);
            addCustomerData.setInt(6, custID);
            int rowAffected = addCustomerData.executeUpdate();
            if (rowAffected > 0) {
                System.out.println("Update Successful");
            } else {
                System.out.println("Update Unsuccessful");
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
