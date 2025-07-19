package com.example.b07_project21;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
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

    public ListenerRegistration listenAll(OnSuccessListener<List<Reminder>> onChange,
                                          OnFailureListener onError) {
        return col
                .orderBy("triggerAt")  // so they’re always sorted
                .addSnapshotListener((snap, err) -> {
                    if (err != null) {
                        onError.onFailure(err);
                        return;
                    }
                    List<Reminder> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snap) {
                        Reminder r = ds.toObject(Reminder.class);
                        if (r != null) list.add(r);
                    }
                    onChange.onSuccess(list);
                });
    }
}
