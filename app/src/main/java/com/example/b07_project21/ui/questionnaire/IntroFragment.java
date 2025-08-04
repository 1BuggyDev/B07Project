package com.example.b07_project21.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import dataAccess.AccountListener;
import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.LoginManager;
import dataAccess.infoType;

/**
 * This class controls the activity for the questionnaire introduction page
 * The class has two button fields to display the back and next buttons
 * The class methods control which page the user goes to based on the button clicked
 * The class's static field that represents whether the user has completed the questionnaire
 */
public class IntroFragment extends Fragment implements AccountListener, DataListener {
    /**
     * Fields for the page's buttons and questionnaire completion status
     */
    private LinearLayout leftButton, rightButton;
    public static boolean questionnaireCompleted;

    /**
     * This method acts like a constructor, creates the page's initial view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return root view of the page to be displayed from the corresponding layout .xml file
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // gets/inflates the view from the layout file
        View root = inflater.inflate(R.layout.fragment_questionnaire_intro, container,
                false);

        // calls check to see if questionnaire has been done by user
        setQuestionnaireValue();

        // sets up the buttons
        leftButton = root.findViewById(R.id.bottomNavButtonLeft);
        rightButton = root.findViewById(R.id.bottomNavButtonRight);

        // sets up the navigation controller used by the buttons
        NavController navController = NavHostFragment.findNavController(IntroFragment.this);

        // sets up left click listener
        leftButton.setOnClickListener(v -> {
            // check is questionnaire has been completed before, if so, user can go to homepage
            if (IntroFragment.questionnaireCompleted) {
                navController.navigate(R.id.nav_home);
            } else {
                Toast.makeText(getContext(), "Please complete the Questionnaire",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // sets up right click listener
        rightButton.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Next to Warm", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_question_warm);
        });

        // returns the root view of the page
        return root;
    }

    /**
     * This method attempts to set the static field's value
     * Method attempts to log into the user's account to access data and set value
     */
    private void setQuestionnaireValue() {
        DatabaseAccess.readData(infoType.ANSWER, this);
    }

    /*
    private void setQuestionnaireValue() {
        // tries to log into the user's account
        LoginManager.attemptLogin("maxwang972@gmail.com", "B07Test", this);
    }

    **
     * This method based on the user, tries to access the database for the user's data
     * Method tries to read data, if unsuccessful, sets questionnaire completion status to false
     * @param user the FirebaseUser, or null if the login failed
     **

    public void onEmailLogin(FirebaseUser user) {
        // checks if the user is not null, if so, tries to access database
        if (user != null) {
            // access data
            DatabaseAccess.readData(infoType.ANSWER, this);  // issue
        } else {
            IntroFragment.questionnaireCompleted = false;
        }
    }*/

    /**
     * Method access Firebase data and sets the questionnaire completion status accordingly
     * @param type the type of data read
     * @param data a container for the data read. null if an error occurred
     */
    public void onDataReceived(infoType type, Object data) {
        // checks if database access was successful, if so, sets the questionnaire status variable
        if (type == infoType.ANSWER) {
            if (data == null) {
                IntroFragment.questionnaireCompleted = false;
            } else {
                HashMap<String, String> mapData = (HashMap<String, String>) data;
                // sets static field's value based on database information
                if (mapData.containsKey("Questionnaire done?") &&
                        mapData.get("Questionnaire done?").equals("true")) {
                    IntroFragment.questionnaireCompleted = true;
                } else {
                    IntroFragment.questionnaireCompleted = false;
                }
            }
        } else {
            IntroFragment.questionnaireCompleted = false;
        }
    }

    /**
     * This method acts as a deconstructor for the view
     * Destroys the view and sets button fields to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // null out view references for safety
        leftButton = null;
        rightButton = null;
    }
}
