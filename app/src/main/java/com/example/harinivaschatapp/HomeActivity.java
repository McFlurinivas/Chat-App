package com.example.harinivaschatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.authentication.AuthenticationService;
import com.example.harinivaschatapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.openMessageScreenButton.setOnClickListener(
            (view) -> {
                final FirebaseUser user = authenticationService.getCurrentUser();
                if(binding.userToMessageEditText.getText().toString().equals(user.getEmail())) {
                    Log.e("CustomLog", "User entered his own email");
                    Toast.makeText(this, "Dont enter your email :)", Toast.LENGTH_LONG).show();

                    return;
                }

                final String chattingWithUserId;
                try {
                    chattingWithUserId = getChattingWithUsersUserId().get();
                }
                catch(Exception e) {
                    Log.e("CustomLog", "Error while checking if chatting with user exists");
                    e.printStackTrace();
                    return;
                }

                if(chattingWithUserId == null) {
                    Log.e("CustomLog", "User doesn't exist");
                    Toast.makeText(this, "User doesn't exist :)", Toast.LENGTH_LONG).show();
                    return;
                }

                final String chatRoom;
                try {
                    chatRoom = getChatRoom().get();
                }
                catch(Exception e) {
                    Log.e("CustomLog", "Error getting chatroom");
                    e.printStackTrace();
                    return;
                }

                openMessageScreen(chatRoom);
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if(itemId == R.id.action_logout) {
            authenticationService.logout();

            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openMessageScreen(final String chatRoomId) {
        final Intent intent = new Intent(this, MessageScreenActivity.class);
        intent.putExtra("ChatRoomId", chatRoomId);
        startActivity(intent);
    }

    private CompletableFuture<String> getChatRoom() {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    String chatRoomId = checkChatRoomExists().get();

                    if(chatRoomId != null)
                        return chatRoomId;

                    Log.d("CustomLog", "Creating new chat room");
                    return createChatRoom().get();
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        );
    }

    private CompletableFuture<String> checkChatRoomExists() {
        return CompletableFuture.supplyAsync(
            () -> {
                final String chattingWithUserId;
                final DocumentSnapshot documentSnapshot;
                try {
                    chattingWithUserId = getChattingWithUsersUserId().get();

                    documentSnapshot = Tasks.await(
                        firestore.collection("users_chat_rooms_list")
                            .document(authenticationService.getCurrentUser().getUid())
                            .collection("chat_rooms")
                            .document(chattingWithUserId)
                            .get()
                    );
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }

                if(!documentSnapshot.exists())
                    return null;

                return (String) documentSnapshot.get("chat_room");
            }
        );
    }

    private CompletableFuture<String> createChatRoom() throws ExecutionException, InterruptedException {
        final CreateChatRoomMO createChatRoomMO = new CreateChatRoomMO(
            authenticationService.getCurrentUser().getUid(),
            getChattingWithUsersUserId().get()
        );

        return createChatRoomMO.execute();
    }

    private CompletableFuture<String> getChattingWithUsersUserId() {
        return CompletableFuture.supplyAsync(
            () -> {
                logCurrentThread("getChattingWithUsersUserId");

                final Task<QuerySnapshot> findUserTask = firestore.collection("users")
                    .whereEqualTo("email", binding.userToMessageEditText.getText().toString())
                    .get();

                final QuerySnapshot querySnapshot;
                try {
                     querySnapshot = Tasks.await(findUserTask);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                if(querySnapshot.isEmpty())
                    return null;

                return querySnapshot.getDocuments().get(0).getId();
            }
        );
    }

    private void logCurrentThread(final String codeLocation) {
        final Thread currentThread = Thread.currentThread();
        Log.d("CustomLog", codeLocation + " in thread " + currentThread.getName());
    }



    private ActivityHomeBinding binding;

    private final AuthenticationService authenticationService = new AuthenticationService();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
}