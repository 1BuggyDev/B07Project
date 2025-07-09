package com.example.b07_project21.ui.safety_plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.b07_project21.databinding.FragmentSafetyBinding;

public class SafetyFragment extends Fragment {

    private FragmentSafetyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SafetyViewModel safetyViewModel =
                new ViewModelProvider(this).get(SafetyViewModel.class);

        binding = FragmentSafetyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSafetyPlan;
        safetyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
