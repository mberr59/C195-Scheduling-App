package Controller;

import Helper.DBConnection;
import Helper.PopulateData;
import Helper.QueryExecutions;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is the controller for the Customer screen. A list of all customers is populated in this class asl well as
 * all the buttons to add, update, delete customers. There is also a button to refresh the data in the customer table if
 * any of the data changes.
 */
public class CustomerScreen implements Initializable {
    public TableView<Customer> customerTable;
    public TableColumn<Customer,Integer> customerTableId;
    public TableColumn<Customer,String> customerTableName;
    public TableColumn<Customer,String> customerTableAddress;
    public TableColumn<Customer,String> customerTablePhone;
    public TableColumn<Customer,String> customerTablePostal;
    public TableColumn<Customer,String> customerTableFLD;
    public Button addCustomerButton;
    public Button modifyCustomerButton;
    public Button exitCustomerButton;
    public Button deleteCustomerButton;
    public Button refreshDataButton;

    /**
     * Lambda Expression 6. Creates a PopulateData Interface named customerData and passes the data to the Interface
     * using a Lambda expression block.
     */
    PopulateData customerData = () -> {
        try {
            ObservableList<Customer> customerData = FXCollections.observableArrayList();
            Connection conn = DBConnection.getConn();
            String query = QueryExecutions.getSelectCustomerQuery();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {

                int id = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postal = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                PreparedStatement divisionNameStatement = conn.prepareStatement(QueryExecutions.getDivisionName());
                divisionNameStatement.setInt(1, divisionID);
                ResultSet divisionNameRS = divisionNameStatement.executeQuery();
                divisionNameRS.next();
                String divisionName = divisionNameRS.getString("Division");

                Customer customer = new Customer(id, name, address, postal, phone, divisionName);
                customerData.add(customer);

            }
            customerTableId.setCellValueFactory(new PropertyValueFactory<>("id"));
            customerTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerTableAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerTablePostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
            customerTablePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            customerTableFLD.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
            customerTable.setItems(customerData);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerData.poplateData();
    }

    /**
     * Add Button Handler. This method calls the Add Customer screen.
     */
    public void addButtonHandler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddCustomerScreen.fxml"));
            Parent root = loader.load();

            AddCustomerScreen addCustomer = loader.getController();
            addCustomer.PopulateCountryCB();

            Stage appStage = new Stage();
            appStage.setTitle("Add Customer Screen");
            appStage.setScene(new Scene(root));
            appStage.show();

        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * Modify Button Handler. This button calls the Modify Customer screen.
     */
    public void modifyButtonHandler() {

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ModifyCustomerScreen.fxml")));
            Parent root = loader.load();

            ModifyCustomerScreen modCustomer = loader.getController();
            modCustomer.populateCustomerFields(customerTable.getSelectionModel().getSelectedItem());
            Stage appStage = new Stage();
            appStage.setTitle("Modify Customer Screen");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            Alert selectionAlert = new Alert(Alert.AlertType.ERROR);
            selectionAlert.setTitle("Selection Error");
            selectionAlert.setContentText("Please select a customer to modify.");
            selectionAlert.showAndWait();
        }
    }

    /**
     * Exit Button Handler. This method closes the screen.
     */
    public void exitButtonHandler() {
        Stage stage = (Stage) exitCustomerButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Delete Button Handler. This method tries to delete the selected Customer. If the customer has appointments, a
     * custom error message displays saying that all appointments must this customer must be deleted before deleting the
     * customer.
     */
    public void deleteButtonHandler() {
        try {
            int customerID = customerTable.getSelectionModel().getSelectedItem().getId();
            String customerName = customerTable.getSelectionModel().getSelectedItem().getName();
            Connection connection = DBConnection.getConn();
            PreparedStatement appointmentStatement = connection.prepareStatement(QueryExecutions.getAppointmentByCustomer());
            appointmentStatement.setInt(1, customerID);
            ResultSet appointmentRS =  appointmentStatement.executeQuery();
            if (!appointmentRS.next()) {
                Alert deleteCustomerAlert = new Alert(Alert.AlertType.CONFIRMATION,"Delete This Customer?",ButtonType.YES, ButtonType.NO);
                deleteCustomerAlert.setContentText("Are you sure you wish to delete " + customerName + "?");
                deleteCustomerAlert.showAndWait();
                if (deleteCustomerAlert.getResult() == ButtonType.YES) {
                    PreparedStatement deleteCustomer = connection.prepareStatement(QueryExecutions.deleteCustomer());
                    deleteCustomer.setInt(1, customerID);
                    int updatedRows = deleteCustomer.executeUpdate();
                    Alert deleteInfo = new Alert(Alert.AlertType.INFORMATION);
                    if (updatedRows > 0) {
                        deleteInfo.setTitle("Customer Deleted");
                        deleteInfo.setContentText(customerName + " has been deleted.");
                    } else {
                        deleteInfo.setTitle("Customer Not Deleted");
                        deleteInfo.setContentText(customerName + " has not been deleted.");
                    }
                    deleteInfo.showAndWait();
                }
            } else {
                Alert appointmentWarning = new Alert(Alert.AlertType.ERROR);
                appointmentWarning.setTitle("Customer Appointments");
                appointmentWarning.setContentText("Customer still has appointments scheduled.");
                appointmentWarning.showAndWait();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Refresh Data Handler. This method re-loads the customer data in the table.
     */
    public void refreshDataHandler() {
        customerData.poplateData();
    }
}
