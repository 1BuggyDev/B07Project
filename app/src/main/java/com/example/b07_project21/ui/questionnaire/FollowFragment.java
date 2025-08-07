package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
 * This class controls the activity for the questionnaire follow-up questions page
 * The class has fields to keep track of the user's answers to the questions
 * The class methods control which page the user goes to based on the button clicked
 */
public class FollowFragment extends Fragment {
    /**
     * Fields for the page's buttons and for questionnaire aspects
     */
    private LinearLayout leftButton, rightButton;

    private int situation, live_status;
    private String city, safe_room, children, code_word;
    private String abuse_situation, recording, contact;
    private int bag, temp_status;
    private String date, stash, temp_place;
    private int have_contacted, order_status, tools_status;
    private String order_type, tools_type;
    private CheckBox box1, box2, box3, box4;
    private int support_status=0;

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
        // inflate view
        View root = inflater.inflate(R.layout.fragment_questionnaire_follow, container, false);
        // load textbox
        TextView questionTextView1 = root.findViewById(R.id.follow_question_1);
        // load questions
        loadQuestions(questionTextView1);

        // getting information from bundle
        if (getArguments() != null) {
            situation = getArguments().getInt("situation");  // 1, 2, 3
            city = getArguments().getString("selected_city");  // "Toronto", ...
            live_status = getArguments().getInt("live_with");  // 1, 2, 3, 4
            safe_room = getArguments().getString("safe_room");  // {safe_room}
            children = getArguments().getString("children");  // y, n
            code_word = getArguments().getString("code_word");  // {code_word} or NONE

            if (situation == 1) {
                abuse_situation = getArguments().getString("abuse_situation");  // "YNYN"
                recording = getArguments().getString("recording");  // y, n
                contact = getArguments().getString("contact");  // {contact_name}

                date = "NONE";
                bag = 0;
                stash = "NONE";
                temp_status = 0;
                temp_place = "NONE";

                have_contacted = 0;
                order_status = 0;
                order_type = "NONE";
                tools_status = 0;
                tools_type = "NONE";
            } else if (situation == 2) {
                date = getArguments().getString("date");  // {yyyy-mm-dd}
                bag = getArguments().getInt("bag");  // 1, 2
                stash = getArguments().getString("stash");  // {location}
                temp_status = getArguments().getInt("temp_status");  // 1, 2
                temp_place = getArguments().getString("temp_place");  // {place}

                abuse_situation = "NONE";
                recording = "NONE";
                contact = "NONE";

                have_contacted = 0;
                order_status = 0;
                order_type = "NONE";
                tools_status = 0;
                tools_type = "NONE";
            }
            else if (situation == 3) {
                have_contacted = getArguments().getInt("have_contacted");  // 1, 2
                order_status = getArguments().getInt("order_status");  // 1, 2
                order_type = getArguments().getString("order_type");  // {yyyy-mm-dd}
                tools_status = getArguments().getInt("tools_status");  // 1, 2
                tools_type = getArguments().getString("tools_type");  // {yyyy-mm-dd}

                abuse_situation = "NONE";
                recording = "NONE";
                contact = "NONE";

                date = "NONE";
                bag = 0;
                stash = "NONE";
                temp_status = 0;
                temp_place = "NONE";
            }
        }

        // Q4 checkboxes
        box1 = root.findViewById(R.id.checkbox11);
        box2 = root.findViewById(R.id.checkbox12);
        box3 = root.findViewById(R.id.checkbox13);
        box4 = root.findViewById(R.id.checkbox14);
        maintainBoxIntegrity();  // make sure only 1 box

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(FollowFragment.this);
        // left button choices
        leftButton.setOnClickListener(v -> {
            Bundle b = reBundle();
            if (situation == 1) {
                navController.navigate(R.id.nav_question_branch_1, b);
            } else if (situation == 2) {
                navController.navigate(R.id.nav_question_branch_2, b);
            } else if (situation == 3) {
                navController.navigate(R.id.nav_question_branch_3, b);
            }
        });
        // right button choices
        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Close", Toast.LENGTH_SHORT).show();
            if (countBoxIntegrity() != 1) {
                Toast.makeText(getContext(), "Answer all questions to proceed", Toast.LENGTH_SHORT).show();
            } else {
                Bundle b = makeBundle();
                navController.navigate(R.id.nav_question_close, b);
            }
        });

        return root;
    }

    /**
     * Method loads the questions to the screen
     * @param questionTextView1 question 1
     */
    private void loadQuestions(TextView questionTextView1) {
        try {
            // load JSON file
            JSONObject json = loadJSONFromAsset("questions.json");

            // Read "question" from questions
            JSONArray qArray1 = json.getJSONArray("q 20");
            String questionText1 = qArray1.getJSONObject(0).getString("question");

            // Set it to the TextView
            questionTextView1.setText(questionText1);

        } catch (Exception e) {
            e.printStackTrace();
            questionTextView1.setText("Error loading question.");
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

    // update question choices
    private void maintainBoxIntegrity() {
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box2.setChecked(false);
                    box3.setChecked(false);
                    box4.setChecked(false);
                    support_status = 1;
                }
            }
        });

        box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    box3.setChecked(false);
                    box4.setChecked(false);
                    support_status = 2;
                }
            }
        });

        box3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    box2.setChecked(false);
                    box4.setChecked(false);
                    support_status = 3;
                }
            }
        });

        box4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    box2.setChecked(false);
                    box3.setChecked(false);
                    support_status = 4;
                }
            }
        });
    }

    // update question choices
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
        if (box4.isChecked()) {
            count+=1;
        }
        return count;
    }

    // get question answer
    private int getSupportStatus() {
        return support_status;
    }

    /**
     * Method bundles the information given by the user to be passed to the next page
     * @return Bundle information needed to be passed to the next page
     */
    private Bundle makeBundle() {
        Bundle b = new Bundle();

        // bundle to pass data
        b.putInt("action", 1);

        // follow
        b.putInt("support_status", getSupportStatus());

        // warm-up
        b.putInt("situation", situation);
        b.putString("selected_city", city);
        b.putString("safe_room", safe_room);
        b.putInt("live_with", live_status);
        b.putString("children", children);
        b.putString("code_word", code_word);

        // branch-1
        b.putString("abuse_situation", abuse_situation);
        b.putString("recording", recording);
        b.putString("contact", contact);

        // branch-2
        b.putString("date", date);
        b.putInt("bag", bag);
        b.putString("stash", stash);
        b.putInt("temp_status", temp_status);
        b.putString("temp_place", temp_place);

        // branch-3
        b.putInt("have_contacted", have_contacted);
        b.putInt("order_status", order_status);
        b.putString("order_type", order_type);
        b.putInt("tools_status", tools_status);
        b.putString("tools_type", tools_type);

        return b;
    }

    /**
     * Method bundles the information given by the user to be passed to the previous page
     * @return Bundle information needed to be passed to the previous page
     */
    private Bundle reBundle() {
        Bundle b = new Bundle();
        b.putInt("action", 0);

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