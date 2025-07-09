package com.example.b07_skeleton_app.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_skeleton_app.databinding.FragmentQuestionnaireBinding;

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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}