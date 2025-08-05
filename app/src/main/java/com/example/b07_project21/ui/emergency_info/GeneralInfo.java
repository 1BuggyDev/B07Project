package com.example.b07_project21.ui.emergency_info;

import androidx.annotation.Nullable;

import dataAccess.Document;
import dataAccess.EmergencyContact;
import dataAccess.Medication;
import dataAccess.SafeLocation;
import dataAccess.infoType;

public class GeneralInfo {
    private String first;
    private String second;
    private String third;

    public GeneralInfo(String first, @Nullable String second, @Nullable String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public GeneralInfo(String first, @Nullable String second) {
        this(first, second, null);
    }

    public GeneralInfo(String first) {
        this(first, null, null);
    }

    /** Return the given object cast into a GeneralInfo */
    public static GeneralInfo castFrom(infoType type, Object obj) {
        switch (type) {
            case CONTACT:
                EmergencyContact contact = (EmergencyContact) obj;
                return new GeneralInfo(contact.getContactName(), contact.getRelationship(), contact.getContactMethod());
            case LOCATION:
                SafeLocation location = (SafeLocation) obj;
                return new GeneralInfo(location.getAddress(), null, null);
            case MEDICATION:
                Medication medication = (Medication) obj;
                return new GeneralInfo(medication.getName(), medication.getAmount(), String.valueOf(medication.getDailyFrequency()));
            default:
                Document document = (Document) obj;
                return new GeneralInfo(document.getTitle(), null, null);
//                return new GeneralInfo(document.getTitle(), null, document.getFilePath());
        }
    }

    /** Return this object cast into the specified infoType */
    public Object castTo(infoType type) {
        switch (type) {
            case DOCUMENT:
                return new Document();
            case CONTACT:
                EmergencyContact contact = new EmergencyContact();
                contact.setContactName(first);
                contact.setRelationship(second);
                contact.setContactMethod(third);
                return contact;
            case LOCATION:
                SafeLocation location = new SafeLocation();
                location.setAddress(first);
                return location;
            case MEDICATION:
                Medication medication = new Medication();
                medication.setName(first);
                medication.setAmount(second);
                medication.setDailyFrequency(Integer.parseInt(third)); // NOTE: check valid
                return medication;
            default:
                return null;
        }
    }

    /** Check if contents of GeneralInfo are equal to contents of the given object */
    public boolean isEqualTo(infoType type, Object obj) {
        switch (type) {
            case CONTACT:
                EmergencyContact contact = (EmergencyContact) obj;
                return first.equals(contact.getContactName()) && second.equals(contact.getRelationship()) && third.equals(contact.getContactMethod());
            case LOCATION:
                SafeLocation location = (SafeLocation) obj;
                return first.equals(location.getAddress());
            case MEDICATION:
                Medication medication = (Medication) obj;
                return first.equals(medication.getName()) && second.equals(medication.getAmount()) && third.equals(String.valueOf(medication.getDailyFrequency()));
            default: return false;
        }
    }

    public String first() {
        return first;
    }

    public String second() {
        return second;
    }

    public String third() {
        return third;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public void setThird(String third) {
        this.third = third;
    }
}
