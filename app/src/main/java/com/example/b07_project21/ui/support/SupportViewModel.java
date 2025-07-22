package com.example.b07_project21.ui.support;

import android.content.res.AssetManager;

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

    private String city;
    private HashMap<String, HashMap<String, String>[]> cityData;

    public SupportViewModel() {
        city = "Vancouver"; // temp
//        CityReader cityReader = new CityReader(this);
//        DatabaseAccess.readData(infoType.ANSWER, cityReader);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public HashMap<String, HashMap<String, String>[]> getCityData() {
        return cityData;
    }

    public void initializeCityData(String json) {
        // takes in contents of support.json and initializes cityData
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, HashMap<String, String>[]>>(){}.getType();
        cityData = gson.fromJson(json, type);
    }
}