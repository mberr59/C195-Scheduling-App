package Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is the Database Connection class. It is used to store all the String variables used to create the URL for
 * the Database Connection. Also stores the username and password for database connections.
 */
public class DBConnection {

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddr = "//localhost:3306/";
    private static final String dbName = "client_schedule";

    //Builds the URL for the database connection
    private static final String jdbcURL = protocol + vendorName + ipAddr + dbName;

    private static final String mySQLJBDCDriver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    private static Connection conn = null;


    /**
     * Attempts to start the connection to the database. Throws error if the driver
     * is not found or if there is a connection issue.
     */
    public static Connection startConn(){
        try {
            Class.forName(mySQLJBDCDriver);
            conn = DriverManager.getConnection(jdbcURL, userName, password);

            System.out.println("Connection to Database Successful.");
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (ClassNotFoundException c){
            c.printStackTrace();
        }
        return conn;
    }

    /**
     * Getter method for the open connection.
     * @return Returns the currently open connection object.
     */
    public static Connection getConn(){
        return conn;
    }

    /**
     * Closes the database connection.
     */
    public static void endConn(){
        try{
            conn.close();
        } catch (Exception e){
            // do nothing
        }
    }

}
