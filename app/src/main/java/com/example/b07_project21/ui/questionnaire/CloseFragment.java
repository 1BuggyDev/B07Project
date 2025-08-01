package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.google.firebase.auth.FirebaseUser;

import dataAccess.AccountListener;
import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.LoginManager;
import dataAccess.Pair;
import dataAccess.infoType;

/**
 * This class controls the activity for the questionnaire closing page
 * The class has fields to keep track of the user's answers to the questions
 * The class methods control which page the user goes to based on the button clicked
 */
public class CloseFragment extends Fragment implements AccountListener, DataListener {
    /**
     * Fields for the page's buttons and for questionnaire aspects
     */
    private LinearLayout rightButton, leftButton;
    private int situation, live_status;
    private String city, safe_room, children, code_word;
    private String abuse_situation, recording, contact;
    private int bag, temp_status;
    private String date, stash, temp_place;
    private int have_contacted, order_status, tools_status;
    private String order_type, tools_type;
    private int support_status;

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
        View root = inflater.inflate(R.layout.fragment_questionnaire_close, container, false);

        // get information from bundle
        if (getArguments() != null) {
            situation = getArguments().getInt("situation");  // 1, 2, 3
            city = getArguments().getString("selected_city");  // "Toronto", ...
            live_status = getArguments().getInt("live_with");  // 1, 2, 3, 4
            safe_room = getArguments().getString("safe_room");  // {safe_room}
            children = getArguments().getString("children");  // y, n
            code_word = getArguments().getString("code_word");  // {code_word} or NONE

            abuse_situation = getArguments().getString("abuse_situation");  // "YNYN"
            recording = getArguments().getString("recording");  // y, n
            contact = getArguments().getString("contact");  // {contact_name}

            date = getArguments().getString("date");  // {yyyy-mm-dd}
            bag = getArguments().getInt("bag");  // 1, 2
            stash = getArguments().getString("stash");  // {location}
            temp_status = getArguments().getInt("temp_status");  // 1, 2
            temp_place = getArguments().getString("temp_place");  // {place}

            have_contacted = getArguments().getInt("have_contacted");  // 1, 2
            order_status = getArguments().getInt("order_status");  // 1, 2
            order_type = getArguments().getString("order_type");  // {yyyy-mm-dd}
            tools_status = getArguments().getInt("tools_status");  // 1, 2
            tools_type = getArguments().getString("tools_type");  // {yyyy-mm-dd}

            support_status = getArguments().getInt("support_status");  // 1, 2, 3, 4
        }

        // Find views
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // Set up click listeners
        NavController navController = NavHostFragment.findNavController(CloseFragment.this);

        leftButton.setOnClickListener(v -> {
            Bundle b = reBundle();
            //Toast.makeText(getContext(), "Back to Follow", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_follow, b);
        });

        rightButton.setOnClickListener(v -> {
            updateData();
            IntroFragment.questionnaireCompleted = true;
            // Toast.makeText(getContext(), "Questionnaire Completed", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_home);
        });

        return root;
    }

    // update user data to Firebase
    private void updateData() {
        // update data to FireBase
        LoginManager.attemptLogin("maxwang972@gmail.com", "B07Test", this);
    }

    // update user information to Firebase
    public void onEmailLogin(FirebaseUser user) {
        // general
        Pair<String> q_done = new Pair<>("Questionnaire done?", "true");  // "true"/"false" (may not exist, so check if exists first)

        // warm-up
        Pair<String> w_situation = new Pair<>("Which best describes your situation?", Integer.toString(situation));  // 1 (Still), 2 (Plan), 3 (Post) [Strings]
        Pair<String> w_city = new Pair<>("What city do you live in?", city);  // "Toronto"..
        Pair<String> w_room = new Pair<>("Which room in your home feels the safest if you need to get away quickly?", safe_room);  // "{room}"
        Pair<String> w_live = new Pair<>("Who do you live with?", Integer.toString(live_status));  // 1 (Family), 2 (Roommates), 3 (Alone), 4 (Partner) [Strings]
        Pair<String> w_child = new Pair<>("Do you have children?", children);  // "y", "n"
        Pair<String> w_word = new Pair<>("What is your family's safety code word?", code_word);  // "{word}" or "NONE"

        // branch 1
        Pair<String> b1_abuse = new Pair<>("Which form(s) of abuse are you experiencing?", abuse_situation);  // "YNYN" [Physical | Emotional | Financial | Other] or "NONE"
        Pair<String> b1_record = new Pair<>("Are you recording the incidents (dates, times, details)?", recording);  // "y", "n" or "NONE"
        Pair<String> b1_contact = new Pair<>("Name one person you can call immediately in an emergency", contact);  // "{contact}" or "NONE"

        // branch 2
        Pair<String> b2_date = new Pair<>("When do you plan to leave?", date);  // "{date}" or "NONE"
        Pair<String> b2_bag = new Pair<>("Do you have a go-bag packed with essentials?", Integer.toString(bag));  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
        Pair<String> b2_stash = new Pair<>("Where do you keep emergency cash or cards?", stash);  // "{location}" or "NONE"
        Pair<String> b2_temp_status = new Pair<>("Have you identified a safe place to stay?", Integer.toString(temp_status));  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
        Pair<String> b2_temp_place = new Pair<>("Where do you plan to stay?", temp_place);  // "{place}" or "NONE"

        // branch 3
        Pair<String> b3_contacted = new Pair<>("Has the abuser continued any contact?", Integer.toString(have_contacted));  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
        Pair<String> b3_order = new Pair<>("Do you have a protection order? If yes, which type?", Integer.toString(order_status));  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
        Pair<String> b3_order_type = new Pair<>("What legal order do you have?", order_type);  // "{legal_order}" or "NONE"
        Pair<String> b3_tools = new Pair<>("Are you using any safety tools (cameras, door alarms)?", Integer.toString(tools_status));  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
        Pair<String> b3_tools_type = new Pair<>("What safety tools are you using?", tools_type);  // "{safety_tools}" or "NONE"

        // follow-up
        Pair<String> f_help = new Pair("Which type of support would help most right now?", Integer.toString(support_status));  // 1 (Counselling), 2 (Legal), 3 (Financial), 4 (Co-parenting) [Strings]

        // adding user data to Firebase if user not null
        if (user != null) {
            // general
            DatabaseAccess.writeData(infoType.ANSWER, q_done);

            // warm-up
            DatabaseAccess.writeData(infoType.ANSWER, w_situation);
            DatabaseAccess.writeData(infoType.ANSWER, w_city);
            DatabaseAccess.writeData(infoType.ANSWER, w_room);
            DatabaseAccess.writeData(infoType.ANSWER, w_live);
            DatabaseAccess.writeData(infoType.ANSWER, w_child);
            DatabaseAccess.writeData(infoType.ANSWER, w_word);

            // branch 1
            DatabaseAccess.writeData(infoType.ANSWER, b1_abuse);
            DatabaseAccess.writeData(infoType.ANSWER, b1_record);
            DatabaseAccess.writeData(infoType.ANSWER, b1_contact);

            // branch 2
            DatabaseAccess.writeData(infoType.ANSWER, b2_date);
            DatabaseAccess.writeData(infoType.ANSWER, b2_bag);
            DatabaseAccess.writeData(infoType.ANSWER, b2_stash);
            DatabaseAccess.writeData(infoType.ANSWER, b2_temp_status);
            DatabaseAccess.writeData(infoType.ANSWER, b2_temp_place);

            // branch 3
            DatabaseAccess.writeData(infoType.ANSWER, b3_contacted);
            DatabaseAccess.writeData(infoType.ANSWER, b3_order);
            DatabaseAccess.writeData(infoType.ANSWER, b3_order_type);
            DatabaseAccess.writeData(infoType.ANSWER, b3_tools);
            DatabaseAccess.writeData(infoType.ANSWER, b3_tools_type);

            // follow
            DatabaseAccess.writeData(infoType.ANSWER, f_help);
        }
    }

    /**
     * Method bundles the information given by the user to be passed to the previous page
     * @return Bundle information needed to be passed to the previous page
     */
    private Bundle reBundle() {
        Bundle b = new Bundle();
        b.putInt("action", 0);

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
