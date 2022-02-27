//package com.example.messaging;
//
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class MessagingService {
//    public CompletableFuture<String> getChatRoom(final String email0, final String email1) throws ExecutionException, InterruptedException {
//        final CompletableFuture<String> r = new CompletableFuture<String>();
//
//        CompletableFuture.runAsync(
//            () -> {
//                final String userId0;
//                try {
//                    userId0 = getUserIdFromEmail(email0).get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                final String userId1;
//                try {
//                    userId1 = getUserIdFromEmail(email1).get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                final Task<QuerySnapshot> toFromTask = firestore.collection("chat_rooms")
//                    .whereEqualTo("to", userId0)
//                    .whereEqualTo("from", userId1)
//                    .get();
//
//                final QuerySnapshot querySnapshot;
//                try {
//                    querySnapshot = Tasks.await(toFromTask);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(!querySnapshot.isEmpty()) {
//                    r.complete((String)querySnapshot.getDocuments().get(0).get("id"));
//                    return;
//                }
//            }
//        );
//    }
//
//    private CompletableFuture<String> getUserIdFromEmail(final String email) {
//        final CompletableFuture<String> r = new CompletableFuture<String>();
//
//        firestore.collection("users").whereEqualTo("email", email).get()
//            .addOnCompleteListener(
//                (task) -> {
//                    final QuerySnapshot querySnapshot = task.getResult();
//
//                    if(querySnapshot.isEmpty()) {
//                        r.complete(null);
//                        return;
//                    }
//
//                    r.complete((String) querySnapshot.getDocuments().get(0).get("email"));
//                }
//            );
//
//        return r;
//    }
//
//
//    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//}
