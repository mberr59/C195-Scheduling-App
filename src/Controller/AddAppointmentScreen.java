package Controller;

import Helper.DBConnection;
import Helper.QueryExecutions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public DatePicker addAppDateStart;
    public DatePicker addAppDateEnd;
    public ComboBox<LocalTime> addAppStartTime;
    public ComboBox<LocalTime> addAppEndTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { populateData(); }

    public void addAppSaveHandler() {
        Connection conn = DBConnection.getConn();
        String appTitle = addAppTitle.getText();
        String appDesc = addAppDesc.getText();
        String appLocation = addAppLocation.getText();
        String appContact = addAppContact.getSelectionModel().getSelectedItem();
        String appType = addAppType.getText();
        LocalDate appStartDate = addAppDateStart.getValue();
        LocalTime appStartTime = addAppStartTime.getSelectionModel().getSelectedItem();
        LocalDate appEndDate = addAppDateEnd.getValue();
        LocalTime appEndTime = addAppEndTime.getSelectionModel().getSelectedItem();
        Timestamp appAddStartTimestamp = setDateTimeFormat(appStartDate, appStartTime);
        Timestamp appAddEndTimestamp = setDateTimeFormat(appEndDate, appEndTime);
        int appAddCustomerID = Integer.parseInt(addAppCustomerID.getText());
        int appAddUserID = Integer.parseInt(addAppUserID.getText());
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
            addAppointmentData.setTimestamp(5, appAddStartTimestamp);
            addAppointmentData.setTimestamp(6, appAddEndTimestamp);
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

    public void populateData() {
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
    }
}
