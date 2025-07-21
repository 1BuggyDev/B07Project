package dataAccess;

public class SafeLocation extends UserData {
    private String address;
    public SafeLocation() {}
    public SafeLocation(String title, String description, String address) {
        super(title, description);
        this.address = address;
    }

    public SafeLocation(String title, String description, String filePath, String address) {
        super(title, description, filePath);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
