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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class controls the activity for the questionnaire warm-up questions page
 * The class has fields to keep track of the user's answers to the questions
 * The class methods control which page the user goes to based on the button clicked
 */
public class WarmFragment extends Fragment {
    /**
     * Fields for the page's buttons and for questionnaire aspects
     */
    private LinearLayout leftButton, rightButton;
    private CheckBox box1, box2, box3, box4, box5, box6, box7, box8, box9;
    private Spinner spin_city;
    private TextView code_title;
    private EditText safe_room, code_word;
    private int situation = 0, live_status = 0;
    private String children;

    /**
     * Method acts as a constructor for the class, initializes the initial view of the page
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return root view of the page
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set view
        View root = inflater.inflate(R.layout.fragment_questionnaire_warm, container, false);

        // question textboxs
        TextView questionTextView1 = root.findViewById(R.id.warm_question_1);
        TextView questionTextView2 = root.findViewById(R.id.warm_question_2);
        TextView questionTextView3 = root.findViewById(R.id.warm_question_3);
        TextView questionTextView4 = root.findViewById(R.id.warm_question_4);
        TextView questionTextView5 = root.findViewById(R.id.warm_question_5);
        TextView questionTextView6 = root.findViewById(R.id.child_safe_word_text);

        // load questions to screen
        loadQuestions(questionTextView1, questionTextView2, questionTextView3, questionTextView4, questionTextView5, questionTextView6);

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
            navController.navigate(R.id.nav_questionnaire);
        });

        // right button action
        rightButton.setOnClickListener(v -> {
            if (countBoxIntegrity() != 1 || safeRoomIntegrity() == 0  || countBoxLiveIntegrity() != 1 || countBoxYNIntegrity() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                // branching logic to redirect to the correct next page
                if (situation == 1) {
                    navController.navigate(R.id.nav_question_branch_1, b);
                } else if (situation == 2) {
                    navController.navigate(R.id.nav_question_branch_2, b);
                } else if (situation == 3) {
                    navController.navigate(R.id.nav_question_branch_3, b);
                }
            }
        });

        // return view
        return root;
    }

    /**
     * Method loads the questions to the screen
     * @param questionTextView1 question 1
     * @param questionTextView2 question 2
     * @param questionTextView3 question 3
     * @param questionTextView4 question 4
     * @param questionTextView5 question 5
     * @param questionTextView6 question 6
     */
    private void loadQuestions(TextView questionTextView1, TextView questionTextView2, TextView questionTextView3,
                               TextView questionTextView4, TextView questionTextView5, TextView questionTextView6)
    {
        try {
            // load JSON file
            JSONObject json = loadJSONFromAsset("questions.json");

            // Read "question" from questions
            JSONArray qArray1 = json.getJSONArray("q 1");
            String questionText1 = qArray1.getJSONObject(0).getString("question");
            JSONArray qArray2 = json.getJSONArray("q 2");
            String questionText2 = qArray2.getJSONObject(0).getString("question");
            JSONArray qArray3 = json.getJSONArray("q 3");
            String questionText3 = qArray3.getJSONObject(0).getString("question");
            JSONArray qArray4 = json.getJSONArray("q 4");
            String questionText4 = qArray4.getJSONObject(0).getString("question");
            JSONArray qArray5 = json.getJSONArray("q 5");
            String questionText5 = qArray5.getJSONObject(0).getString("question");
            JSONArray qArray6 = json.getJSONArray("q 6");
            String questionText6 = qArray6.getJSONObject(0).getString("question");

            // Set it to the TextView
            questionTextView1.setText(questionText1);
            questionTextView2.setText(questionText2);
            questionTextView3.setText(questionText3);
            questionTextView4.setText(questionText4);
            questionTextView5.setText(questionText5);
            questionTextView6.setText(questionText6);

        } catch (Exception e) {
            // catch error
            e.printStackTrace();
            questionTextView1.setText("Error loading question.");
            questionTextView2.setText("Error loading question.");
            questionTextView3.setText("Error loading question.");
            questionTextView4.setText("Error loading question.");
            questionTextView5.setText("Error loading question.");
            questionTextView6.setText("Error loading question.");
        }
    }

    /**
     * Method to get data from the JSON file
     * @param filename
     * @return JSONObject retrieves information from the JSON file
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject loadJSONFromAsset(String filename) throws IOException, JSONException {
        // access the JSON file and return the content object
        InputStream is = getContext().getAssets().open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new JSONObject(new String(buffer, "UTF-8"));
    }

    // updates boxes based on clicks
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

    // updates boxes based on clicks
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

    // updates boxes based on clicks
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

    // updates spinner based on choice
    private void maintainSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cities, // Replace with your actual array name
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_city.setAdapter(adapter);
    }

    // makes sure the question was answered properly
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

    // makes sure the question was answered properly
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

    // makes sure the questions were answered properly
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

    // makes sure the room was given
    private int safeRoomIntegrity() {
        if (getSafeRoom().isEmpty()) {
            return 0;
        }
        return 1;
    }

    // make suer the safe word was given
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

    /**
     * Method bundles the information given by the user to be passed to the next page
     * @return Bundle information needed to be passed to the next page
     */
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

    /**
     * This method acts as a deconstructor for the view
     * Destroys the view and sets button fields to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Null out view references for safety
        leftButton = null;
        rightButton = null;
    }
}