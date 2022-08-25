package Controller;

import Helper.DBConnection;
import Helper.QueryExecutions;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ChooseReportScreen {
    public Button custAppRep;
    public Button contactRep;
    public Button ftcApp;
    public Button repClose;
    private final File contactFile = new File("C:\\Users\\micah\\Logs\\ContactReport.txt");
    private final File customerAppFile = new File("C:\\Users\\micah\\Logs\\CustomerAppReport.txt");
    private final File customerNewFile = new File("C:\\Users\\micah\\Logs\\NewCustomerReport.txt");


    public void custAppRepHandler() {

    }

    public void contactRepHandler() {
        try {
            Connection conn = DBConnection.getConn();
            FileWriter writer = new FileWriter(contactFile);
            ArrayList<Integer> contacts = new ArrayList<>();
            PreparedStatement contactStatement = conn.prepareStatement(QueryExecutions.getSelectAppointmentQuery());
            ResultSet contactRS = contactStatement.executeQuery();
            while (contactRS.next()) {
                int contactNum = contactRS.getInt("Contact_ID");
                if (!contacts.contains(contactNum)) {
                    contacts.add(contactNum);
                }
            }
            PreparedStatement contactInfo = conn.prepareStatement(QueryExecutions.getAllContacts());
            ResultSet contactInfoRS = contactInfo.executeQuery();
            while (contactInfoRS.next()) {
                    String contactName = contactInfoRS.getString("Contact_Name");
                    int contactID = contactInfoRS.getInt("Contact_ID");
                    writer.write(".............................................................\n");
                    writer.write("CONTACT: " + contactName + " SCHEDULE\n");
                    PreparedStatement contactSchedule = conn.prepareStatement(QueryExecutions.getAppointmentByContactID());
                    contactSchedule.setInt(1, contactID);
                    ResultSet contactScheduleData = contactSchedule.executeQuery();
                    while (contactScheduleData.next()) {
                        if (contactScheduleData.getInt("Contact_ID") == contactID) {
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
                            writer.write("-------------------------------------------\n");
                            writer.write("Appointment ID: " + contactAppID + "\n");
                            writer.write("Title: " + contactAppTitle + "\n");
                            writer.write("Type: " + contactAppType + "\n");
                            writer.write("Description: " + contactAppDesc + "\n");
                            writer.write("Start Date: " + convertedAppStart.toLocalDate() + "\n");
                            writer.write("Start Time: " + dateTimeFormatter.format(convertedAppStart.toLocalTime()) + "\n");
                            writer.write("End Date: " + convertedAppEnd.toLocalDate() + "\n");
                            writer.write("End Time: " + dateTimeFormatter.format(contactAppEnd.toLocalTime()) + "\n");
                            writer.write("Customer ID: " + contactAppCustomerID + "\n");
                    }
                }
            }
            writer.close();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ReportScreen.fxml")));
            Parent root = loader.load();
            ReportScreen reportScreen = loader.getController();
            reportScreen.showReport(contactFile);
            Stage reportStage = new Stage();
            reportStage.setTitle("Contact Report");
            reportStage.setScene(new Scene(root));
            reportStage.show();
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void ftcAppHandler() {
    }

    public void repCloseHandler() {
        Stage stage = (Stage) repClose.getScene().getWindow();
        stage.close();
    }
}
