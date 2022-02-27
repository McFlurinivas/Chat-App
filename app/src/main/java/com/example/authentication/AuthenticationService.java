package com.example.authentication;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class AuthenticationService {
    public FirebaseUser signup(@NonNull final String email, @NonNull final String password) throws ExecutionException, InterruptedException {
         final AuthResult authResult = Tasks.await(auth.createUserWithEmailAndPassword(email, password));
         final FirebaseUser user = authResult.getUser();

        final Map<String, Object> documentData = new HashMap<String, Object>();
        documentData.put("email", user.getEmail());

        final CollectionReference collection = firestore.collection("users");
        final DocumentReference documentReference = collection.document(user.getUid());

        documentReference.set(documentData);

        return user;
    }

    public FirebaseUser login(@NonNull final String email, @NonNull final String password) throws ExecutionException, InterruptedException {
        final AuthResult authResult = Tasks.await(auth.signInWithEmailAndPassword(email, password));

        return authResult.getUser();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }


    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
}
