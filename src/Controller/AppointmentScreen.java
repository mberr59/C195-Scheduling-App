package Controller;

import Helper.DBConnection;
import Helper.QueryExecutions;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppointmentScreen implements Initializable {
    public TableView<Appointment> appointmentTable;
    public TableColumn<Appointment, Integer> appointmentID;
    public TableColumn<Appointment, String> appointmentTitle;
    public TableColumn<Appointment, String> appointmentDesc;
    public TableColumn<Appointment, String> appointmentLocation;
    public TableColumn<Appointment, String> appointmentContact;
    public TableColumn<Appointment, String> appointmentType;
    public TableColumn<Appointment, Date> appointmentStart;
    public TableColumn<Appointment, Date> appointmentEnd;
    public TableColumn<Appointment, Integer> appointmentCustomerID;
    public TableColumn<Appointment, Integer> appointmentUserID;
    public Button addAppointment;
    public Button customerListButton;
    public Button refreshTableButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { populateAppointments(); }

    public void customerListButtonHandler() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/CustomerScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Customer Screen");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void populateAppointments() {
        try {
            ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
            Connection conn = DBConnection.getConn();
            String query = QueryExecutions.getSelectAppointmentQuery();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                Timestamp startDateTime = rs.getTimestamp("Start");
                Timestamp endDateTime = rs.getTimestamp("End");
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                PreparedStatement contactStatement = conn.prepareStatement(QueryExecutions.getContactsQuery());
                contactStatement.setInt(1, contactID);
                ResultSet contactRS = contactStatement.executeQuery();
                contactRS.next();
                String contactName = contactRS.getString("Contact_Name");

                Appointment appointment = new Appointment(appointmentID, title, description, location, contactName,
                        type, startDateTime, endDateTime, customerID, userID, contactID);
                appointmentData.add(appointment);

            }
            appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            appointmentDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
            appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
            appointmentTable.setItems(appointmentData);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void addAppointmentHandler() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/AddAppointmentScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Add Appointment");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void refreshTableHandler() { populateAppointments();}
}
