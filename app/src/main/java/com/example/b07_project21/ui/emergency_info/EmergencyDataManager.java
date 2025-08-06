package com.example.b07_project21.ui.emergency_info;

import java.util.ArrayList;

import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.Document;
import dataAccess.EmergencyContact;
import dataAccess.Medication;
import dataAccess.SafeLocation;
import dataAccess.StorageAccess;
import dataAccess.infoType;

public class EmergencyDataManager implements DataListener {
    private final EmergencyFragment fragment;

    private ArrayList<Document> documents;
    private ArrayList<EmergencyContact> contacts;
    private ArrayList<SafeLocation> locations;
    private ArrayList<Medication> medications;

    public EmergencyDataManager(EmergencyFragment fragment) {
        this.fragment = fragment;
    }

    public ArrayList<?> getData(infoType type) {
        switch (type) {
            case CONTACT:
                return getContacts();
            case LOCATION:
                return getLocations();
            case MEDICATION:
                return getMedications();
            default:
                return getDocuments();
        }
    }

    public ArrayList<Document> getDocuments() {
        return (documents == null) ? new ArrayList<>() : documents;
    }

    public ArrayList<EmergencyContact> getContacts() {
        return (contacts == null) ? new ArrayList<>() : contacts;
    }

    public ArrayList<SafeLocation> getLocations() {
        return (locations == null) ? new ArrayList<>() : locations;
    }

    public ArrayList<Medication> getMedications() {
        return (medications == null) ? new ArrayList<>() : medications;
    }

    public void updateData(infoType type, Object data) {
        if (data == null) return;
        switch (type) {
            case DOCUMENT:
                documents = (ArrayList<Document>) data;
                fragment.updateList(infoType.DOCUMENT);
                break;
            case CONTACT:
                contacts = (ArrayList<EmergencyContact>) data;
                fragment.updateList(infoType.CONTACT);
                break;
            case LOCATION:
                locations = (ArrayList<SafeLocation>) data;
                fragment.updateList(infoType.LOCATION);
                break;
            case MEDICATION:
                medications = (ArrayList<Medication>) data;
                fragment.updateList(infoType.MEDICATION);
                break;
        }
    }

    @Override
    public void onDataReceived(infoType type, Object data) {
        updateData(type, data);
    }

    @Override
    public void onDataWritten(infoType type, Object data) {
        updateData(type, data);
    }

    @Override
    public void onDataDeleted(infoType type, Object data) {
        updateData(type, data);
    }
}
