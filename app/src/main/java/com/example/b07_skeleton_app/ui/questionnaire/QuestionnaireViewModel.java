package com.example.b07_skeleton_app.ui.questionnaire;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuestionnaireViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QuestionnaireViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is questionnaire fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}