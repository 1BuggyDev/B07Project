package com.example.b07_project21.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentPinCreationBinding;

import java.util.HashMap;

import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.PinManager;
import dataAccess.infoType;

public class PinCreationFragment extends Fragment implements DataListener {
    private FragmentPinCreationBinding binding;
    private String curPin;
    private String reenter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPinCreationBinding.inflate(LayoutInflater.from(getContext()));
        createButtonListener();
        createInputListeners();
        curPin = "";
        reenter = "";
        return binding.getRoot();
    }

    private void createInputListeners() {
        binding.Pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                curPin = charSequence.toString();
                String msg = verifyPin(curPin);
                if(msg != null) {
                    binding.PinCreationError.setText(msg);
                    return;
                }
                if(!curPin.equals(reenter)) {
                    if(reenter.isEmpty()) {
                        msg = "";
                    } else {
                        msg = "Pins do not match";
                    }
                    binding.PinCreationError.setText(msg);
                    return;
                }
                msg = "";
                binding.PinCreationError.setText(msg);
            }
        });
        binding.reenter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reenter = charSequence.toString();
                String msg = verifyPin(curPin);
                if(msg != null) {
                    binding.PinCreationError.setText(msg);
                    return;
                }
                if(!curPin.equals(reenter)) {
                    if(reenter.isEmpty()) {
                        msg = "";
                    } else {
                        msg = "Pins do not match";
                    }
                    binding.PinCreationError.setText(msg);
                    return;
                }
                msg = "";
                binding.PinCreationError.setText(msg);
            }
        });
    }

    private void createButtonListener() {
        Button pinCreationButton = binding.createPinButton;
        pinCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String err = verifyPin(curPin);
                if(err != null) {
                    binding.PinCreationError.setText(err);
                    return;
                }
                if(!curPin.equals(reenter)) {
                    String msg = "Pins do not match";
                    binding.PinCreationError.setText(msg);
                    return;
                }
                setPin();
            }
        });
    }

    private String verifyPin(String pin) {
        if(pin.length() == 4 || pin.length() == 6) {
            return null;
        }

        return "Pin must be 4 or 6 digits long";
    }

    private void setPin() {
        PinManager.setPin(curPin, getContext());
        DatabaseAccess.readData(infoType.ANSWER, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDataReceived(infoType type, Object data) {
        NavController navController = NavHostFragment.findNavController(this);
        if(data == null) {
            navController.navigate(R.id.action_to_questionnaire);
        }

        HashMap<String, String> mapData = (HashMap<String, String>) data;
        if(mapData.containsKey("Questionnaire done?") && mapData.get("Questionnaire done?").equals("true")) {
            navController.navigate(R.id.action_to_home);
        } else {
            navController.navigate(R.id.action_to_questionnaire);
        }
    }
}
