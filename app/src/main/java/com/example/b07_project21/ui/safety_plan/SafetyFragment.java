package com.example.b07_project21.ui.safety_plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project21.MainActivity;
import com.example.b07_project21.R;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataAccess.AccountListener;
import dataAccess.DataListener;
import dataAccess.DatabaseAccess;
import dataAccess.LoginManager;
import dataAccess.Pair;
import dataAccess.infoType;

/**
 * This class controls the activity for the safety plan display page
 * The class has fields to keep track of the user's answers to the questionnaire
 * The class methods display relevant tips for the safety plan based on user data
 */
public class SafetyFragment extends Fragment implements AccountListener, DataListener {
    /**
     * Fields for questionnaire answers and for displaying the tips in a recycler view
     */
    private String situation, city, safe_room, live_status, children, code_word;
    private String abuse_situation, recording, contact;
    private String date, bag, stash, temp_status, temp_place;
    private String contacted, order_status, order_type, tools_status, tools_type;
    private String support_status;
    private List<String> tips = new ArrayList<>();
    private RecyclerView.Adapter<tipHolder> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and initialize the recycler view object
        View root = inflater.inflate(R.layout.fragment_safety, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.tips_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up navigation
        NavController navController = NavHostFragment.findNavController(SafetyFragment.this);

        // set up the adapter for the recycler view with new card-based layout
        adapter = new RecyclerView.Adapter<tipHolder>() {
            // called to get the recycler view a new tipHolder
            @NonNull
            @Override
            public tipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the new card-based layout
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_safety_tip, parent, false);
                return new tipHolder(view);
            }
            
            // displays data at specified position
            @Override
            public void onBindViewHolder(@NonNull tipHolder holder, int position) {
                // Set the tip text with bullet points
                String tipText = tips.get(position);
                holder.tipTitle.setText("Safety Tip " + (position + 1));
                
                // Add bullet points to the tip text
                String[] sentences = tipText.split("\\. ");
                StringBuilder bulletedText = new StringBuilder();
                for (String sentence : sentences) {
                    if (!sentence.trim().isEmpty()) {
                        bulletedText.append("• ").append(sentence.trim()).append("\n\n");
                    }
                }
                holder.tipText.setText(bulletedText.toString().trim());
            }
            
            // gets the size, how many tips need to be displayed
            @Override
            public int getItemCount() {
                return tips.size();
            }
        };

        // initialize recycler view by the adapter
        recyclerView.setAdapter(adapter);

        // calls for questionnaire values
        getValues();

        // returns the view
        return root;
    }

    /**
     * This method attempts to login for the user's data stored in Firebase
     * Method attempts to log into the user's account to access data
     */
    private void getValues() {
        DatabaseAccess.readData(infoType.ANSWER, this);
    }

    /**
     * Method gets the user's data and updates fields accordingly
     * @param type the type of data read
     * @param data a container for the data read. null if an error occurred
     */
    public void onDataReceived(infoType type, Object data) {
        if (type == infoType.ANSWER) {
            if (data != null) {
                HashMap<String, String> mapData = (HashMap<String, String>) data;

                if (!(mapData.containsKey("Questionnaire done?")) || !(mapData.get("Questionnaire done?").equals("true"))) {
                    return;
                }

                // warm-up
                situation = mapData.get("Which best describes your situation?");  // 1 (Still), 2 (Plan), 3 (Post)
                city = mapData.get("What city do you live in?");  // Toronto...
                safe_room = mapData.get("Which room in your home feels the safest if you need to get away quickly?");  // {Room}
                live_status = mapData.get("Who do you live with?");  // 1 (Family), 2 (Roommates), 3 (Alone), 4 (Partner)
                children = mapData.get("Do you have children?");  // "y", "n"
                code_word = mapData.get("What is your family's safety code word?");  // {word} or "NONE"

                // branch 1
                abuse_situation = mapData.get("Which form(s) of abuse are you experiencing?");  // "YNYN" [Physical | Emotional | Financial | Other] or "NONE"
                recording = mapData.get("Are you recording the incidents (dates, times, details)?");  // "y", "n" or "NONE"
                contact = mapData.get("Name one person you can call immediately in an emergency");  // {contact} or "NONE"

                // branch 2
                date = mapData.get("When do you plan to leave?");  // {date} or "NONE"
                bag = mapData.get("Do you have a go-bag packed with essentials?");  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
                stash = mapData.get("Where do you keep emergency cash or cards?");  // {location} or "NONE"
                temp_status = mapData.get("Have you identified a safe place to stay?");  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
                temp_place = mapData.get("Where do you plan to stay?");  // {place} or "NONE"

                // branch 3
                contacted = mapData.get("Has the abuser continued any contact?");  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
                order_status = mapData.get("Do you have a protection order? If yes, which type?");  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
                order_type = mapData.get("What legal order do you have?");  // {legal_order} or "NONE"
                tools_status = mapData.get("Are you using any safety tools (cameras, door alarms)?");  // 1 (yes), 2 (no) [Strings] or 0 (NONE)
                tools_type = mapData.get("What safety tools are you using?");  // {safety_tools} or "NONE"

                // follow
                support_status = mapData.get("Which type of support would help most right now?");  // 1 (Counselling), 2 (Legal), 3 (Financial), 4 (Co-parenting) [Strings]

                showTips();  // calls to show the relevant tips on the screen
            }
        }
    }

    /**
     * Method tries to display relevant tips for the safety plan based on Firebase data
     */
    private void showTips() {
        try {
            // gets the JSON file data
            JSONObject json = loadJSONFromAsset("questions.json");

            // warm-up tips
            JSONArray tipsArray1 = json.getJSONArray("id 1");
            JSONObject tipObj1 = tipsArray1.getJSONObject(Integer.parseInt(situation) - 1);
            String tipText1 = tipObj1.getString("tip " + Integer.parseInt(situation));

            JSONArray tipsArray2 = json.getJSONArray("id 2");
            JSONObject tipObj2 = tipsArray2.getJSONObject(0);
            String tipText2 = tipObj2.getString("tip");
            tipText2 = tipText2.replace("{city}", city);

            JSONArray tipsArray3 = json.getJSONArray("id 3");
            JSONObject tipObj3 = tipsArray3.getJSONObject(0);
            String tipText3 = tipObj3.getString("tip");
            tipText3 = tipText3.replace("{safe_room}", safe_room);

            JSONArray tipsArray4 = json.getJSONArray("id 4");
            String tipText4 = "";
            if (live_status.equals("1")) {
                JSONObject tipObj4 = tipsArray4.getJSONObject(0);
                tipText4 = tipObj4.getString("tip 1");
                tipText4 = tipText4.replace("{family/roommates}", "family");
            } else if (live_status.equals("2")) {
                JSONObject tipObj4 = tipsArray4.getJSONObject(0);
                tipText4 = tipObj4.getString("tip 1");
                tipText4 = tipText4.replace("{family/roommates}", "roommates");
            } else if (live_status.equals("3")) {
                JSONObject tipObj4 = tipsArray4.getJSONObject(1);
                tipText4 = tipObj4.getString("tip 2");
            } else if (live_status.equals("4")) {
                JSONObject tipObj4 = tipsArray4.getJSONObject(2);
                tipText4 = tipObj4.getString("tip 3");
                tipText4 = tipText4.replace("{safe_room}", safe_room);
            }

            JSONArray tipsArray5 = json.getJSONArray("id 5");
            String tipText5 = "";
            if (children.equals("y")) {
                JSONObject tipObj5 = tipsArray5.getJSONObject(0);
                tipText5 = tipObj5.getString("tip 1");
                tipText5 = tipText5.replace("{code_word}", code_word);
            } else if (children.equals("n")) {
                JSONObject tipObj5 = tipsArray5.getJSONObject(1);
                tipText5 = tipObj5.getString("tip 2");
            }

            // branch 1
            JSONArray tipsArray7 = json.getJSONArray("id 7");
            String tipText7 = "";
            String abuse_types = "";
            if (!(abuse_situation.equals("NONE"))) {
                if (abuse_situation.charAt(0) == 'Y') {
                    abuse_types += "physical ";
                }
                if (abuse_situation.charAt(1) == 'Y') {
                    abuse_types += " emotional ";
                }
                if (abuse_situation.charAt(2) == 'Y') {
                    abuse_types += " financial ";
                }
                if (abuse_situation.charAt(3) == 'Y') {
                    abuse_types += " other";
                }
                abuse_types = abuse_types.replace("  ", ", ");
                if (abuse_types.length() >= 1 && abuse_types.charAt(abuse_types.length()-1) != ' ') {
                    abuse_types += " ";
                }

                JSONObject tipObj7 = tipsArray7.getJSONObject(0);
                tipText7 = tipObj7.getString("tip");
                tipText7 = tipText7.replace("{abuse_type} ", abuse_types);
            }

            JSONArray tipsArray8 = json.getJSONArray("id 8");
            String tipText8 = "";
            if (!(recording.equals("NONE"))) {
                if (recording.equals("y")) {
                    JSONObject tipObj8 = tipsArray8.getJSONObject(0);
                    tipText8 = tipObj8.getString("tip 1");
                } else if (recording.equals("n")) {
                    JSONObject tipObj8 = tipsArray8.getJSONObject(1);
                    tipText8 = tipObj8.getString("tip 2");
                }
            }

            JSONArray tipsArray9 = json.getJSONArray("id 9");
            String tipText9 = "";
            if (!(contact.equals("NONE"))) {
                JSONObject tipObj9 = tipsArray9.getJSONObject(0);
                tipText9 = tipObj9.getString("tip");
                tipText9 = tipText9.replace("{contact_name}", contact);
            }

            // branch 2
            JSONArray tipsArray10 = json.getJSONArray("id 10");
            String tipText10 = "";
            if (!(date.equals("NONE"))) {
                JSONObject tipObj10 = tipsArray10.getJSONObject(0);
                tipText10 = tipObj10.getString("tip");
                tipText10 = tipText10.replace("{leave_timing}", date);
            }

            JSONArray tipsArray11 = json.getJSONArray("id 11");
            String tipText11 = "";
            if (!(bag.equals("0"))) {
                if (bag.equals("1")) {
                    JSONObject tipObj11 = tipsArray11.getJSONObject(0);
                    tipText11 = tipObj11.getString("tip 1");
                } else if (bag.equals("2")) {
                    JSONObject tipObj11 = tipsArray11.getJSONObject(1);
                    tipText11 = tipObj11.getString("tip 2");
                }
            }

            JSONArray tipsArray12 = json.getJSONArray("id 12");
            String tipText12 = "";
            if (!(stash.equals("NONE"))) {
                JSONObject tipObj12 = tipsArray12.getJSONObject(0);
                tipText12 = tipObj12.getString("tip");
                tipText12 = tipText12.replace("{money_location}", stash);
            }

            JSONArray tipsArray13 = json.getJSONArray("id 13");
            String tipText13 = "";
            if (!(temp_status.equals("0"))) {
                if (temp_status.equals("1")) {
                    JSONObject tipObj13 = tipsArray13.getJSONObject(0);
                    tipText13 = tipObj13.getString("tip 1");
                    tipText13 = tipText13.replace("{temp_shelter}", temp_place);
                } else if (temp_status.equals("2")) {
                    JSONObject tipObj13 = tipsArray13.getJSONObject(1);
                    tipText13 = tipObj13.getString("tip 2");
                }
            }

            // branch 3
            JSONArray tipsArray15 = json.getJSONArray("id 15");
            String tipText15 = "";
            if (!(contacted.equals("0"))) {
                if (contacted.equals("1")) {
                    JSONObject tipObj15 = tipsArray15.getJSONObject(0);
                    tipText15 = tipObj15.getString("tip 1");
                } else if (contacted.equals("2")) {
                    JSONObject tipObj15 = tipsArray15.getJSONObject(1);
                    tipText15 = tipObj15.getString("tip 2");
                }
            }

            JSONArray tipsArray16 = json.getJSONArray("id 16");
            String tipText16 = "";
            if (!(order_status.equals("0"))) {
                if (order_status.equals("1")) {
                    JSONObject tipObj16 = tipsArray16.getJSONObject(0);
                    tipText16 = tipObj16.getString("tip 1");
                    tipText16 = tipText16.replace("{legal_order}", order_type);
                } else if (order_status.equals("2")) {
                    JSONObject tipObj16 = tipsArray16.getJSONObject(1);
                    tipText16 = tipObj16.getString("tip 2");
                }
            }

            JSONArray tipsArray18 = json.getJSONArray("id 18");
            String tipText18 = "";
            if (!(tools_status.equals("0"))) {
                if (tools_status.equals("1")) {
                    JSONObject tipObj18 = tipsArray18.getJSONObject(0);
                    tipText18 = tipObj18.getString("tip 1");
                    tipText18 = tipText18.replace("{equipment}", tools_type);
                } else if (tools_status.equals("2")) {
                    JSONObject tipObj18 = tipsArray18.getJSONObject(1);
                    tipText18 = tipObj18.getString("tip 2");
                }
            }

            // follow
            JSONArray tipsArray20 = json.getJSONArray("id 20");
            JSONObject tipObj20 = tipsArray20.getJSONObject(0);
            String tipText20 = tipObj20.getString("tip");
            String support_name;
            if (support_status.equals("1")) {
                support_name = "counselling";
            } else if (support_status.equals("2")) {
                support_name = "legal aid";
            } else if (support_status.equals("3")) {
                support_name = "financial guidance";
            } else {
                support_name = "co-parenting resources";
            }
            tipText20 = tipText20.replace("{support_choice}", support_name);

            tips.clear(); // clear in case it's a reload

            // add items
            tips.add(tipText1);
            tips.add(tipText2);
            tips.add(tipText3);
            tips.add(tipText4);
            tips.add(tipText5);
            if (situation.equals("1")) {
                tips.add(tipText7);
                tips.add(tipText8);
                tips.add(tipText9);
            } else if (situation.equals("2")) {
                tips.add(tipText10);
                tips.add(tipText11);
                tips.add(tipText12);
                tips.add(tipText13);
            } else if (situation.equals("3")) {
                tips.add(tipText15);
                tips.add(tipText16);
                tips.add(tipText18);
            }
            tips.add(tipText20);

            // update the adapter to update the screen
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            // catches any unchecked errors during runtime
            Log.e("SafetyFragment", "Error loading tips", e);
        }
    }

    /**
     * This class is used to display tips to the recycler view
     * Inherits from RecyclerView.ViewHolder superclass
     */
    static class tipHolder extends RecyclerView.ViewHolder {
        TextView tipTitle;  // displays each tip title
        TextView tipText;   // displays each tip content
        
        // receives the itemView and assigns it accordingly
        public tipHolder(@NonNull View itemView) {
            super(itemView);
            tipTitle = itemView.findViewById(R.id.tipTitle);
            tipText = itemView.findViewById(R.id.tipText);
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
        // open file from the assets directory
        InputStream is = getContext().getAssets().open(filename);
        int size = is.available();  // determine total file size
        byte[] buffer = new byte[size];  // byte buffer of that size
        is.read(buffer);  // read file content now into the buffer's memory
        is.close();  // close the input stream
        return new JSONObject(new String(buffer, "UTF-8"));  // return JSONObject
    }

    /**
     * This method acts as a deconstructor for the view
     * Destroys the view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
