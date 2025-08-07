package com.example.b07_project21.ui.emergency_info;

import android.util.Log;

import java.util.ArrayList;

import dataAccess.FileData;
import dataAccess.FileListener;
import dataAccess.infoType;

public class EmergencyFileManager implements FileListener {
    private final EmergencyFragment fragment;
    private final ArrayList<FileData> fileDataArray;
    private String currentFile;
    private String readMode;

    public EmergencyFileManager(EmergencyFragment fragment) {
        this.fragment = fragment;
        fileDataArray = new ArrayList<>();
    }

    public void resetArray() {
        fileDataArray.clear();
    }

    public void setCurrentFile(String file) {
        currentFile = file;
    }

    public void setReadMode(String mode) {
        readMode = mode;
    }

    public ArrayList<FileData> getFileDataArray() {
        return fileDataArray;
    }

    @Override
    public void onFilesReceived(infoType type, ArrayList<FileData> data) {
        if (data != null && type == infoType.DOCUMENT) {
            for (FileData fileData : data) {
                if (fileData.getName().equals(currentFile + "." + fileData.getExtension())) {
                    if (readMode.equals("open")) EmergencyFileHelper.openFile(fragment.getContext(), fileData);
                    else if (readMode.equals("download")) EmergencyFileHelper.downloadFile(fragment.getContext(), fileData);
                    else Log.e("EmergencyFileManager", "Invalid read mode: " + readMode);
                    break;
                }
            }
        }
    }

//    @Override
//    public void onFileWritten(boolean wasSuccessful) {
//        if (wasSuccessful) {
//
//        }
//    }
//
//    @Override
//    public void onFileDeleted(boolean wasSuccessful) {
//        if (wasSuccessful) {
//
//        }
//    }
}