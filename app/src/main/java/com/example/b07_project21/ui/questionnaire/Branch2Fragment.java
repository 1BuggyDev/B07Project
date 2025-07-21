package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;

public class Branch2Fragment extends Fragment {
    private LinearLayout leftButton, rightButton;
    private int situation, live_status, bag=0, temp_status;
    private String city, safe_room, children, code_word;
    private EditText date, stash, temp_place;
    private CheckBox box1, box2, box3, box4;
    private TextView temp_ask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_questionnaire_branch_2, container, false);

        if (getArguments() != null) {
            situation = getArguments().getInt("situation");  // 1, 2, 3
            city = getArguments().getString("selected_city");  // "Toronto", ...
            live_status = getArguments().getInt("live_with");  // 1, 2, 3, 4
            safe_room = getArguments().getString("safe_room");  // {safe_room}
            children = getArguments().getString("children");  // y, n
            code_word = getArguments().getString("code_word");  // {code_word} or NONE
        }

        // Q1 free-form text
        date = root.findViewById(R.id.day_textbox);

        // Q2 checkboxes
        box1 = root.findViewById(R.id.checkbox21);
        box2 = root.findViewById(R.id.checkbox22);
        maintainBoxIntegrity();  // make sure only 1 box

        // Q3 free-form text
        stash = root.findViewById(R.id.cash_textbox);

        // Q4 checkboxes
        box3 = root.findViewById(R.id.checkbox41);
        box4 = root.findViewById(R.id.checkbox42);
        temp_ask = root.findViewById(R.id.safe_place_ask);
        temp_place = root.findViewById(R.id.safe_place_text);
        maintainBoxYNIntegrity();  // make sure only 1 box

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(Branch2Fragment.this);

        leftButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Back to Warm", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_warm);
        });

        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Follow", Toast.LENGTH_SHORT).show();
            if (dateIntegrity() != 1  || countBoxIntegrity() != 1 || stashIntegrity() != 1 || countBoxYNIntegrity() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                navController.navigate(R.id.nav_question_follow, b);
            }
        });

        return root;
    }

    private int dateIntegrity() {
        if (getLeaveDate().isEmpty()) {
            return 0;
        }
        return 1;
    }

    private int stashIntegrity() {
        if (getStash().isEmpty()) {
            return 0;
        }
        return 1;
    }

    private void maintainBoxIntegrity() {
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box2.setChecked(false);
                    bag = 1;
                }
            }
        });

        box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    bag = 2;
                }
            }
        });
    }

    private void maintainBoxYNIntegrity() {
        box3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box4.setChecked(false);
                    temp_status = 1;
                    temp_ask.setVisibility(View.VISIBLE);
                    temp_place.setVisibility(View.VISIBLE);
                }
                if (!isChecked) {
                    temp_ask.setVisibility(View.GONE);
                    temp_place.setVisibility(View.GONE);
                }
            }
        });

        box4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box3.setChecked(false);
                    temp_status = 2;
                    temp_ask.setVisibility(View.GONE);
                    temp_place.setVisibility(View.GONE);
                }
            }
        });
    }

    private int countBoxIntegrity() {
        int count = 0;
        if (box1.isChecked()) {
            count+=1;
        }
        if (box2.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int countBoxYNIntegrity() {
        int count = 0;
        if (box3.isChecked()) {
            count+=1;
            count+=tempPlaceIntegrity();
        }
        if (box4.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int tempPlaceIntegrity() {
        if (getTempPlace().isEmpty()) {
            return 1;
        }
        return 0;
    }

    private String getLeaveDate() {
        return date.getText().toString().trim();
    }

    private int getPackStatus() {
        return bag;
    }

    private String getStash() {
        return stash.getText().toString().trim();
    }

    private int getTempStatus() {
        return temp_status;
    }

    private String getTempPlace() {
        if (getTempStatus() == 1) {
            return temp_place.getText().toString().trim();
        }
        return "NONE";
    }

    private Bundle makeBundle() {
        Bundle b = new Bundle();
        // bundle to pass data
        b.putInt("action", 1);

        b.putString("date", getLeaveDate());
        b.putInt("bag", getPackStatus());
        b.putString("stash", getStash());
        b.putInt("temp_status", getTempStatus());
        b.putString("temp_place", getTempPlace());

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