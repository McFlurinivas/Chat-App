package com.example.harinivaschatapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class Message {
    public Message(String id, String from, String content, Timestamp timestamp) {
        this.id = id;
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    static public Message fromDocumentSnapshot(final DocumentSnapshot documentSnapshot) {
        return new Message(
            documentSnapshot.getId(),
            (String) documentSnapshot.get("from"),
            (String) documentSnapshot.get("content"),
            (Timestamp) documentSnapshot.get("time")
        );
    }


    public final String id;
    public final String from;
    public final String content;
    public final Timestamp timestamp;
}
