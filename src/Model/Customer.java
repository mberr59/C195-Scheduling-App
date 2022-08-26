package Model;

/**
 * This class holds the constructor for the Customer objects with all the necessary getters and setters.
 */
public class Customer {

    private int id;
    private String name;
    private String address;
    private String postal;
    private String phone;
    private String divisionName;

    /**
     * Constructor for Customer object.
     * @param id Customer ID
     * @param name Customer Name
     * @param address Customer Address
     * @param postal Customer postal code
     * @param phone Customer phone number
     * @param divisionName Customer First-level division name
     */
    public Customer (int id, String name, String address, String postal, String phone, String divisionName){
        setId(id);
        setName(name);
        setAddress(address);
        setPostal(postal);
        setPhone(phone);
        setDivisionName(divisionName);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostal() {
        return postal;
    }

    public String getPhone() {
        return phone;
    }

    public String getDivisionName() { return divisionName; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
}
