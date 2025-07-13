package com.example.b07_project21.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.b07_project21.R;

public class CloseFragment extends Fragment {
    private LinearLayout rightButton, leftButton;

    private int situation, live_status;
    private String city, safe_room, children, code_word;
    private String abuse_situation, recording, contact;
    private int bag, temp_status;
    private String date, stash, temp_place;
    private int have_contacted, order_status, tools_status;
    private String order_type, tools_type;
    private int support_status;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_questionnaire_close, container, false);

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

    // update when api code added
    private void updateData() {
        // update data to FireBase
    }

    private Bundle reBundle() {
        Bundle b = new Bundle();
        b.putInt("action", 0);

        b.putInt("situation", situation);
        b.putString("selected_city", city);
        b.putString("safe_room", safe_room);
        b.putInt("live_with", live_status);
        b.putString("children", children);
        b.putString("code_word", code_word);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Null out view references for safety
        leftButton = null;
        rightButton = null;
    }
}