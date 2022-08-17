package Model;

public class Customer {

    private int id;
    private String name;
    private String address;
    private String postal;
    private String phone;
    private String divisionName;

    public Customer (int id, String name, String address, String postal, String phone, String divisionName){
        this.id = id;
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.divisionName = divisionName;
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
