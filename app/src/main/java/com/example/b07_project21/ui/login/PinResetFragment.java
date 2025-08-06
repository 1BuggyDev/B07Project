package com.example.b07_project21.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.b07_project21.databinding.FragmentPinResetBinding;

import dataAccess.PinManager;

public class PinResetFragment extends Fragment {
    private FragmentPinResetBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPinResetBinding.inflate(LayoutInflater.from(getContext()));
        createButtonListener();
        return binding.getRoot();
    }

    private void createButtonListener() {
        Button button = binding.PinResetButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PinManager.removePin(getContext());
                String msg = "You can now create a new pin after logging in with email and password";
                binding.PinResetMessage.setText(msg);
            }
        });
    }
}
