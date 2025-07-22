package com.example.b07_project21.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

import dataAccess.DataListener;
import dataAccess.infoType;

public class CityReader implements DataListener {
    private final SupportViewModel viewModel;

    public CityReader(SupportViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onDataReceived(infoType type, Object data) {
        if (data != null) {
            HashMap<String, String> questionData = (HashMap<String, String>) data;
            viewModel.setCity(questionData.get("What city do you live in?"));
        }
    }
}
