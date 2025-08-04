package com.example.b07_project21.ui.enter_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentEnterMenuBinding;
import com.example.b07_project21.ui.login.EmailLoginFragment;
import com.example.b07_project21.ui.login.FirebaseSignupFragment;
import com.example.b07_project21.ui.login.PinLoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dataAccess.LoginManager;
import dataAccess.PinManager;

public class EnterScreenFragment extends Fragment {
    private FragmentEnterMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("Login", "EnterScreenFragment.java onCreateView ran");

        binding = FragmentEnterMenuBinding.inflate(LayoutInflater.from(getContext()));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            binding.SignupButton.setVisibility(View.GONE);
            if(!PinManager.pinExists(getContext())) {
                binding.PinLoginButton.setVisibility(View.GONE);
            } else {
                binding.PinLoginButton.setVisibility(View.VISIBLE);
            }
        } else {
            binding.SignupButton.setVisibility(View.VISIBLE);
            binding.PinLoginButton.setVisibility(View.GONE);
        }

        createButtonListeners();

        return binding.getRoot();
    }

    /**
     * Creating listeners for login/signup buttons
     */
    private void createButtonListeners() {
        Button signupButton = binding.SignupButton;
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSignupScreen();
            }
        });

        Button pinLoginButton = binding.PinLoginButton;
        pinLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterPinLoginScreen();
            }
        });

        Button emailLoginButton = binding.EmailLoginButton;
        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterEmailLoginScreen();
            }
        });
    }

    private void switchFragment(int id) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(id);
    }

    private void enterSignupScreen() {
        Log.d("Login", "Signup press detected");
        switchFragment(R.id.action_signup);
    }

    private void enterPinLoginScreen() {
        Log.d("Login", "Pin login press detected");
        switchFragment(R.id.action_pin_login);
    }

    private void enterEmailLoginScreen() {
        Log.d("Login", "Email login press detected");
        switchFragment(R.id.action_email_login);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
