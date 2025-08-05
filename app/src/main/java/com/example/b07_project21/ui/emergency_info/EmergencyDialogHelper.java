package com.example.b07_project21.ui.emergency_info;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.b07_project21.databinding.FragmentEmergencyDialogBinding;

import dataAccess.DatabaseAccess;
import dataAccess.infoType;

public class EmergencyDialogHelper {
    /** Show dialog to add/edit info. Saves input to Firebase.
     * @param fragment EmergencyFragment
     * @param type type of info to add
     * @param title title of dialog
     * @param hints hints for editText
     * @param prevInputs previous inputs if editing, null if adding
     */
    public static void showDialog(EmergencyFragment fragment, infoType type, String title, GeneralInfo hints, @Nullable GeneralInfo prevInputs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        FragmentEmergencyDialogBinding binding = FragmentEmergencyDialogBinding.inflate(fragment.getLayoutInflater(), null, false);
        View contactView = binding.getRoot();
        builder.setView(contactView);

        binding.emergencyDialogTitle.setText(title);
        EditText textInput1 = binding.emergencyInput1;
        EditText textInput2 = binding.emergencyInput2;
        EditText textInput3 = binding.emergencyInput3;

        // Set hints
        textInput1.setHint(hints.first());
        textInput2.setHint(hints.second());
        textInput3.setHint(hints.third());

        // Hide unnecessary fields
        if (hints.first() == null) textInput1.setVisibility(View.GONE);
        if (hints.second() == null) textInput2.setVisibility(View.GONE);
        if (hints.third() == null) textInput3.setVisibility(View.GONE);

        // Set previous inputs (if editing)
        if (prevInputs != null) {
            textInput1.setText((prevInputs.first() != null) ? prevInputs.first() : "");
            textInput2.setText((prevInputs.second() != null) ? prevInputs.second() : "");
            textInput3.setText((prevInputs.third() != null) ? prevInputs.third() : "");
        }

        if (type == infoType.MEDICATION) textInput3.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Set up buttons
        builder.setPositiveButton("Save", null); // Functionality is added after .show()
        builder.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());

        // Create dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Save button functionality
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            // Get user input
            String userInput1 = textInput1.getText().toString();
            String userInput2 = textInput2.getText().toString();
            String userInput3 = textInput3.getText().toString();
            Object inputData = new GeneralInfo(userInput1, userInput2, userInput3).castTo(type);

            // Delete previous contact if editing
            if (prevInputs != null) {
                fragment.delete(type, prevInputs.castTo(type));
            }

            // Check inputs for empty fields
            if (hints.first() != null && userInput1.isEmpty()) {
                textInput1.setError("Required");
                return;
            } else if (hints.second() != null && userInput2.isEmpty()) {
                textInput2.setError("Required");
                return;
            } else if (hints.third() != null && userInput3.isEmpty()) {
                textInput3.setError("Required");
                return;
            }

            // Update database & list
            DatabaseAccess.writeData(type, inputData, fragment.getData());

            dialog.dismiss();
        });
    }
}
