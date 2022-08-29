package Controller;

import Helper.ConvertDateTime;
import Helper.DBConnection;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is the Appointment screen controller. This screen houses all the logic for: displaying all the appointment
 * data, filtering the appointments by current week and month, button to open the controller screen to add appointments,
 * passing the selected appointment to the "modify appointment" screen, button to open the customer list controller
 * and deleting a selected appointment.
 */
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
    public final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public Button appFilter;
    public ToggleGroup filterGroup;
    public Button appDelete;
    public Button appExit;
    public Button appReports;
    public int loginID;

    /**
     * Lambda expression 3. Creates a ConvertDateTime Interface. A timestamp object is passed to the method and then
     * is converted to EST. The converted timestamp is then returned.
     */
    ConvertDateTime timestampConversion = (timestamp) -> {

        LocalDateTime serverDT = timestamp.toLocalDateTime();
        ZonedDateTime zonedDT = serverDT.atZone(ZoneId.of("UTC"));
        ZonedDateTime convertedZoneDT = zonedDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        return convertedZoneDT.toLocalDateTime();
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { loadAppointments(loginID); }

    /**
     * Customer List Button Handler. This method opens the Customer screen controller for customer data manipulation.
     */
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

    /**
     * Adding Appointment Handler. This method opens the Add Appointment screen controller to add a new appointment.
     */
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

    /**
     * This button refreshes the information held in the appointment table to display all appointments for the logged in
     * user. This button is used after the list has been filtered using the Monthly or Weekly radio buttons.
     */
    public void refreshTableHandler() {
        int increment = 0;
        while (increment <= 2) {
            appointmentTable.refresh();
            appointmentTable.getItems().clear();
            loadAppointments(loginID);
            increment += 1;
        }

    }

    /**
     * Load Appointments method. This method houses the logic for creating a connection to a database and pulling all the
     * appointments for the using the passed in login ID.
     * @param loginID This is the user's login ID that is passed to the method for collecting the correct appointments.
     */
    public void loadAppointments(int loginID) {
        try {
            Connection conn = DBConnection.getConn();
            PreparedStatement appST = conn.prepareStatement(QueryExecutions.getAppByUser());
            appST.setInt(1, loginID);
            ResultSet rs = appST.executeQuery();

            while (rs.next()) {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
                Timestamp startDTToConvert = rs.getTimestamp("Start");
                LocalDateTime convertedStartDT = timestampConversion.convertDateTime(startDTToConvert);
                Timestamp endDTToConvert = rs.getTimestamp("End");
                LocalDateTime convertedEndDT = timestampConversion.convertDateTime(endDTToConvert);
                String startDTString = dateTimeFormatter.format(convertedStartDT);
                String endDTString = dateTimeFormatter.format(convertedEndDT);
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
    }

    /**
     * Modify Appointment Handler. This method loads the "Modify Appointment" screen and passes the selected appointment
     * data to the screen.
     */
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

    /**
     * Filter by Month Handler. This method houses the logic to filter the appointment list by current month.
     */
    public void byMonthHandler() {
        ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
        int todayDateMonth = LocalDateTime.now().getMonth().getValue();
        int todayDateYear = LocalDateTime.now().getYear();
        int n = 0;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
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
        appointmentTable.setItems(monthlyAppointments);

    }

    /**
     * Filter By Weekly Handler. This method houses the logic to filter the appointment list by current week.
     */
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
        }
        appointmentTable.setItems(weeklyAppointments);
    }

    /**
     * This button applies the necessary filter by checking to see which of the radio buttons was checked. If neither
     * of the radio buttons are selected, an Alert is displayed saying to select a radio button.
     */
    public void appFilterHandler() {
        appointmentTable.setItems(appointmentList);
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

    /**
     * Appointment Deletion Handler. This method houses the logic for deleting the selected appointment from the list
     * and database.
     */
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

    /**
     * Appointment Exit Handler. This method houses the logic to close the Appointment Screen.
     */
    public void appExitHandler() {
        Stage stage = (Stage) appExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Appointment Reports Handler. Houses the logic to open the "Choose Report" screen.
     */
    public void appReportsHandler() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/ChooseReportScreen.fxml")));
            Stage repStage = new Stage();
            repStage.setTitle("Choose Report");
            repStage.setScene(new Scene(root));
            repStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * Check Appointment Times method. This method houses the logic to check if an appointment is within 15 minutes
     * of the time the user logs in. Starting with comparing the year, and scales from there to the checking that the
     * appTime is 15 minutes or less from the login time. Also checks to see if the appTime is the next hour. If so, it
     * adds 60 minutes to the appTime minutes value and subtracts the login minutes value from it to see if the appointment
     * is at the top of the hour and within 15 minutes.
     * @param userID Method takes in the userID to get all appointments tied to that user then does the timestamp comparison.
     */
    public void checkAppointmentTimes(int userID) {
        LocalDateTime currentTime = LocalDateTime.now();
        Alert appAlert = new Alert(Alert.AlertType.INFORMATION);
        appAlert.setTitle("Appointments");
        appAlert.setContentText("No appointments within 15 minutes.");
        try {
            Connection conn = DBConnection.getConn();
            PreparedStatement appCheck = conn.prepareStatement(QueryExecutions.getAppByUser());
            appCheck.setInt(1, userID);
            ResultSet appRS = appCheck.executeQuery();
            while(appRS.next()) {
                LocalDateTime appTime = appRS.getTimestamp("Start").toLocalDateTime();
                if (currentTime.getYear() == appTime.getYear()) {
                    if (currentTime.getDayOfYear() == appTime.getDayOfYear()) {
                        if (currentTime.getHour() == appTime.getHour()) {
                            if ((appTime.getMinute() - currentTime.getMinute()) <= 15 && appTime.isAfter(currentTime)) {
                                ZonedDateTime zonedAppTime = appTime.atZone(ZoneId.systemDefault());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
                                LocalDateTime convertedAppTime = zonedAppTime.toLocalDateTime();
                                appAlert.setContentText("Appointment: " + appRS.getInt("Appointment_ID") + "\nDate: " +
                                        convertedAppTime.toLocalDate().toString() + "\nTime: " + dtf.format(convertedAppTime.toLocalTime()) +
                                        "\nis within 15 minutes.");
                            }
                        } else if (appTime.getHour() == (currentTime.getHour() + 1)){
                            if (((appTime.getMinute() + 60) - currentTime.getMinute()) <= 15 && appTime.isAfter(currentTime)) {
                                ZonedDateTime zonedAppTime = appTime.atZone(ZoneId.systemDefault());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
                                LocalDateTime convertedAppTime = zonedAppTime.toLocalDateTime();
                                appAlert.setContentText("Appointment: " + appRS.getInt("Appointment_ID") + "\nDate: " +
                                        convertedAppTime.toLocalDate().toString() + "\nTime: " + dtf.format(convertedAppTime.toLocalTime()) +
                                        "\nis within 15 minutes.");
                            }
                        }
                    }
                }
            }
            appAlert.showAndWait();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }
}
