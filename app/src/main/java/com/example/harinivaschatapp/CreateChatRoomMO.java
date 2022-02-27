package com.example.harinivaschatapp;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CreateChatRoomMO {
    public CreateChatRoomMO(String userId, String chattingWithUserId) {
        this.userId = userId;
        this.chattingWithUserId = chattingWithUserId;
    }

    public CompletableFuture<String> execute() {
        return addRoomToDatabase().thenApply(
            (result) -> {
                try {
                    addChatRoomToUsersChatList(userId, chattingWithUserId).get();
                    addChatRoomToUsersChatList(chattingWithUserId, userId).get();
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return chatRoomId;
            }
        );
    }

    private CompletableFuture<Void> addRoomToDatabase() {
        return CompletableFuture.runAsync(
            () -> {
                try {
                    final DocumentReference documentReference = Tasks.await(
                        firestore.
                            collection("chat_rooms")
                            .add(new HashMap<String, Object>())
                    );

                    chatRoomId = documentReference.getId();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }

    private CompletableFuture<Void> addChatRoomToUsersChatList(
        final String userId, final String chattingWithUserId
    ) {
        return CompletableFuture.runAsync(
            () -> {
                final Map<String, Object> databaseData = new HashMap<String, Object>();
                databaseData.put("chat_room", chatRoomId);

                try {
                    Tasks.await(
                        firestore
                            .collection("users_chat_rooms_list")
                            .document(userId)
                            .collection("chat_rooms")
                            .document(chattingWithUserId)
                            .set(databaseData)
                    );
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        );
    }


    private String chatRoomId;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final String userId;
    private final String chattingWithUserId;
}
