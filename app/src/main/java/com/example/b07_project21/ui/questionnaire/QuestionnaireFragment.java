package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_project21.databinding.FragmentQuestionnaireBinding;

public class QuestionnaireFragment extends Fragment {

    private FragmentQuestionnaireBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QuestionnaireViewModel questionnaireViewModel =
                new ViewModelProvider(this).get(QuestionnaireViewModel.class);

        binding = FragmentQuestionnaireBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textQuestionnaire;
        questionnaireViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Setup button click listeners
        setupButtonListeners();

        return root;
    }

    /* Added for Buttons */
    private void setupButtonListeners() {
        // Left button (Back) click listener
        LinearLayout leftButton = binding.bottomNavButtonLeft;
        if (leftButton != null) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftButtonClick();
                }
            });
        }

        // Right button (Next) click listener
        LinearLayout rightButton = binding.bottomNavButtonRight;
        if (rightButton != null) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightButtonClick();
                }
            });
        }
    }

    private void onLeftButtonClick() {
        // Handle left button (Back) click
        // Example: Navigate to previous question or go back

        // Option 1: Navigate back using NavController
        // NavController navController = Navigation.findNavController(requireView());
        // navController.navigateUp();

        // Option 2: Custom back logic for questionnaire
        // questionnaireViewModel.goToPreviousQuestion();

        // Show toast message for now
        Toast.makeText(getContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
    }

    private void onRightButtonClick() {
        // Handle right button (Next) click
        // Example: Navigate to next question or submit

        // Option 1: Navigate to next fragment
        // NavController navController = Navigation.findNavController(requireView());
        // navController.navigate(R.id.action_questionnaire_to_next_fragment);

        // Option 2: Custom next logic for questionnaire
        // questionnaireViewModel.goToNextQuestion();

        // Show toast message for now
        Toast.makeText(getContext(), "Next button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}