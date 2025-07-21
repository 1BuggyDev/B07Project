package com.example.b07_project21.ui.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer for reminders. All Firestore operations go through here.
 */
public class ReminderRepository {
    private final CollectionReference col;

    public interface ListCallback {
        void onResult(List<Reminder> list);
        void onError(Exception e);
    }
    public interface OpCallback {
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * @param userId your current user's document ID
     */
    public ReminderRepository(@NonNull String userId) {
        col = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reminders");
    }

    /** Listen for all reminders, ordered ascending by triggerAt. */
    public void listenAll(@NonNull ListCallback cb) {
        col.orderBy("triggerAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snaps, e) -> {
                    if (e != null) {
                        cb.onError(e);
                        return;
                    }
                    List<Reminder> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snaps.getDocuments()) {
                        Reminder r = ds.toObject(Reminder.class);
                        if (r != null) list.add(r);
                    }
                    cb.onResult(list);
                });
    }

    /** Add a new reminder document. */
    public void add(@NonNull Reminder r, @NonNull OpCallback cb) {
        col.document(r.getId()).set(r)
                .addOnSuccessListener(a -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    /** Update an existing reminder’s triggerAt (and later other fields). */
    public void update(@NonNull Reminder r, @NonNull OpCallback cb) {
        col.document(r.getId())
                .update("triggerAt", r.getTriggerAt())
                .addOnSuccessListener(a -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    /** Delete a reminder. cb may be null if you don’t need callbacks. */
    public void delete(@NonNull Reminder r, @Nullable OpCallback cb) {
        col.document(r.getId()).delete()
                .addOnSuccessListener(a -> { if (cb!=null) cb.onSuccess(); })
                .addOnFailureListener(e -> { if (cb!=null) cb.onError(e); });
    }
}