package Controller;

import DAO.DBConnection;
import DAO.QueryExecutions;
import Main.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CustomerScreen implements Initializable {
    public TableView customerTable;
    public TableColumn customerTableId;
    public TableColumn customerTableName;
    public TableColumn customerTableAddress;
    public TableColumn customerTablePhone;
    public TableColumn customerTablePostal;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateCustomerTable();
    }
}
