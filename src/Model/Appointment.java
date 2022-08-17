package Model;

import java.sql.Date;
import java.sql.Timestamp;

public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contactName;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private int customerID;
    private int userID;
    private int contactID;


    public Appointment(int appointmentID, String title, String description, String location, String contactName, String type,
                       Timestamp start, Timestamp end, int customerID, int userID, int contactID) {
        setAppointmentID(appointmentID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contactName);
        setType(type);
        setStart(start);
        setEnd(end);
        setCustomerID(customerID);
        setUserID(userID);
        setContactID(contactID);
    }


    public int getAppointmentID() {
        return appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContactName() {
        return contactName;
    }

    public String getType() {
        return type;
    }

    public Timestamp getStartDate() {
        return start;
    }

    public Timestamp getEndDate() {
        return end;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getUserID() {
        return userID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContact(String contactName) {
        this.contactName = contactName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
}
