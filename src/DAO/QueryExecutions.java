package DAO;

import java.sql.PreparedStatement;

public class QueryExecutions {

    //Sql SELECT query for the customers table
    public static String getSelectCustomerQuery(){
        return "SELECT * FROM customers";
    }

    //Sql SELECT query for the appointments table
    public static String getSelectAppointmentQuery(){
        return "SELECT * FROM appointments";
    }

    public static String getCountriesQuery(){
        return "SELECT Country FROM countries";
    }

    public static String getCountriesIDQuery(){
        return "SELECT Country_ID FROM countries WHERE Country = ?";
    }

    public static String getStatesQuery(int countryID){
        return "SELECT Division FROM first_level_divisions WHERE Country_ID = " + countryID;
    }

    public static String addCustomerQuery(){
        return " insert into customers (Customer_Name, Address, Postal_Code, Phone, Division_ID)" +
                "values (?, ?, ?, ?, ?)";
    }
}
