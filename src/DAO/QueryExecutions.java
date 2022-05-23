package DAO;

public class QueryExecutions {

    //Sql SELECT query for the customers table
    public static String getSelectCustomerQuery(){
        return "SELECT * FROM customers";
    }

    //Sql SELECT query for the appointments table
    public static String getSelectAppointmentQuery(){
        return "SELECT * FROM appointments";
    }
}
