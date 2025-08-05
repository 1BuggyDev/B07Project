package com.example.b07_project21.ui.support;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_project21.databinding.FragmentSupportBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SupportFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SupportViewModel viewModel;
    private FragmentSupportBinding binding;
    private Spinner citySelection;
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SupportViewModel.class);
        binding = FragmentSupportBinding.inflate(inflater, container, false);
        viewModel.initializeCityData(readJson());
        citySelection = binding.supportSpinner;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, viewModel.getCityData().keySet().toArray(new String[0]));

        // City selection spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySelection.setAdapter(adapter);
        citySelection.setOnItemSelectedListener(this);
        setCity("Toronto");

        viewModel.updateCity();
        viewModel.getCity().observe(getViewLifecycleOwner(), newCity -> setCity(newCity));

        return binding.getRoot();
    }

    /** Reads support.json in the assets folder. */
    private String readJson() {
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
        return jsonBuilder.toString();
    }

    /** Sets the spinner to the user's current city from Firebase. */
    private void setCity(String city) {
//        viewModel.updateCity();
//        String city = viewModel.getCity().toString();
        if (adapter.getPosition(city) == -1)
            citySelection.setSelection(adapter.getPosition("Toronto"));
        else
            citySelection.setSelection(adapter.getPosition(city));
        loadText(city);
    }

    /** Loads the support resources of the specified city using TextViews. */
    private void loadText(String city) {
        HashMap<String, HashMap<String, String>[]> cityMap = viewModel.getCityData();
        HashMap<String, String>[] resources = cityMap.get(city);
        LinearLayout layout = binding.supportLinear;
        if (resources == null) {
            Log.e("Support", "Something went wrong. Invalid city. (loadText)");
            return;
        }
        layout.removeAllViews();
        for (HashMap<String, String> resource : resources) {
            // Get info from hashmap
            TextView newView = new TextView(requireContext());
            String title = resource.get("title");
            String info = resource.get("info");
            if (resource.get("info2") != null) info += "\n" + resource.get("info2");
            if (title == null || info == null) continue; // something went wrong

            // For hyperlinks/phone numbers
            newView.setAutoLinkMask(Linkify.ALL);
            newView.setLinksClickable(true);

            // Text formatting
            SpannableString text = new SpannableString(title + "\n" + info + "\n");
            text.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text.setSpan(new AbsoluteSizeSpan(16, true), title.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            newView.setText(text);
            newView.setLineSpacing(0f, 1.4f); // not dp
            layout.addView(newView);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        loadText(parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
