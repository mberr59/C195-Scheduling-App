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

/**
 * This is the controller for the "Modify Appointment" screen. It houses the logic for a user to change the information
 * pulled in from the Appointment screen, make changes, then updates the data in the database.
 * Also uses a Lambda expression to populate the data fields.
 *
 * Lambda expression 4. Creates a PopulateData Interface. Populates the contact and time combo boxes with data.
 * I chose to use a lambda expression for most of the screens to populate data. Most of the logic is the same between
 * screens so instead of creating a populate data method on each screen, I chose to create an Interface then used a
 * lambda expression to call the interface passing the data through it.
 *
 * Lambda expression 5. Lambda expression that creates a ConvertDateTime Interface passing a Timestamp object to it.
 * Converts the timestamp to EST then returns the converted Timestamp.
 * The Convert Date Time Interface is the other way I used Lambda expressions. This function needs to be used at
 * different times throughout the application so I created a Interface and pass the needed parameters through the
 * Lambda and convert the timestamp to Eastern time within the Lambda code block.
 */
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

    ConvertDateTime timestampConversion = (timestamp) -> {
        LocalDateTime localDT = timestamp.toLocalDateTime();
        ZonedDateTime zonedDT = localDT.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZDT = zonedDT.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDateTime convertedLDT = convertedZDT.toLocalDateTime();
        return Timestamp.valueOf(convertedLDT);
    };

    /**
     * Modify Appointment Save Handler. This method calls the Updating Appointment Data when clicked.
     */
    public void modAppSaveHandler() { updatingAppointmentData(); }

    /**
     * Modify Appointment Cancel Handler. Closes the Modify Appointment screen.
     */
    public void modAppCancelHandler() {
        Stage stage = (Stage) modAppCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Populate Appointment Fields. This method is called from the Appointment screen when this screen is being loaded
     * It takes in the selected item from the appointment screen.
     * @param selectedItem Appointment object passed from the selected item in the Appointment screen.
     */
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

    /**
     * Updating Appointment Data. This method takes in all the data within the user fields and tries to update the item
     * in the database with the data if the data passes validation checks.
     *
     * Lambda Expression 8. ValidateDateTime taking in a instant object. Converts the Time to EST and then checks if
     * the date is Saturday or Sunday then checks if the time is between 8am to 10pm EST.
     */
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
                    if (checkForOverlap(appCustomerID, appModStartTimestamp, appModEndTimestamp, appID)) {
                        return;
                    }
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
                    } catch (NullPointerException npe) {
                        Alert npeAlert = new Alert(Alert.AlertType.ERROR);
                        npeAlert.setTitle("Input Error");
                        npeAlert.setContentText("Error n entered data. Please check data provided.");
                        return;
                    } catch (NumberFormatException numberFormatException) {
                        Alert nfeAlert = new Alert(Alert.AlertType.ERROR);
                        nfeAlert.setTitle("Numeric Error");
                        nfeAlert.setContentText("Please enter a valid User ID and Customer ID");
                        return;
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
     * Set Date Time Format. This method takes in a LocalDate and LocalTime. It then creates a LocalDateTime using the
     * passed parameters. Lastly returns a timestamp from the LocalDateTime in the specified pattern.
     * @param date The LocalDate parameter passed to the function.
     * @param time The LocalTime parameter passed to the function.
     * @return The converted and formatted Timestamp object is returned.
     */
    public Timestamp setDateTimeFormat(LocalDate date, LocalTime time) {
        DateTimeFormatter sdfForTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedDateTime = LocalDateTime.of(date, time);
        return Timestamp.valueOf(formattedDateTime.format(sdfForTimestamp));
    }

    /**
     * Check for Overlap. This method takes in the Customer ID, startTime Timestamp, endTime Timestamp, and the
     * Appointment ID as parameters. Compares the passed in startTime and endTime to each appointment tied to the
     * passed in Customer ID. Each appointment is checked to make sure that the Appointment thats ID matches the
     * passed in App ID is skipped over in the comparisons.
     *
     * @param c_ID Customer ID used to get all appointments tied to this customer.
     * @param startTime Start time of the proposed appointment.
     * @param endTime End time of the proposed appointment.
     * @param appID Appointment ID of the Appointment being updated.
     * @return returns a true value if a conflict is detected.
     */
    public boolean checkForOverlap(int c_ID, Timestamp startTime, Timestamp endTime, int appID) {
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
                if (appID != appRS.getInt("Appointment_ID")) {
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
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return overlapDetected;
    }
}


