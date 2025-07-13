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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ReminderListFragment extends Fragment {
    // Firestore collection (using fake UID for now)
    private final CollectionReference col = FirebaseFirestore.getInstance()
            .collection("users")
            .document("devUser123")
            .collection("reminders");

    private final ReminderAdapter adapter = new ReminderAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle saved) {
        super.onViewCreated(v, saved);

        // 1) RecyclerView + adapter
        RecyclerView rv = v.findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // 2) Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override public boolean onMove(@NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            @NonNull RecyclerView.ViewHolder t) {
                return false;
            }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Reminder r = adapter.getItemAt(pos);
                // cancel alarm
                ReminderScheduler.cancel(requireContext(), r);
                // delete in Firestore
                col.document(r.getId()).delete()
                        .addOnSuccessListener(a ->
                                Toast.makeText(requireContext(),
                                        "Reminder deleted",
                                        Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e -> {
                            adapter.notifyItemChanged(pos);
                            Toast.makeText(requireContext(),
                                    "Delete failed",
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        }).attachToRecyclerView(rv);

        // 3) Long-press to edit
        adapter.setOnItemActionListener(new ReminderAdapter.OnItemActionListener() {
            @Override public void onEdit(Reminder r) {
                // a) Date picker pre-set
                MaterialDatePicker<Long> dp = MaterialDatePicker.Builder
                        .datePicker()
                        .setSelection(r.getTriggerAt())
                        .build();
                dp.addOnPositiveButtonClickListener(epoch -> {
                    // b) Time picker pre-set
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(r.getTriggerAt());
                    MaterialTimePicker tp = new MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(cal.get(Calendar.HOUR_OF_DAY))
                            .setMinute(cal.get(Calendar.MINUTE))
                            .build();
                    tp.addOnPositiveButtonClickListener(t -> {
                        // cancel old alarm
                        ReminderScheduler.cancel(requireContext(), r);
                        // update model
                        cal.setTimeInMillis(epoch);
                        cal.set(Calendar.HOUR_OF_DAY, tp.getHour());
                        cal.set(Calendar.MINUTE, tp.getMinute());
                        cal.set(Calendar.SECOND, 0);
                        r.setTriggerAt(cal.getTimeInMillis());
                        // persist & reschedule
                        col.document(r.getId())
                                .update("triggerAt", r.getTriggerAt())
                                .addOnSuccessListener(a -> {
                                    ReminderScheduler.schedule(requireContext(), r);
                                    Toast.makeText(requireContext(),
                                            "Reminder updated",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(),
                                            "Update failed",
                                            Toast.LENGTH_SHORT).show();
                                });
                    });
                    tp.show(getParentFragmentManager(), "time");
                });
                dp.show(getParentFragmentManager(), "date");
            }

            @Override public void onDelete(Reminder r) {
                // alternative delete trigger (besides swipe)
                ReminderScheduler.cancel(requireContext(), r);
                col.document(r.getId()).delete();
            }
        });

        // 4) Real-time listener
        col.orderBy("triggerAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snaps,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(requireContext(),
                                    "Load failed",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<Reminder> list = new ArrayList<>();
                        for (DocumentSnapshot ds : snaps.getDocuments()) {
                            Reminder r = ds.toObject(Reminder.class);
                            if (r != null) list.add(r);
                        }
                        adapter.submitList(list);
                    }
                });

        // 5) FAB → Add new reminder
        v.findViewById(R.id.fab_add).setOnClickListener(x -> {
            MaterialDatePicker<Long> dp = MaterialDatePicker.Builder
                    .datePicker().build();
            dp.addOnPositiveButtonClickListener(epoch -> {
                MaterialTimePicker tp = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .build();
                tp.addOnPositiveButtonClickListener(t -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(epoch);
                    cal.set(Calendar.HOUR_OF_DAY, tp.getHour());
                    cal.set(Calendar.MINUTE, tp.getMinute());
                    cal.set(Calendar.SECOND, 0);
                    Reminder r = new Reminder(
                            UUID.randomUUID().toString(),
                            cal.getTimeInMillis()
                    );
                    col.document(r.getId()).set(r)
                            .addOnSuccessListener(a -> {
                                ReminderScheduler.schedule(requireContext(), r);
                                Toast.makeText(requireContext(),
                                        "Reminder saved",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(),
                                        "Save failed",
                                        Toast.LENGTH_SHORT).show();
                            });
                });
                tp.show(getParentFragmentManager(), "time");
            });
            dp.show(getParentFragmentManager(), "date");
        });
    }
}