package dataAccess;


public class EmergencyContact extends UserData {
    private String contactName;
    private String relationship;
    private String phoneNumber;

    public EmergencyContact() {}
    public EmergencyContact(String title, String description, String contactName, String relationship, String phoneNumber) {
        super(title, description);
        this.contactName = contactName;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
    }

    public EmergencyContact(String title, String description, String filePath, String contactName, String relationship, String phoneNumber) {
        super(title, description, filePath);
        this.contactName = contactName;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
