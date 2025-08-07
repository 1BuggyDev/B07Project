package com.example.b07_project21.ui.login;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.b07_project21.databinding.FragmentFirebaseSignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dataAccess.AccountListener;
import dataAccess.LoginManager;

public class FirebaseSignupFragment extends Fragment implements AccountListener {
    FragmentFirebaseSignupBinding binding;
    String curPassword;
    String reenter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //If account has been created, redirect to main
        //Only accessible when back button is pressed from pin creation menu
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.clearBackStack(R.id.enter_menu);
            navController.navigate(R.id.enter_menu);
        }

        curPassword = "";
        reenter = "";
        binding = FragmentFirebaseSignupBinding.inflate(LayoutInflater.from(getContext()));
        View root = binding.getRoot();
        createButtonListener();
        createInputListeners();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void createButtonListener() {
        Button signupButton = binding.SignupButton;
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });
    }

    private void createInputListeners() {
        binding.PasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                curPassword = charSequence.toString();
                String msg = isPasswordValid(curPassword);
                if(msg != null) {
                    binding.MissingRequirements.setText(msg);
                    return;
                }

                if(!curPassword.equals(reenter) && !reenter.isEmpty()) {
                    String message = "Passwords do not match";
                    binding.MissingRequirements.setText(message);
                    return;
                }
                msg = "";
                binding.MissingRequirements.setText(msg);
            }
        });


        binding.ReenterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reenter = charSequence.toString();
                if(isPasswordValid(curPassword) == null) {
                    if(!reenter.equals(curPassword)) {
                        if(reenter.isEmpty()) {
                            String message = "";
                            binding.MissingRequirements.setText(message);
                            return;
                        }
                        String message = "Passwords do not match";
                        binding.MissingRequirements.setText(message);
                        return;
                    }
                    String msg = isPasswordValid(curPassword);
                    if(msg == null) {
                        msg = "";
                    }
                    binding.MissingRequirements.setText(msg);
                }
            }
        });
    }

    @Override
    public void onEmailLogin(FirebaseUser user) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_to_pin_creation);
    }

    private void attemptSignup() {
        if(curPassword.equals(reenter) && isPasswordValid(curPassword) == null) {
            FirebaseSignupFragment obj = this;
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(binding.EmailAddress.getText().toString(), curPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Log.d("Signup", "success");
                        String msg = "";
                        Log.d("Signup", auth.getCurrentUser().getUid());
                        Log.d("Signup", auth.getUid());
                        binding.MissingRequirements.setText(msg);
                        LoginManager.attemptLogin(binding.EmailAddress.getText().toString(), curPassword, obj);
                    } else {
                        Log.d("Signup", "failure");
                        String msg = "Could not create account.\nPlease verify your email address is correct";
                        binding.MissingRequirements.setText(msg);
                    }
                }
            });
        }
    }

    private String isPasswordValid(String password) {
        if(password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;

        for(int i = 0; i < password.length(); i++) {
            int unicode = (int) password.charAt(i);
            if(unicode >= 65 && unicode <= 90) {
                hasUpper = true;
            } else if(unicode >= 97 && unicode <= 122) {
                hasLower = true;
            } else if(unicode >= 48 && unicode <= 57) {
                hasNumber = true;
            }
        }

        if(!(hasUpper)) {
            return "Password must contain an uppercase letter";
        }
        if(!(hasLower)) {
            return "Password must contain a lowercase letter";
        }
        if(!(hasNumber)) {
            return "Password must contain a number";
        }

        return null;
    }
}
