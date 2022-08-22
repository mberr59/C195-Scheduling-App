package Controller;

import Helper.*;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ModifyAppointmentScreen {
    public TextField modAppTitle;
    public Button modAppSave;
    public Button modAppCancel;
    public TextField modAppDesc;
    public TextField modAppLocation;
    public ComboBox<String> modAppContact;
    public TextField modAppType;
    public DatePicker modAppStartDate;
    public DatePicker modAppEndDate;
    public TextField modAppCustomerID;
    public TextField modAppUserID;
    public ComboBox<LocalTime> modAppStartTime;
    public ComboBox<LocalTime> modAppEndTime;
    public TextField modAppID;

    // Lambda expression 4. Creates a PopulateData Interface. Populates the contact and time combo boxes with data.
    PopulateData modAppData = () -> {
        ObservableList<String> modAppContactList = FXCollections.observableArrayList();
        ObservableList<LocalTime> addAppTimeList = FXCollections.observableArrayList();
        LocalTime time = LocalTime.of(0, 0);
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
                modAppContactList.add(result);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        modAppContact.setItems(modAppContactList);
        modAppStartTime.setItems(addAppTimeList);
        modAppEndTime.setItems(addAppTimeList);
    };

    // Lambda expression 5. Lambda expression that creates a ConvertDateTime Interface passing a Timestamp object to it.
    // Converts the timestamp to EST then returns the converted Timestamp.
    ConvertDateTime timestampConversion = (timestamp) -> {
        LocalDateTime localDT = timestamp.toLocalDateTime();
        ZonedDateTime zonedDT = localDT.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZDT = zonedDT.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime convertedLDT = convertedZDT.toLocalDateTime();
        return Timestamp.valueOf(convertedLDT);
    };

    public void modAppSaveHandler() { updatingAppointmentData(); }

    public void modAppCancelHandler() {
        Stage stage = (Stage) modAppCancel.getScene().getWindow();
        stage.close();
    }

    public void populateAppFields(Appointment selectedItem) {
        try {
            modAppData.poplateData();
            Connection conn = DBConnection.getConn();
            PreparedStatement timestampQuery = conn.prepareStatement(QueryExecutions.getAppTimestamp());
            timestampQuery.setInt(1, selectedItem.getAppointmentID());
            ResultSet timestampRS = timestampQuery.executeQuery();
            timestampRS.next();
            Timestamp appStartTimestamp = timestampRS.getTimestamp("Start");
            Timestamp appEndTimestamp = timestampRS.getTimestamp("End");
            modAppTitle.setText(selectedItem.getTitle());
            modAppDesc.setText(selectedItem.getDescription());
            modAppID.setText(String.valueOf(selectedItem.getAppointmentID()));
            modAppLocation.setText(selectedItem.getLocation());
            modAppContact.getSelectionModel().select(selectedItem.getContactName());
            modAppType.setText(selectedItem.getType());
            modAppStartDate.setValue(appStartTimestamp.toLocalDateTime().toLocalDate());
            modAppStartTime.getSelectionModel().select(appStartTimestamp.toLocalDateTime().toLocalTime());
            modAppEndDate.setValue(appEndTimestamp.toLocalDateTime().toLocalDate());
            modAppEndTime.getSelectionModel().select(appEndTimestamp.toLocalDateTime().toLocalTime());
            modAppCustomerID.setText(String.valueOf(selectedItem.getCustomerID()));
            modAppUserID.setText(String.valueOf(selectedItem.getUserID()));
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void updatingAppointmentData() {
        Connection connection = DBConnection.getConn();
        int appID = Integer.parseInt(modAppID.getText());
        String appTitle = modAppTitle.getText();
        String appDesc = modAppDesc.getText();
        String appLocation = modAppLocation.getText();
        String appContact = modAppContact.getSelectionModel().getSelectedItem();
        String appType = modAppType.getText();
        LocalDate startDate = modAppStartDate.getValue();
        LocalDate endDate = modAppEndDate.getValue();
        LocalTime startTime = modAppStartTime.getValue();
        LocalTime endTime = modAppEndTime.getValue();
        Instant modStartDateTime = setDateTimeFormat(startDate, startTime).toInstant();
        Instant modEndDateTime = setDateTimeFormat(endDate, endTime).toInstant();
        int appCustomerID = Integer.parseInt(modAppCustomerID.getText());
        int appUserID = Integer.parseInt(modAppUserID.getText());

        ValidateDateTime isValidModDT = (instant) -> {
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

        if (isValidModDT.validateDateTime(modStartDateTime)) {
            if (isValidModDT.validateDateTime(modEndDateTime)) {
                if (modStartDateTime.isBefore(modEndDateTime)) {
                    Timestamp appModStartTimestamp = Timestamp.from(modStartDateTime);
                    Timestamp convertedStartTimestamp = timestampConversion.convertDateTime(appModStartTimestamp);
                    Timestamp appModEndTimestamp = Timestamp.from(modEndDateTime);
                    Timestamp convertedEndTimestamp = timestampConversion.convertDateTime(appModEndTimestamp);
                    try {
                        PreparedStatement contactStatement = connection.prepareStatement(QueryExecutions.getContactID());
                        contactStatement.setString(1, appContact);
                        ResultSet contactRS = contactStatement.executeQuery();
                        contactRS.next();
                        int contactID = contactRS.getInt("Contact_ID");
                        PreparedStatement updateAppointmentData = connection.prepareStatement(QueryExecutions.updateAppointmentQuery());
                        updateAppointmentData.setString(1, appTitle);
                        updateAppointmentData.setString(2, appDesc);
                        updateAppointmentData.setString(3, appLocation);
                        updateAppointmentData.setString(4, appType);
                        updateAppointmentData.setTimestamp(5, convertedStartTimestamp);
                        updateAppointmentData.setTimestamp(6, convertedEndTimestamp);
                        updateAppointmentData.setInt(7, appCustomerID);
                        updateAppointmentData.setInt(8, appUserID);
                        updateAppointmentData.setInt(9, contactID);
                        updateAppointmentData.setInt(10, appID);
                        int updatedRows = updateAppointmentData.executeUpdate();
                        if (updatedRows > 0) {
                            System.out.println("Appointment Update Successful");
                        } else {
                            System.out.println("Appointment Update Unsuccessful");
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

    public Timestamp setDateTimeFormat(LocalDate date, LocalTime time) {
        DateTimeFormatter sdfForTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedDateTime = LocalDateTime.of(date, time);
        return Timestamp.valueOf(formattedDateTime.format(sdfForTimestamp));
    }
}


