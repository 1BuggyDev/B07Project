package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;

public class Branch1Fragment extends Fragment {
    private LinearLayout leftButton, rightButton;
    private int situation, live_status;
    private String city, safe_room, children, code_word, abuse_status="NNNN", recording;
    private CheckBox box1, box2, box3, box4, box5, box6;
    private EditText contact;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_questionnaire_branch_1, container, false);

        if (getArguments() != null) {
            situation = getArguments().getInt("situation");  // 1, 2, 3
            city = getArguments().getString("selected_city");  // "Toronto", ...
            live_status = getArguments().getInt("live_with");  // 1, 2, 3, 4
            safe_room = getArguments().getString("safe_room");  // {safe_room}
            children = getArguments().getString("children");  // y, n
            code_word = getArguments().getString("code_word");  // {code_word} or NONE
        }

        // Q1 checkboxes
        box1 = root.findViewById(R.id.checkbox11);
        box2 = root.findViewById(R.id.checkbox12);
        box3 = root.findViewById(R.id.checkbox13);
        box4 = root.findViewById(R.id.checkbox14);
        trackAbuseBoxes();

        // Q2 spinner (dropdown)
        box5 = root.findViewById(R.id.checkbox21);
        box6 = root.findViewById(R.id.checkbox22);
        maintainBoxYNIntegrity();

        // Q3 free-form text
        contact = root.findViewById(R.id.contact_textbox);

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(Branch1Fragment.this);

        leftButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Back to Warm", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_warm);
        });

        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Follow", Toast.LENGTH_SHORT).show();
            if (checkAbuseValid() != 1  || countBoxYNIntegrity() != 1 || checkContactValid() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                navController.navigate(R.id.nav_question_follow, b);
            }
        });

        return root;
    }

    private void trackAbuseBoxes() {
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    abuse_status = "Y" + abuse_status.substring(1,4);
                } else {
                    abuse_status = "N" + abuse_status.substring(1,4);
                }
            }
        });

        box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    abuse_status = abuse_status.charAt(0) + "Y" + abuse_status.substring(2,4);
                } else {
                    abuse_status = abuse_status.charAt(0) + "N" + abuse_status.substring(2,4);
                }
            }
        });

        box3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    abuse_status = abuse_status.substring(0,2) + "Y" + abuse_status.charAt(3);
                } else {
                    abuse_status = abuse_status.substring(0,2) + "N" + abuse_status.charAt(3);
                }
            }
        });

        box4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    abuse_status = abuse_status.substring(0,3) + "Y";
                } else {
                    abuse_status = abuse_status.substring(0,3) + "N";
                }
            }
        });
    }

    private void maintainBoxYNIntegrity() {
        box5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box6.setChecked(false);
                    recording = "y";
                }
            }
        });

        box6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box5.setChecked(false);
                    recording = "n";
                }
            }
        });
    }

    private int countBoxYNIntegrity() {
        int count = 0;
        if (box5.isChecked()) {
            count+=1;
        }
        if (box6.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int checkAbuseValid() {
        if (getAbuseStatus().equals("NNNN")) {
            return 0;
        }
        return 1;
    }

    private int checkContactValid() {
        if (getContact().isEmpty()) {
            return 0;
        }
        return 1;
    }

    private String getAbuseStatus() {
        return abuse_status;
    }

    private String getRecording() {
        return recording;
    }

    private String getContact() {
        return contact.getText().toString().trim();
    }

    private Bundle makeBundle() {
        Bundle b = new Bundle();
        // bundle to pass data
        b.putInt("action", 1);

        b.putString("abuse_situation", getAbuseStatus());
        b.putString("recording", getRecording());
        b.putString("contact", getContact());

        b.putInt("situation", situation);
        b.putString("selected_city", city);
        b.putString("safe_room", safe_room);
        b.putInt("live_with", live_status);
        b.putString("children", children);
        b.putString("code_word", code_word);
        return b;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Null out view references for safety
        leftButton = null;
        rightButton = null;
    }
}