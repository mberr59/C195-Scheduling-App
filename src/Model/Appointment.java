package Model;

import java.time.LocalDateTime;

/**
 * This class holds the Appointment object constructors and all of its necessary getters and setters.
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contactName;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String startString;
    private String endString;
    private int customerID;
    private int userID;
    private int contactID;

    /**
     * Constructor for Appointment object with LocalDateTime start and end values.
     * @param appointmentID Appointment's Id
     * @param title Appointment's Title
     * @param description Appointment's Description
     * @param location Appointment's Location
     * @param contactName Appointment's Contact Name
     * @param type Appointment's Type
     * @param start Appointment's start time in LocalDateTime
     * @param end Appointment's end time in LocalDateTime
     * @param customerID Appointment's customer ID
     * @param userID Appointment's user ID
     * @param contactID Appointment's contact ID
     */
    public Appointment(int appointmentID, String title, String description, String location, String contactName, String type,
                       LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
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

    /**
     * Constructor for Appointment object with String start and end values.
     * @param appointmentID Appointment's Id
     * @param title Appointment's Title
     * @param description Appointment's Description
     * @param location Appointment's Location
     * @param contactName Appointment's Contact Name
     * @param type Appointment's Type
     * @param startString Appointment's start time in String
     * @param endString Appointment's end time in String
     * @param customerID Appointment's customer ID
     * @param userID Appointment's user ID
     * @param contactID Appointment's contact ID
     */
    public Appointment(int appointmentID, String title, String description, String location, String contactName, String type,
                       String startString, String endString, int customerID, int userID, int contactID) {
        setAppointmentID(appointmentID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contactName);
        setType(type);
        setStartString(startString);
        setEndString(endString);
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

    public LocalDateTime getStartDate() {
        return start;
    }

    public LocalDateTime getEndDate() {
        return end;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getUserID() {
        return userID;
    }

    public int getContactID() { return contactID; }

    public String getStartString() { return startString; }

    public String getEndString() { return endString; }

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

    public void setStart(LocalDateTime start) { this.start = start; }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setContactID(int contactID) { this.contactID = contactID;}

    public void setStartString(String startString) { this.startString = startString; }

    public void setEndString(String endString) { this.endString = endString; }
}
