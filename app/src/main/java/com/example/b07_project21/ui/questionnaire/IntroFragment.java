package com.example.b07_project21.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.MainActivity;
import com.example.b07_project21.R;

public class IntroFragment extends Fragment {
    private LinearLayout leftButton, rightButton;
    public static boolean questionnaireCompleted;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_questionnaire_intro, container, false);

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(IntroFragment.this);

        leftButton.setOnClickListener(v -> {
            if (IntroFragment.questionnaireCompleted) {
                //Toast.makeText(getContext(), "Back to Homepage", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.nav_home);
            } else {
                Toast.makeText(getContext(), "Please complete the Questionnaire", Toast.LENGTH_SHORT).show();
            }
        });

        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Warm", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_warm);
        });

        return root;
    }

    // update when api code is there
    private static void setQuestionnaireValue() {
        // code for using the api
        IntroFragment.questionnaireCompleted = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Null out view references for safety
        leftButton = null;
        rightButton = null;
    }
}