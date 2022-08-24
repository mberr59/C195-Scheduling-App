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

    // Lambda expression 3. Creates a ConvertDateTime Interface. A timestamp object is passed to the method and then
    // is converted to EST. The converted timestamp is then returned.
    ConvertDateTime timestampConversion = (timestamp) -> {
        LocalDateTime localDT = timestamp.toLocalDateTime();
        ZonedDateTime zonedDT = localDT.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZDT = zonedDT.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime convertedLDT = convertedZDT.toLocalDateTime();
        return Timestamp.valueOf(convertedLDT);
    };

    // Lambda expression 2. Creates a PopulateData Interface and passes instructions to add the Appointment data
    // to the proper fields using a Lambda expression.
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

        // Lambda expression 1. Creates a ValidateDateTime boolean Interface. Passing an Instant object to the
        // Interface, then checking the date to confirm it is a weekday also checking the time is 8am to 10pm EST through
        // a Lambda expression.
        ValidateDateTime isValidAddDT = (instant) -> {
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            ZonedDateTime eastDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

            if (eastDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || eastDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                Alert dayAlert = new Alert(Alert.AlertType.ERROR);
                dayAlert.setTitle("Invalid Day");
                dayAlert.setContentText("Please enter a valid workday (Mon-Fri).");
                dayAlert.showAndWait();
                return false;
            } else if ((eastDateTime.getHour() < 8) || (eastDateTime.getHour() > 22)) {
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
                    Timestamp appAddStartTimestamp = Timestamp.from(appStartDateTime);
                    Timestamp convertedStartTimestamp = timestampConversion.convertDateTime(appAddStartTimestamp);
                    Timestamp appAddEndTimestamp = Timestamp.from(appEndDateTime);
                    Timestamp convertedEndTimestamp = timestampConversion.convertDateTime(appAddEndTimestamp);
                    if (checkForOverlap(appAddCustomerID, appAddStartTimestamp, appAddEndTimestamp)) {
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
                        addAppointmentData.setTimestamp(5, convertedStartTimestamp);
                        addAppointmentData.setTimestamp(6, convertedEndTimestamp);
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

    public void addAppCancelHandler() {
        Stage stage = (Stage) addAppCancel.getScene().getWindow();
        stage.close();
    }

    public Timestamp setDateTimeFormat(LocalDate date, LocalTime time) {
        DateTimeFormatter sdfForTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedDateTime = LocalDateTime.of(date, time);
        return Timestamp.valueOf(formattedDateTime.format(sdfForTimestamp));
    }

    public boolean checkForOverlap(int c_ID, Timestamp startTime, Timestamp endTime) {
        boolean overlapDetected = false;
        try {
            Connection connection = DBConnection.getConn();
            Alert overlapAlert = new Alert(Alert.AlertType.ERROR);
            LocalDateTime proposedStart = startTime.toLocalDateTime();
            LocalDateTime proposedEnd = endTime.toLocalDateTime();
            PreparedStatement appStatement = connection.prepareStatement(QueryExecutions.getAppTimestampCustomer());
            appStatement.setInt(1, c_ID);
            ResultSet appRS = appStatement.executeQuery();
            while (appRS.next()) {
                LocalDateTime savedStart = appRS.getTimestamp("Start").toLocalDateTime();
                LocalDateTime savedEnd = appRS.getTimestamp("End").toLocalDateTime();
                if ((savedStart.isAfter(proposedStart) || savedStart.isEqual(proposedStart)) && savedStart.isBefore(proposedEnd)) {
                    overlapAlert.setTitle("Overlapping Appointments");
                    overlapAlert.setContentText("This time conflicts with an already scheduled appointment.\n" +
                            "Please enter a different time.");
                    overlapAlert.showAndWait();
                    overlapDetected = true;

                } else if (savedEnd.isAfter(proposedStart) && (savedEnd.isBefore(proposedEnd) || savedEnd.isEqual(proposedEnd))) {
                    overlapAlert.setTitle("Overlapping Appointments");
                    overlapAlert.setContentText("This time conflicts with an already scheduled appointment.\n" +
                            "Please enter a different time.");
                    overlapAlert.showAndWait();
                    overlapDetected = true;

                } else if ((savedStart.isBefore(proposedStart) || savedStart.isEqual(proposedStart)) && (savedEnd.isAfter(proposedEnd) || savedEnd.isEqual(proposedEnd))) {
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
