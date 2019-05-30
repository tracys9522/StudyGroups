package com.example.studygroups;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupProfile extends AppCompatActivity {

    TextView name, type, department, course_no, professor,creator;
    String key;
    String currentUser;
    String newGroup;

    ArrayList<String> currentMembers = new ArrayList<>();
    ArrayList<String> currentGroups = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");
    CollectionReference collectionProfile = db.collection("User Profile");
    DocumentReference groupDocument;
    DocumentReference userDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        name = findViewById(R.id.set_name);
        type = findViewById(R.id.set_type);
        department = findViewById(R.id.set_department);
        course_no = findViewById(R.id.set_course);
        professor = findViewById(R.id.set_professor);
        creator = findViewById(R.id.creator);

        Bundle bundle = getIntent().getExtras();
        final Group target = (Group)bundle.getSerializable("group");

        name.setText(target.getName());
        type.setText(target.getType());
        department.setText(target.getDepartment());
        course_no.setText(target.getCourse_no());
        professor.setText(target.getProf());
        creator.setText(target.creator);

        currentMembers = target.group_member;
        currentUser = target.creator;
        key = target.getKey();
        newGroup = target.name;

        Query search = collection.whereEqualTo("key",target.key);
        search.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        groupDocument = document.getReference();
                    }
                }
            }
        });

        Query searchUser = collectionProfile.whereEqualTo("username",currentUser);
        searchUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userDocument = document.getReference();
                        UserProfile user = document.toObject(UserProfile.class);
                        currentGroups = user.groups;
                    }
                }
            }
        });


        //join group activity
        //TODO

        Button join = findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean found = false;
                for(int i= 0; i < currentMembers.size(); ++i) {
                    if (currentMembers.get(i).equals(currentUser)) {
                        found = true;
                    }
                }
                if(found){
                    Toast.makeText(GroupProfile.this, "You have already joined this group!", Toast.LENGTH_LONG).show();
                }
                else{
                    currentMembers.add(currentUser);
                    currentGroups.add(newGroup);

                    System.out.println("PRINT CURRENT MEMEBER SIZE" + currentMembers.size());
                    System.out.println("PRINT CURERNT GROUPS"+currentGroups.size());

                    collection.document(groupDocument.getId()).update("group_member",currentMembers);
                    collectionProfile.document(userDocument.getId()).update("groups",currentGroups);
                }
            }
        });

    }

}
