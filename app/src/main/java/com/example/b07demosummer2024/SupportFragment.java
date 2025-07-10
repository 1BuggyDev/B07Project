package com.example.b07demosummer2024;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.lang.reflect.Type;

import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.database.FirebaseDatabase;

public class SupportFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private View rootView;
    private FirebaseDatabase db;
    private HashMap<String, HashMap<String, String>> jsonContents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_support, container, false);
        this.jsonContents = readJson();

        // city selection spinner
        Spinner citySelection = rootView.findViewById(R.id.supportSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, jsonContents.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySelection.setAdapter(adapter);
        citySelection.setOnItemSelectedListener(this);

        // firebase (set default city)
//        db = FirebaseDatabase.getInstance("");
        String city = "invalid city"; // placeholder, firebase later
        if (adapter.getPosition(city) == -1) city = "Toronto";
        citySelection.setSelection(adapter.getPosition(city));
        loadInfo(city);

        return rootView;
    }
    private void loadInfo(String city) {
        HashMap<String, String> cityMap = jsonContents.get(city);
        LinearLayout layout = rootView.findViewById(R.id.supportLinear);
        layout.removeAllViews();
        for (String resource : cityMap.keySet()) { // to do (maybe): make this ordered
            TextView newView = new TextView(requireContext());

            // text formatting
            SpannableString item = new SpannableString(resource + "\n" + cityMap.get(resource) + "\n");
            item.setSpan(new AbsoluteSizeSpan(50), 0, resource.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setSpan(new AbsoluteSizeSpan(40), resource.length(), item.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            newView.setText(item);
            layout.addView(newView);
        }
    }

    private HashMap<String, HashMap<String, String>> readJson() {
        StringBuilder jsonBuilder = new StringBuilder();
        AssetManager assetManager = requireContext().getAssets();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("support.json")));
            String line = reader.readLine();
            while (line != null) {
                jsonBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to read support.json");
        }
        String json = jsonBuilder.toString();

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, HashMap<String, String>>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void fetchCityFromDatabase() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        loadInfo(parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
