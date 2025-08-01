package com.example.b07_project21.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       String welcomeText =
                "Welcome to the Safety Planning App.\n\n" +
                        "This tool is built on Victim Services Toronto’s framework. " +
                        "Use it to assess risk, build a flexible safety plan, and connect with support.\n\n" +
                        "How to use:\n" +
                        "• Left menu:\n" +
                        "    – Safety Plan: create or update your personal plan.\n" +
                        "    – Questionnaire: answer guided questions for tailored tips.\n" +
                        "    – Emergency Info: save safe locations, contacts, documents.\n" +
                        "    – Support Connection: find local hotlines, shelters, legal aid.\n" +
                        "    – Notifications: set reminders to review your plan.\n" +
                        "• Exit App (bottom): tap any time for a quick, discreet exit.\n\n" +
                        "All data stays private on your device.";

        binding.textHome.setText(welcomeText);
        // Enable scrolling of links (if you ever add any URL spans)
        binding.textHome.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}