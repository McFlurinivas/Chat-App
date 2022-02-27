package com.example.harinivaschatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.authentication.AuthenticationService;
import com.example.harinivaschatapp.databinding.ActivityMessageScreenBinding;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.auth.User;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MessageScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMessageScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatRoomId = getIntent().getStringExtra("ChatRoomId");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.messagesList.setAdapter(
            new MessagesListAdapter(this, auth.getCurrentUser().getEmail(), new ArrayList<Message>())
        );
        binding.messagesList.setLayoutManager(new LinearLayoutManager(this));

        listenForMessages();

        binding.sendButton.setOnClickListener(
            (view) -> {
                CompletableFuture.runAsync(
                    () -> {
                        final String message;
                        try {
                            message = sendMessage(binding.messageToSendEditText.getText().toString());
                        }
                        catch (Exception e) {
                            Log.d("CustomLog", "Send message failed");

                            runOnUiThread(
                                () -> Toast.makeText(this, "Send message failed", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        Log.d("CustomLog", "Message with id " + message + " sent");
                        runOnUiThread(() -> Toast.makeText(this, "Sent message successfully", Toast.LENGTH_SHORT).show());
                    }
                );
            }
        );
    }

    private void listenForMessages() {
        messageListenerRegistration = firestore
        .collection("chat_rooms")
        .document(chatRoomId)
        .collection("messages")
        .orderBy("time")
        .addSnapshotListener(
            (querySnapshot, error) -> {
                final List<Message> messages = new ArrayList<Message>();
                for(DocumentSnapshot documents : querySnapshot.getDocuments())
                    messages.add(Message.fromDocumentSnapshot(documents));

                MessagesListAdapter adapter = (MessagesListAdapter) binding.messagesList.getAdapter();
                adapter.messages = messages;
                adapter.notifyDataSetChanged();

                Log.d("CustomLog", "Received new messages, count = " + adapter.getItemCount());
            }
        );
    }

    private String sendMessage(final String messageToSend) throws ExecutionException, InterruptedException {
        final Map<String, Object> databaseMessage = new HashMap<String, Object>();
        databaseMessage.put("from", auth.getCurrentUser().getEmail());
        databaseMessage.put("content", messageToSend);
        databaseMessage.put("time", Timestamp.now());

        final DocumentReference documentReference = Tasks.await(
            firestore.collection("chat_rooms")
                .document(chatRoomId)
                .collection("messages")
                .add(databaseMessage)
        );

        return documentReference.getId();
    }

    @Override
    protected void onDestroy() {
        messageListenerRegistration.remove();
        messageListenerRegistration = null;

        super.onDestroy();
    }




    private ActivityMessageScreenBinding binding;

    private ListenerRegistration messageListenerRegistration;

    private String chatRoomId;
    private String chattingWithUserId;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final AuthenticationService auth = new AuthenticationService();

}