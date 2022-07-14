package Controller;

import DAO.DBConnection;
import DAO.QueryExecutions;
import Main.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CustomerScreen implements Initializable {
    public TableView<Customer> customerTable;
    public TableColumn<Customer,Integer> customerTableId;
    public TableColumn<Customer,String> customerTableName;
    public TableColumn<Customer,String> customerTableAddress;
    public TableColumn<Customer,String> customerTablePhone;
    public TableColumn<Customer,String> customerTablePostal;
    public Button addCustomerButton;
    public Button modifyCustomerButton;
    public Button exitCustomerButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateCustomerTable();
    }

    public void addButtonHandler(ActionEvent actionEvent) {
    }

    public void modifyButtonHandler(ActionEvent actionEvent) {
    }

    public void exitButtonHandler(ActionEvent actionEvent) {
        Stage stage = (Stage) exitCustomerButton.getScene().getWindow();
        stage.close();
    }

    public void populateCustomerTable(){
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

                Customer customer = new Customer(id, name, address, postal, phone);
                customerData.add(customer);

            }
            customerTableId.setCellValueFactory(new PropertyValueFactory<>("id"));
            customerTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerTableAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerTablePostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
            customerTablePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            customerTable.setItems(customerData);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
