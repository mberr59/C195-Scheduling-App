package Controller;

import Helper.DBConnection;
import Helper.PopulateData;
import Helper.QueryExecutions;
import Model.Appointment;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public TableColumn<Appointment, String> appointmentStart;
    public TableColumn<Appointment, String> appointmentEnd;
    public TableColumn<Appointment, Integer> appointmentCustomerID;
    public TableColumn<Appointment, Integer> appointmentUserID;
    public Button addAppointment;
    public Button customerListButton;
    public Button refreshTableButton;
    public Button modAppointment;
    public RadioButton byMonthRadio;
    public RadioButton byWeekRadio;
    public ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public LocalDateTime startDateTime;
    public Button appFilter;
    public ToggleGroup filterGroup;
    public Button appDelete;
    public Button appExit;
    public Button appReports;

    // Lambda Expression 7. Creates a PopulateData Interface and passes the appointment data to the Interface using
    // a Lambda Expression block.
    PopulateData appointmentData = () -> {
        try {
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
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
                startDateTime = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
                String startDTString = dateTimeFormatter.format(startDateTime);
                String endDTString = dateTimeFormatter.format(endDateTime);
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                PreparedStatement contactStatement = conn.prepareStatement(QueryExecutions.getContactsQuery());
                contactStatement.setInt(1, contactID);
                ResultSet contactRS = contactStatement.executeQuery();
                contactRS.next();
                String contactName = contactRS.getString("Contact_Name");
                Appointment appointment = new Appointment(appointmentID, title, description, location, contactName,
                        type, startDTString, endDTString, customerID, userID, contactID);
                appointmentList.add(appointment);

            }
            appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            appointmentDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
            appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startString"));
            appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endString"));
            appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
            appointmentTable.setItems(appointmentList);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { appointmentData.poplateData(); }

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

    public void refreshTableHandler() {
        appointmentTable.getItems().removeAll(appointmentList);
        appointmentData.poplateData();
    }

    public void modAppointmentHandler() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ModifyAppointmentScreen.fxml")));
            Parent root = loader.load();

            ModifyAppointmentScreen modApp = loader.getController();
            modApp.populateAppFields(appointmentTable.getSelectionModel().getSelectedItem());
            Stage appStage = new Stage();
            appStage.setTitle("Modify Appointment Screen");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            Alert selectionAlert = new Alert(Alert.AlertType.ERROR);
            selectionAlert.setTitle("Selection Error");
            selectionAlert.setContentText("Please select an Appointment to modify.");
            selectionAlert.showAndWait();
        }
    }

    public void byMonthHandler() {
        int todayDateMonth = LocalDateTime.now().getMonth().getValue();
        int todayDateYear = LocalDateTime.now().getYear();
        int n = 0;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
        for (Appointment app: appointmentList) {
            LocalDateTime appointmentDT = LocalDateTime.parse(appointmentList.get(n).getStartString(), dateTimeFormatter);
            int appointmentMonth = appointmentDT.getMonth().getValue();
            int appointmentYear = appointmentDT.getYear();
            if (todayDateYear == appointmentYear) {
                if (todayDateMonth == appointmentMonth) {
                    monthlyAppointments.add(app);
                }
            }
            n += 1;
        }
        appointmentTable.getItems().removeAll(appointmentList);
        appointmentTable.setItems(monthlyAppointments);
    }

    public void byWeekHandler() {
        ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        LocalDateTime todayDateWeek = LocalDateTime.now();
        int day = todayDateWeek.getDayOfWeek().getValue();
        int monthDay = todayDateWeek.getDayOfMonth();
        int month = todayDateWeek.getMonth().getValue();
        int year = todayDateWeek.getYear();
        int n = 0;
        int count = 0;
        if (day != 0) {
            do {
                count += 1;
                day -= 1;
            } while (day > 1);
            for (Appointment app : appointmentList) {
                LocalDateTime appointmentDT = LocalDateTime.parse(appointmentList.get(n).getStartString(), dateTimeFormatter);
                int appointmentDay = appointmentDT.getDayOfMonth();
                int weekStart = monthDay - count;
                int weekEnd = weekStart + 7;
                int tempWeekStart = weekStart;
                if (appointmentDT.getYear() == year){
                    if (appointmentDT.getMonth().getValue() == month){
                        do {
                            if (appointmentDay == tempWeekStart) {
                                weeklyAppointments.add(app);
                            }
                            tempWeekStart += 1;
                        } while (tempWeekStart <= weekEnd);
                    }
                }
                n += 1;
            }
            appointmentTable.getItems().removeAll(appointmentList);
            appointmentTable.setItems(weeklyAppointments);
        }
        }

    public void appFilterHandler() {
        if (byMonthRadio.isSelected()) {
            byMonthHandler();
        } else if (byWeekRadio.isSelected()) {
            byWeekHandler();
        } else {
            Alert filterAlert = new Alert(Alert.AlertType.WARNING);
            filterAlert.setTitle("Select Month or Week");
            filterAlert.setContentText("Please select the Month or Week radio button.");
            filterAlert.showAndWait();
        }
    }

    public void appDeleteHandler() {
        try {
            int appointmentID = appointmentTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String type = appointmentTable.getSelectionModel().getSelectedItem().getType();
            Connection connection = DBConnection.getConn();

            Alert deleteCustomerAlert = new Alert(Alert.AlertType.CONFIRMATION,"Delete This Appointment?",ButtonType.YES, ButtonType.NO);
            deleteCustomerAlert.setContentText("Are you sure you wish to delete the following appointment?\n" +
                    "Appointment: " + appointmentID + "\n" +
                    "Type: " + type);
            deleteCustomerAlert.showAndWait();
            if (deleteCustomerAlert.getResult() == ButtonType.YES) {
                PreparedStatement deleteAppointment = connection.prepareStatement(QueryExecutions.deleteAppointment());
                deleteAppointment.setInt(1, appointmentID);
                int updatedRows = deleteAppointment.executeUpdate();
                appointmentTable.getItems().removeAll(appointmentTable.getSelectionModel().getSelectedItem());
                Alert deleteInfo = new Alert(Alert.AlertType.INFORMATION);
                if (updatedRows > 0) {
                    deleteInfo.setTitle("Appointment Deleted");
                    deleteInfo.setContentText("Appointment: " + appointmentID + " has been deleted.");
                } else {
                    deleteInfo.setTitle("Appointment Not Deleted");
                    deleteInfo.setContentText("Appointment: " + appointmentID + " has not been deleted.");
                }
                deleteInfo.showAndWait();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void appExitHandler() {
        Stage stage = (Stage) appExit.getScene().getWindow();
        stage.close();
    }

    public void appReportsHandler() {

    }
}
