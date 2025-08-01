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
 * This class controls the activity for the questionnaire branch 3 questions page
 * The class has fields to keep track of the user's answers to the questions
 * The class methods control which page the user goes to based on the button clicked
 */
public class Branch3Fragment extends Fragment {
    /**
     * Fields for the page's buttons and for questionnaire aspects
     */
    private LinearLayout leftButton, rightButton;
    private int situation, live_status, have_contacted=0, order_status=0, tools_status=0;
    private String city, safe_room, children, code_word;
    private TextView order_ask, tool_ask;
    private EditText order_name, tool_name;
    private CheckBox box1, box2, box3, box4, box5, box6;

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
        View root = inflater.inflate(R.layout.fragment_questionnaire_branch_3, container, false);

        // load textboxes
        TextView questionTextView1 = root.findViewById(R.id.branch3_question_1);
        TextView questionTextView2 = root.findViewById(R.id.branch3_question_2);
        TextView questionTextView3 = root.findViewById(R.id.order_ask);
        TextView questionTextView4 = root.findViewById(R.id.branch3_question_3);
        TextView questionTextView5 = root.findViewById(R.id.tool_ask);

        // load question titles
        loadQuestions(questionTextView1, questionTextView2, questionTextView3, questionTextView4, questionTextView5);

        // get information from bundle
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
        maintainBoxIntegrity();  // make sure only 1 box

        // Q2 checkboxes
        box3 = root.findViewById(R.id.checkbox21);
        box4 = root.findViewById(R.id.checkbox22);
        order_ask = root.findViewById(R.id.order_ask);
        order_name = root.findViewById(R.id.order_text);
        maintainBoxYN1Integrity();  // make sure only 1 box

        // Q3 checkboxes
        box5 = root.findViewById(R.id.checkbox31);
        box6 = root.findViewById(R.id.checkbox32);
        tool_ask = root.findViewById(R.id.tool_ask);
        tool_name = root.findViewById(R.id.tool_text);
        maintainBoxYN2Integrity();  // make sure only 1 box

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(Branch3Fragment.this);

        leftButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Back to Warm", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_warm);
        });

        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Follow", Toast.LENGTH_SHORT).show();
            if (countBoxIntegrity() != 1 || countBoxYN1Integrity() != 1 || countBoxYN2Integrity() != 1) {
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
     * @param questionTextView4 question 4
     * @param questionTextView5 question 5
     */
    private void loadQuestions(TextView questionTextView1, TextView questionTextView2, TextView questionTextView3,
                               TextView questionTextView4, TextView questionTextView5)
    {
        try {
            // load JSON file
            JSONObject json = loadJSONFromAsset("questions.json");

            // Read "question" from questions
            JSONArray qArray1 = json.getJSONArray("q 15");
            String questionText1 = qArray1.getJSONObject(0).getString("question");
            JSONArray qArray2 = json.getJSONArray("q 16");
            String questionText2 = qArray2.getJSONObject(0).getString("question");
            JSONArray qArray3 = json.getJSONArray("q 17");
            String questionText3 = qArray3.getJSONObject(0).getString("question");
            JSONArray qArray4 = json.getJSONArray("q 18");
            String questionText4 = qArray4.getJSONObject(0).getString("question");
            JSONArray qArray5 = json.getJSONArray("q 19");
            String questionText5 = qArray5.getJSONObject(0).getString("question");

            // Set it to the TextView
            questionTextView1.setText(questionText1);
            questionTextView2.setText(questionText2);
            questionTextView3.setText(questionText3);
            questionTextView4.setText(questionText4);
            questionTextView5.setText(questionText5);

        } catch (Exception e) {
            e.printStackTrace();
            questionTextView1.setText("Error loading question.");
            questionTextView2.setText("Error loading question.");
            questionTextView3.setText("Error loading question.");
            questionTextView4.setText("Error loading question.");
            questionTextView5.setText("Error loading question.");
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
                    have_contacted = 1;
                }
            }
        });

        box2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box1.setChecked(false);
                    have_contacted = 2;
                }
            }
        });
    }

    // update question choices
    private void maintainBoxYN1Integrity() {
        box3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box4.setChecked(false);
                    order_status = 1;
                    order_ask.setVisibility(View.VISIBLE);
                    order_name.setVisibility(View.VISIBLE);
                }
                if (!isChecked) {
                    order_ask.setVisibility(View.GONE);
                    order_name.setVisibility(View.GONE);
                }
            }
        });

        box4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box3.setChecked(false);
                    order_status = 2;
                    order_ask.setVisibility(View.GONE);
                    order_name.setVisibility(View.GONE);
                }
            }
        });
    }

    // update question choices
    private void maintainBoxYN2Integrity() {
        box5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box6.setChecked(false);
                    tools_status = 1;
                    tool_ask.setVisibility(View.VISIBLE);
                    tool_name.setVisibility(View.VISIBLE);
                }
                if (!isChecked) {
                    tool_ask.setVisibility(View.GONE);
                    tool_name.setVisibility(View.GONE);
                }
            }
        });

        box6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    box5.setChecked(false);
                    tools_status = 2;
                    tool_ask.setVisibility(View.GONE);
                    tool_name.setVisibility(View.GONE);
                }
            }
        });
    }

    // check question answered
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

    // check question answered
    private int countBoxYN1Integrity() {
        int count = 0;
        if (box3.isChecked()) {
            count+=1;
            count+=orderIntegrity();
        }
        if (box4.isChecked()) {
            count+=1;
        }
        return count;
    }

    // check question answered
    private int countBoxYN2Integrity() {
        int count = 0;
        if (box5.isChecked()) {
            count+=1;
            count+=toolIntegrity();
        }
        if (box6.isChecked()) {
            count+=1;
        }
        return count;
    }

    // check question answered
    private int orderIntegrity() {
        if (getOrderType().isEmpty()) {
            return 1;
        }
        return 0;
    }

    // check question answered
    private int toolIntegrity() {
        if (getToolType().isEmpty()) {
            return 1;
        }
        return 0;
    }

    // get question answers
    private int getContacted() {
        return have_contacted;
    }
    private int getOrderStatus() {
        return order_status;
    }
    private String getOrderType() {
        if (getOrderStatus() == 1) {
            return order_name.getText().toString().trim();
        }
        return "NONE";
    }
    private int getToolStatus() {
        return tools_status;
    }
    private String getToolType() {
        if (getToolStatus() == 1) {
            return tool_name.getText().toString().trim();
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

        b.putInt("have_contacted", getContacted());
        b.putInt("order_status", getOrderStatus());
        b.putString("order_type", getOrderType());
        b.putInt("tools_status", getToolStatus());
        b.putString("tools_type", getToolType());

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