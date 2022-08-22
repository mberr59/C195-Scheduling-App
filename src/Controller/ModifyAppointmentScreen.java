package Controller;

import Helper.DBConnection;
import Helper.PopulateData;
import Helper.QueryExecutions;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalTime;

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

    public void modAppSaveHandler() {
    }

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
}


