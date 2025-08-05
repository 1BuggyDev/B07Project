package dataAccess;


public class EmergencyContact extends UserData {
    private String contactName;
    private String relationship;
    private String contactMethod;

    public EmergencyContact() {}
    public EmergencyContact(String title, String description, String contactName, String relationship, String contactMethod) {
        super(title, description);
        this.contactName = contactName;
        this.relationship = relationship;
        this.contactMethod = contactMethod;
    }

    public EmergencyContact(String title, String description, String filePath, String contactName, String relationship, String contactMethod) {
        super(title, description, filePath);
        this.contactName = contactName;
        this.relationship = relationship;
        this.contactMethod = contactMethod;
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

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String phoneNumber) {
        this.contactMethod = phoneNumber;
    }

}
