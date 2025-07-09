package com.example.b07_project21.ui.safety_plan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SafetyViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SafetyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is safety plan fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}