package com.example.b07_project21.ui.emergency_info;

import android.provider.DocumentsProvider;

import java.util.ArrayList;

import dataAccess.FileListener;
import dataAccess.infoType;

public class EmergencyFileManager implements FileListener {
    EmergencyFragment fragment;

    public EmergencyFileManager(EmergencyFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onFilesReceived(infoType type, ArrayList<byte[]> data) {
        if (data != null) {

        }
    }

    @Override
    public void onFileWritten(boolean wasSuccessful) {
        if (wasSuccessful) {

        }
    }

    @Override
    public void onFileDeleted(boolean wasSuccessful) {
        if (wasSuccessful) {

        }
    }
}
