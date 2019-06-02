package com.example.studygroups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    String newGroup;

    ArrayList<String> currentMembers = new ArrayList<>();
    ArrayList<String> currentGroups = new ArrayList<>();
    Group target;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");
    CollectionReference collectionProfile = db.collection("User Profile");
    CollectionReference close_collection = db.collection("Closed Groups");
    DocumentReference groupDocument;
    DocumentReference userDocument;

    Bundle bundle;

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
        target = (Group) bundle.getSerializable("group");


        name.setText(target.getName());
        type.setText(target.getType());
        department.setText(target.getDepartment());
        course_no.setText(target.getCourse_no());
        professor.setText(target.getProf());
        creator.setText(target.creator);

        currentMembers = target.group_member;
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

        Query searchUser = collectionProfile.whereEqualTo("username", PostLoginActivity.username);
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
        if(target.creator.equals(PostLoginActivity.username)){
            join.setText("View Pending Invitations");
        }
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean found = false;
                for(int i= 0; i < currentMembers.size(); ++i) {
                    if (currentMembers.get(i).equals(PostLoginActivity.username)) {
                        found = true;
                        break;
                    }
                }

                if(found && !target.creator.equals(PostLoginActivity.username)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("You have already joined this group!");
                    builder.show();
                } else if (target.creator.equals(PostLoginActivity.username)){
                    Intent intent = new Intent(GroupProfile.this, PendingInvitationsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", target);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
//                    currentMembers.add(currentUser);
//                    currentGroups.add(newGroup);
//                    collection.document(groupDocument.getId()).update("group_member", currentMembers);
                    Query searchGroup = collection.whereEqualTo("name",target.getName());
                    searchGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    groupDocument = document.getReference();
                                    Group g = document.toObject(Group.class);
                                    for(String s: g.pending_invitations){
                                        if(s.equals(PostLoginActivity.username)){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                                            builder.setMessage("You have already joined this group!");
                                            builder.show();
                                            return;
                                        }
                                    }


                                    target.request2join(PostLoginActivity.username);
                                    collection.document(groupDocument.getId()).update("pending_invitations", target.pending_invitations);
                                    Toast.makeText(GroupProfile.this, "You have requested the creator's permission", Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        });

        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("group",target);

                Intent intent = new Intent(GroupProfile.this, UploadImage.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });






        Button close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String the_creator = creator.getText().toString();
                if(!PostLoginActivity.username.equals(the_creator)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("Only the creator can close this group");
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("Are you sure you want to close the group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference addedDocRef = close_collection.document();
                                    String key = addedDocRef.getId();
                                    target.setKey(key);
                                    addedDocRef.set(target);
                                    collection.document(groupDocument.getId()).delete();
                                    Intent intent = new Intent(GroupProfile.this, PostLoginActivity.class);
                                    intent.putExtra("original_activity", "not main");
                                    startActivity(intent);                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            }
        });


    }

}
