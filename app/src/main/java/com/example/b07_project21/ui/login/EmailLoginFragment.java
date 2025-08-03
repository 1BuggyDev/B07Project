package com.example.b07_project21.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentEmailLoginBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import dataAccess.AccountListener;
import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.LoginManager;
import dataAccess.PinManager;
import dataAccess.infoType;


public class EmailLoginFragment extends Fragment implements AccountListener, DataListener {
    FragmentEmailLoginBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmailLoginBinding.inflate(LayoutInflater.from(getContext()));
        createButtonListener();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void createButtonListener() {
        Button loginButton = binding.SubmitEmailPasswordButton;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = binding.LoginEmailAddress.getText().toString();
        String password = binding.LoginPassword.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            String msg = "Please make sure to fill in both the email and password fields.";
            binding.EmailLoginFailureMessage.setText(msg);
            return;
        }
        LoginManager.attemptLogin(email, password, this);
    }

    @Override
    public void onEmailLogin(FirebaseUser user) {
        if(user == null) {
            String msg = "Login failed.\nPlease verify your email address and password is correct.";
            binding.EmailLoginFailureMessage.setText(msg);
        } else  {
            if(PinManager.pinExists(getContext())) {
                DatabaseAccess.readData(infoType.ANSWER, this);
            } else {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_to_pin_creation);
            }
        }
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
