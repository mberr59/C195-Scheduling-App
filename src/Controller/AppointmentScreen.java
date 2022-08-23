package Controller;

import Helper.DBConnection;
import Helper.PopulateData;
import Helper.QueryExecutions;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppointmentScreen implements Initializable {
    public TableView<Appointment> appointmentTable;
    public TableColumn<Appointment, Integer> appointmentID;
    public TableColumn<Appointment, String> appointmentTitle;
    public TableColumn<Appointment, String> appointmentDesc;
    public TableColumn<Appointment, String> appointmentLocation;
    public TableColumn<Appointment, String> appointmentContact;
    public TableColumn<Appointment, String> appointmentType;
    public TableColumn<Appointment, String> appointmentStart;
    public TableColumn<Appointment, String> appointmentEnd;
    public TableColumn<Appointment, Integer> appointmentCustomerID;
    public TableColumn<Appointment, Integer> appointmentUserID;
    public Button addAppointment;
    public Button customerListButton;
    public Button refreshTableButton;
    public Button modAppointment;
    public RadioButton byMonthRadio;
    public RadioButton byWeekRadio;
    public ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    public LocalDateTime startDateTime;
    public Button appFilter;
    public ToggleGroup filterGroup;

    // Lambda Expression 7. Creates a PopulateData Interface and passes the appointment data to the Interface using
    // a Lambda Expression block.
    PopulateData appointmentData = () -> {
        try {
            Connection conn = DBConnection.getConn();
            String query = QueryExecutions.getSelectAppointmentQuery();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {

                int appointmentID = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
                startDateTime = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endDateTime = rs.getTimestamp("End").toLocalDateTime();
                String startDTString = dateTimeFormatter.format(startDateTime);
                String endDTString = dateTimeFormatter.format(endDateTime);
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                PreparedStatement contactStatement = conn.prepareStatement(QueryExecutions.getContactsQuery());
                contactStatement.setInt(1, contactID);
                ResultSet contactRS = contactStatement.executeQuery();
                contactRS.next();
                String contactName = contactRS.getString("Contact_Name");
                Appointment appointment = new Appointment(appointmentID, title, description, location, contactName,
                        type, startDTString, endDTString, customerID, userID, contactID);
                appointmentList.add(appointment);

            }
            appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            appointmentDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
            appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
            appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startString"));
            appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endString"));
            appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
            appointmentTable.setItems(appointmentList);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { appointmentData.poplateData(); }

    public void customerListButtonHandler() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/CustomerScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Customer Screen");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void addAppointmentHandler() {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/AddAppointmentScreen.fxml")));
            Stage appStage = new Stage();
            appStage.setTitle("Add Appointment");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void refreshTableHandler() { appointmentTable.refresh();}

    public void modAppointmentHandler() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/ModifyAppointmentScreen.fxml")));
            Parent root = loader.load();

            ModifyAppointmentScreen modApp = loader.getController();
            modApp.populateAppFields(appointmentTable.getSelectionModel().getSelectedItem());
            Stage appStage = new Stage();
            appStage.setTitle("Modify Appointment Screen");
            appStage.setScene(new Scene(root));
            appStage.show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            Alert selectionAlert = new Alert(Alert.AlertType.ERROR);
            selectionAlert.setTitle("Selection Error");
            selectionAlert.setContentText("Please select an Appointment to modify.");
            selectionAlert.showAndWait();
        }
    }

    public void byMonthHandler() {
        int todayDateMonth = LocalDateTime.now().getMonth().getValue();
        int n = 0;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
        for (Appointment app: appointmentList) {
            LocalDateTime appointmentDT = LocalDateTime.parse(appointmentList.get(n).getStartString(), dateTimeFormatter);
            int appointmentMonth = appointmentDT.getMonth().getValue();
            if (todayDateMonth == appointmentMonth) {
                monthlyAppointments.add(app);
            }
            n += 1;
        }
        appointmentTable.setItems(monthlyAppointments);
    }

    public void byWeekHandler() {
        ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh':'mm a");
        LocalDateTime todayDateWeek = LocalDateTime.now();
        int day = todayDateWeek.getDayOfWeek().getValue();
        int n = 0;
        do {
            day -= 1;
        } while (day > 1);
        for (Appointment app : appointmentList) {
            LocalDateTime appointmentDT = LocalDateTime.parse(appointmentList.get(n).getStartString(), dateTimeFormatter);
            int appointmentDay = appointmentDT.getDayOfWeek().getValue();
            do {
                if (appointmentDay == day) {
                    weeklyAppointments.add(app);
                }
                day += 1;
            } while (day <= 7);
            day = 1;
            n += 1;
        }
        appointmentTable.setItems(weeklyAppointments);
    }

    public void appFilterHandler() {
        if (byMonthRadio.isSelected()) {
            byMonthHandler();
        } else if (byWeekRadio.isSelected()) {
            byWeekHandler();
        } else {
            Alert filterAlert = new Alert(Alert.AlertType.WARNING);
            filterAlert.setTitle("Select Month or Week");
            filterAlert.setContentText("Please select the Month or Week radio button.");
            filterAlert.showAndWait();
        }
    }
}
