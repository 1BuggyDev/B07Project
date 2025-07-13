package com.example.b07_project21.ui.support;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.util.Linkify;
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

    private SupportViewModel supportViewModel;
    private FragmentSupportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        supportViewModel = new ViewModelProvider(this).get(SupportViewModel.class);
        binding = FragmentSupportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        supportViewModel.initializeCityData(readJson());

        // city selection spinner
        Spinner citySelection = binding.supportSpinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, supportViewModel.getCityData().keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySelection.setAdapter(adapter);
        citySelection.setOnItemSelectedListener(this);

        String city = "Edmonton"; // placeholder, firebase later
        if (adapter.getPosition(city) == -1) city = "Toronto";
        citySelection.setSelection(adapter.getPosition(city));
        loadText(city);

        return root;
    }

    private String readJson() {
        // reads support.json in the assets folder
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

    private void loadText(String city) {
        // loads the support info of the specified city using TextViews
        HashMap<String, HashMap<String, String>[]> cityMap = supportViewModel.getCityData();
        HashMap<String, String>[] resources = cityMap.get(city);
        LinearLayout layout = binding.supportLinear;
        if (resources == null) {
            System.err.println("Something went wrong. Invalid city. (loadText)");
            return;
        }
        layout.removeAllViews();
        for (HashMap<String, String> resource : resources) {
            TextView newView = new TextView(requireContext());
            String title = resource.get("title");
            String info = resource.get("info");
            if (resource.get("info2") != null) info += "\n" + resource.get("info2");
            if (title == null || info == null) continue; // something went wrong

            // for hyperlinks/phone numbers
            newView.setAutoLinkMask(Linkify.ALL);
            newView.setLinksClickable(true);

            // text formatting
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
