package com.example.b07_project21.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SupportViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SupportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is support fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}