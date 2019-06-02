package com.example.studygroups;

import android.content.Intent;
import android.media.MediaRouter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int RC_SIGN_IN = 123;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("User Profile");
    String username;
    String display_name;
    static FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            username = user.getEmail();
            display_name = user.getDisplayName();
            UserProfile current_user = null;
            Query query = collection.whereEqualTo("username", username);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    boolean found = false;
                    if (task.isSuccessful()) {
                        UserProfile current_user = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            current_user = document.toObject(UserProfile.class);
                            found = true;
                        }
                        Intent intent = new Intent(MainActivity.this, PostLoginActivity.class);
                        intent.putExtra("original_activity", "main");
                        intent.putExtra("current_user", current_user);
                        startActivity(intent);
                    }
                }
                });
            return;
        }

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        setContentView(R.layout.activity_main);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                username = user.getEmail();
                display_name = user.getDisplayName();
                Query query = collection.whereEqualTo("username", username);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean found = false;
                        if (task.isSuccessful()) {
                            UserProfile current_user = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                current_user = document.toObject(UserProfile.class);
                                found = true;
                            }

                            if(!found) {
                                current_user = new UserProfile(username, display_name);
                                collection.add(current_user);
                            }


                            Intent intent = new Intent(MainActivity.this, PostLoginActivity.class);
                            intent.putExtra("original_activity", "main");
                            intent.putExtra("current_user", current_user);
                            startActivity(intent);
                        }

                    }
                });
            }
        }
    }


}
