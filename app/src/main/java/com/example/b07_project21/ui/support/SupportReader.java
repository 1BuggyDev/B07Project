package com.example.b07_project21.ui.support;

import java.util.HashMap;

import dataAccess.DataListener;
import dataAccess.infoType;

public class SupportReader implements DataListener {
    private final SupportViewModel viewModel;

    public SupportReader(SupportViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onDataReceived(infoType type, Object data) {
        if (data != null && type == infoType.ANSWER) {
            HashMap<String,String> questionData = (HashMap<String,String>) data;
            viewModel.setCity(questionData.get("What city do you live in?"));
        }
    }
}
