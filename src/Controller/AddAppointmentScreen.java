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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public TextField addAppTimeStart;
    public TextField addAppTimeEnd;
    public ChoiceBox<String> addAppAMPMStart;
    public ChoiceBox<String> addAppAMPMEnd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { populateComboboxes(); }

    public void addAppSaveHandler() {
        Connection conn = DBConnection.getConn();
        String appTitle = addAppTitle.getText();
        String appDesc = addAppDesc.getText();
        String appLocation = addAppLocation.getText();
        String appContact = addAppContact.getSelectionModel().getSelectedItem();
        String appType = addAppType.getText();
        String appStartDate = addAppDateStart.getValue().toString();
        String appStartTime = addAppTimeStart.getText() + " ";
        String appStartAMPM = addAppAMPMStart.getValue();
        String appEndDate = addAppDateEnd.getValue().toString();
        String appEndTime = addAppTimeEnd.getText() + " ";
        String appEndAMPM = addAppAMPMEnd.getValue();
        Timestamp appAddStartTimestamp = setDateTimeFormat(appStartDate, appStartTime, appStartAMPM);
        Timestamp appAddEndTimestamp = setDateTimeFormat(appEndDate, appEndTime, appEndAMPM);
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

    public Timestamp setDateTimeFormat(String date, String time, String timeOfDay) {
        try{
            SimpleDateFormat sdfForTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf12HourFormat = new SimpleDateFormat("HH:mm a");
            SimpleDateFormat sdf24HourFormat = new SimpleDateFormat("HH:mm");
            String convertTo12 = sdf12HourFormat.parse(time);
            return (Timestamp) sdfForTimestamp.parse(newTime);

        }catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

    public void populateComboboxes() {
        ObservableList<String> addAppAMPMList = FXCollections.observableArrayList("AM", "PM");
        ObservableList<String> addAppContactList = FXCollections.observableArrayList();
        addAppAMPMStart.setItems(addAppAMPMList);
        addAppAMPMStart.getSelectionModel().selectFirst();
        addAppAMPMEnd.setItems(addAppAMPMList);
        addAppAMPMEnd.getSelectionModel().selectFirst();
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
    }
}
