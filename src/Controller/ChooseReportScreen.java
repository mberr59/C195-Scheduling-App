package Controller;

import Helper.DBConnection;
import Helper.QueryExecutions;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
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
    public ArrayList<String> tempStringArray = new ArrayList<>();
    public ArrayList<String> contactAppTotal = new ArrayList<>();
    public ArrayList<String> customerAppByType = new ArrayList<>();


    public void custAppRepHandler() {
        try {
            Connection conn = DBConnection.getConn();
            ArrayList<String> appType = new ArrayList<>();
            PreparedStatement customerStatement = conn.prepareStatement(QueryExecutions.getSelectAppointmentQuery(),
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet customerRS = customerStatement.executeQuery();
            while (customerRS.next()) {
                String type = customerRS.getString("Type");
                if (!appType.contains(type)) {
                    appType.add(type);
                }
            }
            ResultSet secondRS = customerStatement.executeQuery();
            int totalOfType = 0;
            int incrementMonth = 1;
            while (incrementMonth <= 12) {
                tempStringArray.add("Month of " + Month.of(incrementMonth) + ":\n");
                tempStringArray.add("----------------------------------\n");
                for (String type : appType) {
                    while (secondRS.next()) {
                        if ((secondRS.getString("Type").equals(type)) &&
                                secondRS.getTimestamp("Start").toLocalDateTime().getMonth() == Month.of(incrementMonth)) {
                            totalOfType += 1;
                        }
                    }
                    tempStringArray.add(type + " Total: " + totalOfType + "\n");
                    tempStringArray.add(".............................\n");
                    totalOfType = 0;
                    secondRS.beforeFirst();
                }
                incrementMonth += 1;
            }
                customerAppByType.addAll(tempStringArray);
                tempStringArray.clear();

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ReportScreen.fxml")));
            Parent root = loader.load();
            ReportScreen reportScreen = loader.getController();
            for (String entry:customerAppByType) {
                reportScreen.reportTF.appendText(entry);
            }
            Stage reportStage = new Stage();
            reportStage.setTitle("Contact Report");
            reportStage.setScene(new Scene(root));
            reportStage.show();

        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }

    }

    public void contactRepHandler() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ContactSelectScreen.fxml")));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Contact Report");
            reportStage.setScene(new Scene(root));
            reportStage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void allAppHandler() {
        try {
            Connection conn = DBConnection.getConn();
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
                tempStringArray.add(".............................................................\n");
                tempStringArray.add("CONTACT: " + contactName + " SCHEDULE\n");
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
                    }
                    contactAppTotal.addAll(tempStringArray);
                    tempStringArray.clear();
                }
            }
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ReportScreen.fxml")));
            Parent root = loader.load();
            ReportScreen reportScreen = loader.getController();
            for (String entry:contactAppTotal) {
                reportScreen.reportTF.appendText(entry);
            }
            Stage reportStage = new Stage();
            reportStage.setTitle("Contact Report");
            reportStage.setScene(new Scene(root));
            reportStage.show();
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void repCloseHandler() {
        Stage stage = (Stage) repClose.getScene().getWindow();
        stage.close();
    }
}
