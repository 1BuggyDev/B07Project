package com.example.b07_project21;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReminderRepository {
    // this is what it should be after login implementation, it's a sample user so far
    /*private final CollectionReference col =
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("reminders");*/

    String fakeUid = "devUser123";
    private final CollectionReference col = FirebaseFirestore.getInstance()
            .collection("users")
      .document(fakeUid)
      .collection("reminders");

    /**
     * Fetches *all* reminders once, and returns a Task<Void>
     */
    public Task<Void> add(Reminder r) {
        return col.document(r.getId()).set(r);
    }

    /** Fetch all reminders once. */
    public void getAll(OnSuccessListener<List<Reminder>> onSuccess) {
        col.get()
                .addOnSuccessListener(snapshot -> {
                    List<Reminder> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snapshot) {
                        Reminder r = ds.toObject(Reminder.class);
                        if (r != null) list.add(r);
                    }
                    onSuccess.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    Log.e("REM", "fetch failed", e);
                });
    }

    public ListenerRegistration listenAll(
            EventListener<QuerySnapshot> listener) {
        return col.orderBy("triggerAt", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }

    // will add update() and delete() later
}
