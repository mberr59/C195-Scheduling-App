package DAO;

public class QueryExecutions {

    //Sql SELECT query for the customers table
    public static String getSelectQuery(){
        String selectQuery = "SELECT * FROM customers";
        return selectQuery;
    }
}
