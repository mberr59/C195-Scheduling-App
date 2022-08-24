package Helper;


public abstract class QueryExecutions {

    //Sql SELECT query for the customers table
    public static String getSelectCustomerQuery(){ return "SELECT * FROM customers"; }

    //Sql SELECT query for the appointments table
    public static String getSelectAppointmentQuery(){ return "SELECT * FROM appointments"; }

    public static String getAppointmentByCustomer() { return "SELECT * FROM appointments WHERE Customer_ID = ?"; }

    public static String getCountriesQuery() { return "SELECT Country FROM countries"; }

    public static String getCountriesIDQuery(){ return "SELECT Country_ID FROM countries WHERE Country = ?"; }

    public static String getCountriesName() { return "SELECT Country FROM countries WHERE Country_ID = ?"; }

    public static String getFLDCountriesID() { return "SELECT Country_ID FROM first_level_divisions WHERE Division = ?"; }

    public static String getStatesQuery(){ return "SELECT Division FROM first_level_divisions WHERE Country_ID = ?"; }

    public static String getDivisionID() {
        return "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
    }

    public static String getDivisionName() { return "SELECT Division FROM first_level_divisions WHERE Division_ID = ?"; }

    public static String getContactsName() { return "SELECT Contact_Name FROM contacts"; }

    public static String getContactID() { return "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?"; }

    public static String addCustomerQuery() {
        return " INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                "values (?, ?, ?, ?, ?)";
    }

    public static String updateCustomerQuery() {
        return "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";
    }

    public static String getContactsQuery() { return "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?"; }

    public static String deleteCustomer() { return "DELETE FROM customers WHERE Customer_ID = ?";}

    public static String addAppointmentQuery() {
        return " INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static String getAppTimestamp() { return "SELECT Start, End FROM appointments WHERE Appointment_ID = ?"; }

    public static String getAppByUser() { return "SELECT * FROM appointments WHERE User_ID = ?"; }

    public static String getUserID() { return "SELECT User_ID FROM users WHERE User_Name = ?"; }

    public static String getAppTimestampCustomer() { return "SELECT Start, End FROM appointments WHERE Customer_ID = ?"; }

    public static String updateAppointmentQuery() {
        return "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    }

    public static String deleteAppointment() { return "DELETE FROM appointments WHERE Appointment_ID = ?"; }

    public static String getUsernames() { return "SELECT User_Name FROM users"; }

    public static String getPassword() { return "SELECT Password FROM users WHERE User_Name = ?"; }
}
