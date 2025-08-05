package com.example.b07_project21.ui.login;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentPasswordResetBinding;
import com.google.firebase.auth.FirebaseAuth;

import dataAccess.PinManager;

public class PasswordResetFragment extends Fragment {
    private FragmentPasswordResetBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("Reset", "fragment run");
        binding = FragmentPasswordResetBinding.inflate(LayoutInflater.from(getContext()));

        createButtonListener();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Reset", "onViewCreated");

        requireActivity().getOnBackPressedDispatcher().addCallback(
            getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Log.d("Reset", "Func call");
                    NavController navController = NavHostFragment.findNavController(PasswordResetFragment.this);
                    navController.popBackStack(R.id.email_login_menu, false);
                }
            });
    }

    private void createButtonListener() {
        Button button = binding.ResetButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = binding.ResetEmailAddress.getText().toString();
        if(email.isEmpty()) {
            String msg = "Enter your email above to reset your password";
            binding.ResetMessage.setText(msg);
            binding.ResetMessage.setTextColor(Color.rgb(255, 0, 0));
            return;
        }
        String msg = "Sent password reset email to the email above.\nIf you don't see an email, please check your spam inbox.";
        binding.ResetMessage.setText(msg);
        binding.ResetMessage.setTextColor(Color.rgb(0, 0, 0));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        auth.sendPasswordResetEmail(email);
        //NavController navController = NavHostFragment.findNavController(this);
        //navController.popBackStack(R.id.email_login_menu, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
