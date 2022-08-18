package DAO;


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

    public static String addCustomerQuery(){
        return " INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                "values (?, ?, ?, ?, ?)";
    }

    public static String updateCustomerQuery(){
        return "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";
    }

    public static String getContactsQuery() { return "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?"; }

    public static String deleteCustomer() { return "DELETE FROM customers WHERE Customer_ID = ?";}
}
