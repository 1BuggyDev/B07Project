package com.example.b07_project21.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import dataAccess.DatabaseAccess;
import dataAccess.infoType;

public class SupportViewModel extends ViewModel {

    private final MutableLiveData<String> city;
    private HashMap<String, HashMap<String, String>[]> cityData;
    private SupportReader cityReader;

    public SupportViewModel() {
        city = new MutableLiveData<>();
        cityReader = new SupportReader(this);
    }

    /** Updates the user's city from Firebase. */
    public void updateCity() {
        DatabaseAccess.readData(infoType.ANSWER, cityReader);
    }

    public void setCity(String city) {
        this.city.setValue(city);
    }

    public LiveData<String> getCity() {
        return city;
    }

    public HashMap<String, HashMap<String, String>[]> getCityData() {
        return cityData;
    }

    /** Initializes cityData from the contents of support.json. */
    public void initializeCityData(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, HashMap<String, String>[]>>(){}.getType();
        cityData = gson.fromJson(json, type);
    }
}