package Controller;

import Helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


/**
 * This is the Add Appointment screen controller. This class is responsible for all of the logic
 * for taking in user input, validating the data then adding the data to the database.
 */
public class AddAppointmentScreen implements Initializable {
    public TextField addAppTitle;
    public TextField addAppDesc;
    public TextField addAppLocation;
    public ComboBox<String> addAppContact;
    public TextField addAppType;
    public TextField addAppCustomerID;
    public TextField addAppUserID;
    public Button addAppSave;
    public Button addAppCancel;
    public DatePicker addAppStartDate;
    public DatePicker addAppEndDate;
    public ComboBox<LocalTime> addAppStartTime;
    public ComboBox<LocalTime> addAppEndTime;


    /**
     * Lambda expression 2. Creates a PopulateData Interface and passes instructions to add the Appointment data
     * to the proper fields using a Lambda expression.
     */
    PopulateData addAppData = () -> {
        ObservableList<String> addAppContactList = FXCollections.observableArrayList();
        ObservableList<LocalTime> addAppTimeList = FXCollections.observableArrayList();
        LocalTime time = LocalTime.of(0,0);
            addAppTimeList.add(time);
        int n = 0;
            do {
            time = time.plusMinutes(15);
            addAppTimeList.add(time);
            if (time.getMinute() == 45) {
                if (time.getHour() == 23) {
                    break;
                }
                time = time.plusHours(1);
                time = time.minusMinutes(45);
                addAppTimeList.add(time);
                n += 1;
            }
        } while (n < 24);

            try {
            Connection connection = DBConnection.getConn();
            PreparedStatement contactNames = connection.prepareStatement(QueryExecutions.getContactsName());
            ResultSet contactRS = contactNames.executeQuery();
            while (contactRS.next()) {
                String result = contactRS.getString("Contact_Name");
                addAppContactList.add(result);
            }
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }
            addAppContact.setItems(addAppContactList);
            addAppStartTime.setItems(addAppTimeList);
            addAppEndTime.setItems(addAppTimeList);
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAppData.poplateData();
    }

    /**
     * Add Appointment Save Button Handler. This method takes in all data provided by the user and tries to add the
     * data into the database provided it passes the validation checks. Contains the following Lambda expression:
     * Lambda expression 1. Creates a ValidateDateTime boolean Interface. Passing an Instant object to the
     * Interface, then checking the date to confirm it is a weekday also checking the time is 8am to 10pm EST through
     * a Lambda expression.
     *
     */
    public void addAppSaveHandler() {
        Connection conn = DBConnection.getConn();
        String appTitle = addAppTitle.getText();
        String appDesc = addAppDesc.getText();
        String appLocation = addAppLocation.getText();
        String appContact = addAppContact.getSelectionModel().getSelectedItem();
        String appType = addAppType.getText();
        LocalDate appStartDate = addAppStartDate.getValue();
        LocalTime appStartTime = addAppStartTime.getSelectionModel().getSelectedItem();
        LocalDate appEndDate = addAppEndDate.getValue();
        LocalTime appEndTime = addAppEndTime.getSelectionModel().getSelectedItem();
        Instant appStartDateTime = setDateTimeFormat(appStartDate, appStartTime).toInstant();
        Instant appEndDateTime = setDateTimeFormat(appEndDate, appEndTime).toInstant();
        int appAddCustomerID = Integer.parseInt(addAppCustomerID.getText());
        int appAddUserID = Integer.parseInt(addAppUserID.getText());

        ValidateDateTime isValidAddDT = (instant) -> {
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            ZonedDateTime eastDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

         if ((eastDateTime.getHour() < 8) || (eastDateTime.getHour() > 22)) {
            Alert timeAlert = new Alert(Alert.AlertType.ERROR);
            timeAlert.setTitle("Invalid Time");
            timeAlert.setContentText("Please enter a valid time between 8am and 10pm EST.");
            timeAlert.showAndWait();
            return false;
        } else {
            return true;
        }
    };

        if (isValidAddDT.validateDateTime(appStartDateTime)) {
            if (isValidAddDT.validateDateTime(appEndDateTime)) {
                if (appStartDateTime.isBefore(appEndDateTime)) {
                    LocalDateTime ldtStart =  LocalDateTime.ofInstant(appStartDateTime, ZoneId.of("UTC"));
                    LocalDateTime ldtEnd =  LocalDateTime.ofInstant(appEndDateTime, ZoneId.of("UTC"));
                    if (checkForOverlap(appAddCustomerID, ldtStart, ldtEnd)) {
                        return;
                    }
                    try {
                        PreparedStatement contactStatement = conn.prepareStatement(QueryExecutions.getContactID());
                        contactStatement.setString(1, appContact);
                        ResultSet contactRS = contactStatement.executeQuery();
                        contactRS.next();
                        int contactID = contactRS.getInt("Contact_ID");
                        PreparedStatement addAppointmentData = conn.prepareStatement(QueryExecutions.addAppointmentQuery());
                        addAppointmentData.setString(1, appTitle);
                        addAppointmentData.setString(2, appDesc);
                        addAppointmentData.setString(3, appLocation);
                        addAppointmentData.setString(4, appType);
                        addAppointmentData.setTimestamp(5, Timestamp.valueOf(ldtStart));
                        addAppointmentData.setTimestamp(6, Timestamp.valueOf(ldtEnd));
                        addAppointmentData.setInt(7, appAddCustomerID);
                        addAppointmentData.setInt(8, appAddUserID);
                        addAppointmentData.setInt(9, contactID);
                        int updatedRows = addAppointmentData.executeUpdate();
                        if (updatedRows > 0) {
                            System.out.println("Appointment Insert Successful");
                        } else {
                            System.out.println("Appointment Insert Unsuccessful");
                        }

                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    } catch (NumberFormatException numberFormatException) {
                        Alert nfeAlert = new Alert(Alert.AlertType.ERROR);
                        nfeAlert.setTitle("Numeric Error");
                        nfeAlert.setContentText("Please enter a valid User ID and Customer ID");
                    } catch (NullPointerException npe) {
                        Alert npeAlert = new Alert(Alert.AlertType.ERROR);
                        npeAlert.setTitle("Input Error");
                        npeAlert.setContentText("Error n entered data. Please check data provided.");
                    }
                } else {
                    Alert startAfterEnd = new Alert(Alert.AlertType.ERROR);
                    startAfterEnd.setTitle("Start date/time after End date/time");
                    startAfterEnd.setContentText("The Start date/time cannot be to a date/time after End date/time.");
                    startAfterEnd.showAndWait();
                }
            }
        }
    }

    /**
     * Add Appointment Cancel Handler. Closes the Add Appointment screen.
     */
    public void addAppCancelHandler() {
        Stage stage = (Stage) addAppCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Set Date and Time Format. Takes in the passed in Date and Time from the user and converts it to a LocalDateTime
     * then to a Timestamp. Also sets the format of the returned timestamp.
     * @param date Date provided from the user in the DatePicker
     * @param time Time provided from the user in the Time ComboBox
     * @return Returns the created Timestamp back to the calling method to use in adding to the database.
     */
    public Timestamp setDateTimeFormat(LocalDate date, LocalTime time) {
        DateTimeFormatter sdfForTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedDateTime = LocalDateTime.of(date, time);
        return Timestamp.valueOf(formattedDateTime.format(sdfForTimestamp));
    }

    /**
     * Check for Overlap method. Checks the passed startTime and endTime timestamps to confirm that there is no overlap
     * between them before adding them to the appointment table. Also takes in the Customer ID to get the timestamps of
     * each appointment that the customer has to make sure there is no overlap.
     * @param c_ID Customer ID used to get all appointments of customer.
     * @param startTime This is the startTime that the user entered.
     * @param endTime This is the endTime that the user entered.
     * @return Returns a boolean overlapDetected. If false the data is added to the server. If true the program is halted
     * until the user enters data again.
     */
    public boolean checkForOverlap(int c_ID, LocalDateTime startTime, LocalDateTime endTime) {
        boolean overlapDetected = false;
        try {
            Connection connection = DBConnection.getConn();
            Alert overlapAlert = new Alert(Alert.AlertType.ERROR);
            PreparedStatement appStatement = connection.prepareStatement(QueryExecutions.getAppTimestampCustomer());
            appStatement.setInt(1, c_ID);
            ResultSet appRS = appStatement.executeQuery();
            while (appRS.next()) {
                LocalDateTime savedStart = appRS.getTimestamp("Start").toLocalDateTime();
                LocalDateTime savedEnd = appRS.getTimestamp("End").toLocalDateTime();
                if ((savedStart.isAfter(startTime) || savedStart.isEqual(startTime)) && savedStart.isBefore(endTime)) {
                    overlapAlert.setTitle("Overlapping Appointments");
                    overlapAlert.setContentText("This time conflicts with an already scheduled appointment.\n" +
                            "Please enter a different time.");
                    overlapAlert.showAndWait();
                    overlapDetected = true;

                } else if (savedEnd.isAfter(startTime) && (savedEnd.isBefore(endTime) || savedEnd.isEqual(endTime))) {
                    overlapAlert.setTitle("Overlapping Appointments");
                    overlapAlert.setContentText("This time conflicts with an already scheduled appointment.\n" +
                            "Please enter a different time.");
                    overlapAlert.showAndWait();
                    overlapDetected = true;

                } else if ((savedStart.isBefore(startTime) || savedStart.isEqual(startTime)) && (savedEnd.isAfter(endTime) || savedEnd.isEqual(endTime))) {
                    overlapAlert.setTitle("Overlapping Appointments");
                    overlapAlert.setContentText("This time conflicts with an already scheduled appointment.\n" +
                            "Please enter a different time.");
                    overlapAlert.showAndWait();
                    overlapDetected = true;
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return overlapDetected;
    }
}
