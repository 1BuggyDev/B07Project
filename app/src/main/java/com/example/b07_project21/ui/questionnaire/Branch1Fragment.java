package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class controls the activity for the questionnaire branch 1 questions page
 * The class has fields to keep track of the user's answers to the questions
 * The class methods control which page the user goes to based on the button clicked
 */
public class Branch1Fragment extends Fragment {
    /**
     * Fields for the page's buttons and for questionnaire aspects
     */
    private LinearLayout leftButton, rightButton;
    private int situation, live_status;
    private String city, safe_room, children, code_word, abuse_status="NNNN", recording;
    private CheckBox box1, box2, box3, box4, box5, box6;
    private EditText contact;

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
        // inflates view
        View root = inflater.inflate(R.layout.fragment_questionnaire_branch_1, container, false);

        // load textbox
        TextView questionTextView1 = root.findViewById(R.id.branch1_question_1);
        TextView questionTextView2 = root.findViewById(R.id.branch1_question_2);
        TextView questionTextView3 = root.findViewById(R.id.branch1_question_3);

        // get text titles
        loadQuestions(questionTextView1, questionTextView2, questionTextView3);

        // get information from the bundle
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

        // Q2 checkboxes (Yes/No)
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
            navController.navigate(R.id.nav_question_warm);
        });

        rightButton.setOnClickListener(v -> {
            if (checkAbuseValid() != 1  || countBoxYNIntegrity() != 1 || checkContactValid() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                navController.navigate(R.id.nav_question_follow, b);
            }
        });

        return root;
    }

    /**
     * Method loads the questions to the screen
     * @param questionTextView1 question 1
     * @param questionTextView2 question 2
     * @param questionTextView3 question 3
     */
    private void loadQuestions(TextView questionTextView1, TextView questionTextView2, TextView questionTextView3)
    {
        try {
            // load JSON file
            JSONObject json = loadJSONFromAsset("questions.json");

            // Read "question" from questions
            JSONArray qArray1 = json.getJSONArray("q 7");
            String questionText1 = qArray1.getJSONObject(0).getString("question");
            JSONArray qArray2 = json.getJSONArray("q 8");
            String questionText2 = qArray2.getJSONObject(0).getString("question");
            JSONArray qArray3 = json.getJSONArray("q 9");
            String questionText3 = qArray3.getJSONObject(0).getString("question");

            // Set it to the TextView
            questionTextView1.setText(questionText1);
            questionTextView2.setText(questionText2);
            questionTextView3.setText(questionText3);

        } catch (Exception e) {
            e.printStackTrace();
            questionTextView1.setText("Error loading question.");
            questionTextView2.setText("Error loading question.");
            questionTextView3.setText("Error loading question.");
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
        InputStream is = getContext().getAssets().open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new JSONObject(new String(buffer, "UTF-8"));
    }

    // update boxes based on clicks
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

    // Fixed method to prevent checkbox disappearing
    private void maintainBoxYNIntegrity() {
        // Create the listeners first
        box5Listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Temporarily remove listener to prevent infinite loop
                    box6.setOnCheckedChangeListener(null);
                    box6.setChecked(false);
                    // Restore listener
                    box6.setOnCheckedChangeListener(box6Listener);
                    recording = "y";
                }
            }
        };

        box6Listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Temporarily remove listener to prevent infinite loop
                    box5.setOnCheckedChangeListener(null);
                    box5.setChecked(false);
                    // Restore listener
                    box5.setOnCheckedChangeListener(box5Listener);
                    recording = "n";
                }
            }
        };

        // Set the listeners
        box5.setOnCheckedChangeListener(box5Listener);
        box6.setOnCheckedChangeListener(box6Listener);
    }

    // Store listeners to restore them
    private CompoundButton.OnCheckedChangeListener box5Listener;
    private CompoundButton.OnCheckedChangeListener box6Listener;

    // check question answered
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

    // check question answered
    private int checkAbuseValid() {
        if (getAbuseStatus().equals("NNNN")) {
            return 0;
        }
        return 1;
    }

    // check question answered
    private int checkContactValid() {
        if (getContact().isEmpty()) {
            return 0;
        }
        return 1;
    }

    // get variable information
    private String getAbuseStatus() {
        return abuse_status;
    }
    private String getRecording() {
        return recording;
    }
    private String getContact() {
        return contact.getText().toString().trim();
    }

    /**
     * Method bundles the information given by the user to be passed to the next page
     * @return Bundle information needed to be passed to the next page
     */
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