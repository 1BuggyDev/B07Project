package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;

import java.util.Objects;

public class WarmFragment extends Fragment {
    private LinearLayout leftButton, rightButton;
    private CheckBox box1, box2, box3, box4, box5, box6, box7, box8, box9;
    private Spinner spin_city;
    private TextView code_title;
    private EditText safe_room, code_word;
    private int situation = 0, live_status = 0;
    private String children;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set view
        View root = inflater.inflate(R.layout.fragment_questionnaire_warm, container, false);

        // Q1 checkboxes
        box1 = root.findViewById(R.id.checkbox1);
        box2 = root.findViewById(R.id.checkbox2);
        box3 = root.findViewById(R.id.checkbox3);
        maintainBoxIntegrity();  // make sure only 1 box

        // Q2 spinner (dropdown)
        spin_city = root.findViewById(R.id.city_spinner);
        maintainSpinner();

        // Q3 free-form text
        safe_room = root.findViewById(R.id.safe_room_textbox);

        // Q4 checkboxes
        box4 = root.findViewById(R.id.checkbox41);
        box5 = root.findViewById(R.id.checkbox42);
        box6 = root.findViewById(R.id.checkbox43);
        box7 = root.findViewById(R.id.checkbox44);
        maintainBoxLiveIntegrity();  // make sure only 1 box

        // Q5 checkboxes
        box8 = root.findViewById(R.id.checkbox51);
        box9 = root.findViewById(R.id.checkbox52);
        code_title = root.findViewById(R.id.child_safe_word_text);
        code_word = root.findViewById(R.id.child_safe_word);
        maintainBoxYNIntegrity();  // make sure only 1 box

        // buttons
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // click listeners
        NavController navController = NavHostFragment.findNavController(WarmFragment.this);

        // left button action
        leftButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Back to Q-Home", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_questionnaire);
        });

        // right button action
        rightButton.setOnClickListener(v -> {
            if (countBoxIntegrity() != 1 || safeRoomIntegrity() == 0  || countBoxLiveIntegrity() != 1 || countBoxYNIntegrity() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                if (situation == 1) {
                    // Toast.makeText(getContext(), "Next to Branch 1", Toast.LENGTH_SHORT).show();

                    navController.navigate(R.id.nav_question_branch_1, b);
                } else if (situation == 2) {
                    // Toast.makeText(getContext(), "Next to Branch 2", Toast.LENGTH_SHORT).show();

                    navController.navigate(R.id.nav_question_branch_2, b);
                } else if (situation == 3) {
                    Toast.makeText(getContext(), "Next to Branch 3", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_question_branch_3, b);
                }
            }
        });

        // return view
        return root;
    }

    private void maintainBoxIntegrity() {
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box2.setChecked(false);
                    box3.setChecked(false);
                    situation = 1;
                }
            }
        });

        box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    box3.setChecked(false);
                    situation = 2;
                }
            }
        });

        box3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box2.setChecked(false);
                    box1.setChecked(false);
                    situation = 3;
                }
            }
        });
    }

    private void maintainBoxLiveIntegrity() {
        box4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box5.setChecked(false);
                    box6.setChecked(false);
                    box7.setChecked(false);
                    live_status = 1;
                }
            }
        });

        box5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box4.setChecked(false);
                    box6.setChecked(false);
                    box7.setChecked(false);
                    live_status = 2;
                }
            }
        });

        box6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box4.setChecked(false);
                    box5.setChecked(false);
                    box7.setChecked(false);
                    live_status = 3;
                }
            }
        });

        box7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box4.setChecked(false);
                    box5.setChecked(false);
                    box6.setChecked(false);
                    live_status = 4;
                }
            }
        });
    }

    private void maintainBoxYNIntegrity() {
        box8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box9.setChecked(false);
                    children = "y";
                    code_title.setVisibility(View.VISIBLE);
                    code_word.setVisibility(View.VISIBLE);
                }
                if (!isChecked) {
                    code_title.setVisibility(View.GONE);
                    code_word.setVisibility(View.GONE);
                }
            }
        });

        box9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box8.setChecked(false);
                    children = "n";
                    code_title.setVisibility(View.GONE);
                    code_word.setVisibility(View.GONE);
                }
            }
        });
    }

    private void maintainSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cities, // Replace with your actual array name
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_city.setAdapter(adapter);
    }

    private int countBoxIntegrity() {
        int count = 0;
        if (box1.isChecked()) {
            count+=1;
        }
        if (box2.isChecked()) {
            count+=1;
        }
        if (box3.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int countBoxLiveIntegrity() {
        int count = 0;
        if (box4.isChecked()) {
            count+=1;
        }
        if (box5.isChecked()) {
            count+=1;
        }
        if (box6.isChecked()) {
            count+=1;
        }
        if (box7.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int countBoxYNIntegrity() {
        int count = 0;
        if (box8.isChecked()) {
            count+=1;
            count+=safeWordIntegrity();
        }
        if (box9.isChecked()) {
            count+=1;
        }
        return count;
    }

    private int safeRoomIntegrity() {
        if (getSafeRoom().isEmpty()) {
            return 0;
        }
        return 1;
    }
    private int safeWordIntegrity() {
        if (getCodeWord().isEmpty()) {
            return 1;
        }
        return 0;
    }

    // To pass along arguments
    private int getSituation() {
        return situation;
    }

    private String getCity() {
        return spin_city.getSelectedItem().toString();
    }

    private String getSafeRoom() {
        return safe_room.getText().toString().trim();
    }

    private int getLiveSituation() {
        return live_status;
    }

    private String getChildrenStatus() {
        return children;
    }

    private String getCodeWord() {
        if (getChildrenStatus().equals("y")) {
            return code_word.getText().toString().trim();
        }
        return "NONE";
    }

    private Bundle makeBundle() {
        Bundle b = new Bundle();
        // bundle to pass data
        b.putInt("action", 1);

        b.putInt("situation", getSituation());
        b.putString("selected_city", getCity());
        b.putString("safe_room", getSafeRoom());
        b.putInt("live_with", getLiveSituation());
        b.putString("children", getChildrenStatus());
        b.putString("code_word", getCodeWord());
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