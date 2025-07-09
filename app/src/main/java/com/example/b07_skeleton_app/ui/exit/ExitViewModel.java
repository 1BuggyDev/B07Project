package com.example.b07_skeleton_app.ui.exit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExitViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExitViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is exit fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}