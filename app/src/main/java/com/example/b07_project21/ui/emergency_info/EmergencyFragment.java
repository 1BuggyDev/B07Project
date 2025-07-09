package com.example.b07_project21.ui.emergency_info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_project21.databinding.FragmentEmergencyBinding;

public class EmergencyFragment extends Fragment {

    private FragmentEmergencyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EmergencyViewModel emergencyViewModel =
                new ViewModelProvider(this).get(EmergencyViewModel.class);

        binding = FragmentEmergencyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEmergency;
        emergencyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}