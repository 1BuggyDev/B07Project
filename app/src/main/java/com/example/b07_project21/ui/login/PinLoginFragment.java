package com.example.b07_project21.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentPinLoginBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.LoginManager;
import dataAccess.PinManager;
import dataAccess.infoType;

public class PinLoginFragment extends Fragment implements DataListener {
    private FragmentPinLoginBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPinLoginBinding.inflate(LayoutInflater.from(getContext()));
        createButtonListeners();

        if(!PinManager.pinExists(getContext())) {
            //head back to enter menu, must have reset pin
            //change history to enter_menu -> email login
            NavController navController = NavHostFragment.findNavController(this);
            navController.clearBackStack(R.id.enter_menu);
            navController.navigate(R.id.enter_menu);
            navController.navigate(R.id.email_login_menu);
        }

        return binding.getRoot();
    }

    private void createButtonListeners() {
        Button submitPinButton = binding.SubmitPinButton;
        submitPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Fragment obj = this;
        Button resetPinButton = binding.ResetPinButton;
        resetPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(obj);
                navController.navigate(R.id.action_to_pin_reset);
            }
        });
    }

    private void attemptLogin() {
        String pin = binding.PinEnter.getText().toString();
        FirebaseUser user = null;
        try {
            user = LoginManager.attemptLogin(pin, getContext());
        } catch(Exception ignored) {}
        if(user == null) {
            //login failed
            String msg = "Incorrect Pin";
            binding.PinLoginFailureMessage.setText(msg);
        } else {
            String msg = "";
            binding.PinLoginFailureMessage.setText(msg);
            DatabaseAccess.readData(infoType.ANSWER, this);
        }
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
