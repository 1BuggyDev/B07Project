package com.example.b07_project21.ui.reminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project21.R;
import com.google.android.gms.common.util.BiConsumer;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class ReminderListFragment extends Fragment {
    private final CollectionReference col = FirebaseFirestore.getInstance()
            .collection("users")
            .document("devUser123")
            .collection("reminders");
    private final ReminderAdapter adapter = new ReminderAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(view);
        setupSwipeToDelete();
        setupAdapterActions();
        setupFirestoreListener();
        setupFab(view);
    }

    private void setupRecyclerView(View root) {
        RecyclerView rv = root.findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                Reminder r = adapter.getItemAt(pos);
                ReminderScheduler.cancel(requireContext(), r);
                col.document(r.getId()).delete()
                        .addOnSuccessListener(a ->
                                Toast.makeText(requireContext(), "Reminder deleted", Toast.LENGTH_SHORT)
                                        .show())
                        .addOnFailureListener(e -> {
                            adapter.notifyItemChanged(pos);
                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT)
                                    .show();
                        });
            }
        }).attachToRecyclerView(
                getView().findViewById(R.id.list)
        );
    }

    private void setupAdapterActions() {
        adapter.setOnItemActionListener(new ReminderAdapter.OnItemActionListener() {
            @Override public void onEdit(Reminder r) {
                pickDateTime(r.getTriggerAt(), newTime -> handleEdit(r, newTime));
            }
            @Override public void onDelete(Reminder r) {
                ReminderScheduler.cancel(requireContext(), r);
                col.document(r.getId()).delete();
            }
        });
    }

    private void setupFirestoreListener() {
        col.orderBy("triggerAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snaps, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Load failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Reminder> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snaps.getDocuments()) {
                        Reminder r = ds.toObject(Reminder.class);
                        if (r != null) list.add(r);
                    }
                    adapter.submitList(list);
                    adapter.notifyDataSetChanged(); // ensure rebind
                });
    }

    private void setupFab(View root) {
        root.findViewById(R.id.fab_add).setOnClickListener(v ->
                pickDateTime(System.currentTimeMillis(), this::handleAdd)
        );
    }

    /**
     * Opens date & time pickers, applies timezone correction, and returns timestamp.
     */
    private void pickDateTime(long initialEpoch, DateTimeCallback cb) {
        MaterialDatePicker<Long> dp = MaterialDatePicker.Builder.datePicker()
                .setSelection(initialEpoch)
                .build();
        dp.addOnPositiveButtonClickListener(epoch -> {
            long localMid = epoch - TimeZone.getDefault().getOffset(epoch);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(localMid);

            MaterialTimePicker tp = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(cal.get(Calendar.HOUR_OF_DAY))
                    .setMinute(cal.get(Calendar.MINUTE))
                    .build();
            tp.addOnPositiveButtonClickListener(t -> {
                cal.set(Calendar.HOUR_OF_DAY, tp.getHour());
                cal.set(Calendar.MINUTE, tp.getMinute());
                cal.set(Calendar.SECOND, 0);
                long chosen = cal.getTimeInMillis();
                if (chosen <= System.currentTimeMillis()) {
                    Toast.makeText(requireContext(),
                                    "Time must be in the future", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                cb.onDateTimeChosen(chosen);
            });
            tp.show(getParentFragmentManager(), "time");
        });
        dp.show(getParentFragmentManager(), "date");
    }

    /**
     * Generic check for duplicate reminders.
     * Skip the reminder with skipId (use null to skip none).
     * Extend this method for future criteria beyond timestamp.
     */
    private boolean isDuplicateReminder(@Nullable String skipId, long timestamp) {
        for (Reminder existing : adapter.getCurrentList()) {
            boolean sameTime = existing.getTriggerAt() == timestamp;
            boolean skip = (skipId != null && existing.getId().equals(skipId));
            if (sameTime && !skip) {
                return true;
            }
        }
        return false;
    }

    private void handleAdd(long timestamp) {
        if (isDuplicateReminder(null, timestamp)) {
            Toast.makeText(requireContext(),
                            "A reminder at that exact time already exists", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        promptText(timestamp, (ts, msg) -> {
            Reminder r = new Reminder(UUID.randomUUID().toString(), ts, msg);
            col.document(r.getId()).set(r)
                    .addOnSuccessListener(a -> {
                        ReminderScheduler.schedule(requireContext(), r);
                        Toast.makeText(requireContext(), "Reminder saved", Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Save failed", Toast.LENGTH_SHORT)
                                    .show()
                    );
        });
    }

    private void handleEdit(Reminder original, long newTime) {
        if (isDuplicateReminder(original.getId(), newTime)) {
            Toast.makeText(requireContext(),
                            "Another reminder is already set for that time", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ReminderScheduler.cancel(requireContext(), original);
        promptText(newTime, (ts, msg) -> {
            original.setTriggerAt(ts);
            original.setMessage(msg);
            col.document(original.getId())
                    .update("triggerAt", ts, "message", msg)
                    .addOnSuccessListener(a -> {
                        ReminderScheduler.schedule(requireContext(), original);
                        Toast.makeText(requireContext(), "Reminder updated", Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT)
                                    .show()
                    );
        });
    }

    private interface DateTimeCallback {
        void onDateTimeChosen(long timestamp);
    }

    /** Prompts for a custom message, then runs cb(timestamp, message) */
    private void promptText(long ts, BiConsumer<Long,String> cb) {
        EditText input = new EditText(requireContext());
        input.setHint("Notification text (optional)");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add message")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> cb.accept(ts,
                        input.getText() != null ? input.getText().toString().trim() : ""))
                .setNegativeButton("Cancel", null)
                .show();
    }
}