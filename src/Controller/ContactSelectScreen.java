package Controller;

import Helper.DBConnection;
import Helper.PopulateData;
import Helper.QueryExecutions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is designed to give the user the option to select a contact to view their appointment schedule
 * Controller class for Contact Select fxml.
 * Lambda Expression 7. Uses a Lambda Expression to call the Populate Data Interface to populate the contact ComboBox.
 */
public class ContactSelectScreen implements Initializable {
    public Button selectButton;
    public ComboBox<String> contactCB;
    public ObservableList<String> contactNames = FXCollections.observableArrayList();
    public ArrayList<String> tempStringArray = new ArrayList<>();
    public ArrayList<String> contactApps = new ArrayList<>();


    PopulateData contactName = () -> {
        try{
            Connection conn = DBConnection.getConn();
            PreparedStatement contact = conn.prepareStatement(QueryExecutions.getContactsName());
            ResultSet names = contact.executeQuery();
            while (names.next()) {
                contactNames.add(names.getString("Contact_Name"));
            }
            contactCB.setItems(contactNames);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    };


    /**
     * Select Button Handler. This method takes in the selected contact from the ComboBox and loops through all
     * appointments to grab all appointments that match the Contact ID of the selected contact. Creates a report and
     * passes the data to the Report screen.
     */
    public void selectButtonHandler() {
        try {
            if (!contactCB.getSelectionModel().isEmpty()) {
                Connection conn = DBConnection.getConn();
                String contactName = contactCB.getSelectionModel().getSelectedItem();
                PreparedStatement contactNameStatement = conn.prepareStatement(QueryExecutions.getContactID());
                contactNameStatement.setString(1, contactName);
                ResultSet contactRS = contactNameStatement.executeQuery();
                contactRS.next();
                int contactID = contactRS.getInt("Contact_ID");
                tempStringArray.add(".............................................................\n");
                tempStringArray.add("CONTACT: " + contactName + " SCHEDULE\n");
                PreparedStatement contactSchedule = conn.prepareStatement(QueryExecutions.getAppointmentByContactID());
                contactSchedule.setInt(1, contactID);
                ResultSet contactScheduleData = contactSchedule.executeQuery();
                while (contactScheduleData.next()) {
                    int contactAppID = contactScheduleData.getInt("Appointment_ID");
                    String contactAppTitle = contactScheduleData.getString("Title");
                    String contactAppType = contactScheduleData.getString("Type");
                    String contactAppDesc = contactScheduleData.getString("Description");
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                    LocalDateTime contactAppStart = contactScheduleData.getTimestamp("Start").toLocalDateTime();
                    ZonedDateTime zonedStart = contactAppStart.atZone(ZoneId.systemDefault());
                    LocalDateTime convertedAppStart = zonedStart.toLocalDateTime();
                    LocalDateTime contactAppEnd = contactScheduleData.getTimestamp("End").toLocalDateTime();
                    ZonedDateTime zonedEnd = contactAppEnd.atZone(ZoneId.systemDefault());
                    LocalDateTime convertedAppEnd = zonedEnd.toLocalDateTime();
                    int contactAppCustomerID = contactScheduleData.getInt("Customer_ID");
                    tempStringArray.add("-------------------------------------------\n");
                    tempStringArray.add("Appointment ID: " + contactAppID + "\n");
                    tempStringArray.add("Title: " + contactAppTitle + "\n");
                    tempStringArray.add("Type: " + contactAppType + "\n");
                    tempStringArray.add("Description: " + contactAppDesc + "\n");
                    tempStringArray.add("Start Date: " + convertedAppStart.toLocalDate() + "\n");
                    tempStringArray.add("Start Time: " + dateTimeFormatter.format(convertedAppStart.toLocalTime()) + "\n");
                    tempStringArray.add("End Date: " + convertedAppEnd.toLocalDate() + "\n");
                    tempStringArray.add("End Time: " + dateTimeFormatter.format(contactAppEnd.toLocalTime()) + "\n");
                    tempStringArray.add("Customer ID: " + contactAppCustomerID + "\n");

                    contactApps.addAll(tempStringArray);
                    tempStringArray.clear();

                }

                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ReportScreen.fxml")));
                Parent root = loader.load();
                ReportScreen reportScreen = loader.getController();
                for (String entry:contactApps) {
                    reportScreen.reportTF.appendText(entry);
                }
                Stage reportStage = new Stage();
                reportStage.setTitle("Contact Report");
                reportStage.setScene(new Scene(root));
                reportStage.show();
            } else {
                Alert contactSelect = new Alert(Alert.AlertType.ERROR);
                contactSelect.setTitle("Select Contact");
                contactSelect.setContentText("Please select a contact.");
                contactSelect.showAndWait();
            }
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactName.poplateData();
    }
}
