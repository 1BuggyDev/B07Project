package com.example.b07_project21;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ReminderListFragment extends Fragment {
    private ReminderAdapter adapter = new ReminderAdapter();

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b) {
        return inf.inflate(R.layout.fragment_reminder_list, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);

        // 1) set up RecyclerView + adapter
        RecyclerView rv = v.findViewById(R.id.list);
        ReminderAdapter adapter = new ReminderAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // 2) load existing reminders
        new ReminderRepository()
                .listenAll((snapshots, err) -> {
                    if (err != null) {
                        Log.e("REM", "listen failed", err);
                        return;
                    }
                    List<Reminder> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snapshots.getDocuments()) {
                        Reminder r = ds.toObject(Reminder.class);
                        if (r != null) list.add(r);
                    }
                    adapter.submitList(list);
                });

        // 3) wire up the “+” button
        v.findViewById(R.id.fab_add).setOnClickListener(x -> {
            // (a) date picker
            MaterialDatePicker<Long> date = MaterialDatePicker.Builder
                    .datePicker().build();
            date.addOnPositiveButtonClickListener(epoch -> {
                // (b) time picker
                MaterialTimePicker time = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H).build();
                time.addOnPositiveButtonClickListener(y -> {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(epoch);
                    c.set(Calendar.HOUR_OF_DAY, time.getHour());
                    c.set(Calendar.MINUTE, time.getMinute());
                    c.set(Calendar.SECOND, 0);

                    // (c) build the reminder
                    Reminder r = new Reminder(
                            UUID.randomUUID().toString(),
                            c.getTimeInMillis()
                    );

                    // (d) save + schedule + add to UI
                    new ReminderRepository()
                            .add(r)
                            .addOnSuccessListener( unused -> {
                                ReminderScheduler.schedule(requireContext(), r);
                                adapter.addItem(r);
                            })
                            .addOnFailureListener(err ->
                                    Log.e("REM", "couldn't save", err));
                });
                time.show(getParentFragmentManager(), "time");
            });
            date.show(getParentFragmentManager(), "date");
        });
    }

}

