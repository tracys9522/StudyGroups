package com.example.studygroups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

    TextView name, type, department, course_no, professor, creator;
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
    String from_closed;
    Bundle bundle;

    Button groupMembers;


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

        groupMembers = (Button) findViewById(R.id.groupMembers);




        Bundle bundle = getIntent().getExtras();
        target = (Group) bundle.getSerializable("group");
        from_closed = (String) getIntent().getExtras().get("from_closed");

        name.setText(target.getName());
        type.setText(target.getType());
        department.setText(target.getDepartment());
        course_no.setText(target.getCourse_no());
        professor.setText(target.getProf());
        creator.setText(target.getCreator());

        currentMembers = target.group_member;
        key = target.getKey();
        newGroup = target.name;

        Query search = collection.whereEqualTo("key", target.key);

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
        if (from_closed == null && target.creator.equals(PostLoginActivity.username)) {
            join.setText("Pending Invitations");
        } else if (from_closed != null && target.creator.equals(PostLoginActivity.username)) {
            join.setText("Make Group Active");
        } else if (from_closed != null) {
            join.setVisibility(View.GONE);
            join.setEnabled(false);
        }


        groupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(currentMembers.size() >0){
                        String[] members =  currentMembers.toArray(new String[0]);
                        new AlertDialog.Builder(GroupProfile.this)
                                .setTitle(newGroup + " Members")
                                .setItems(members, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(GroupProfile.this, Profile.class);
                                        intent.putExtra("username", currentMembers.get(which));
                                        Bundle bundle = new Bundle();
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                })
                                .setNegativeButton("close", null)
                                .setIcon(R.drawable.logo)
                                .show();
                    } else{
                        new AlertDialog.Builder(GroupProfile.this)
                                .setTitle(name + "Members")
                                .setMessage("There are no current members")
                                .setNegativeButton("close", null)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean found = false;
                for (int i = 0; i < currentMembers.size(); ++i) {
                    if (currentMembers.get(i).equals(PostLoginActivity.username)) {
                        found = true;
                        break;
                    }
                }
                if (found && !target.creator.equals(PostLoginActivity.username)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("You have already joined this group!");
                    builder.show();
                } else if (target.creator.equals(PostLoginActivity.username) && from_closed == null) {
                    Intent intent = new Intent(GroupProfile.this, PendingInvitationsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", target);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (target.creator.equals(PostLoginActivity.username) && from_closed != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("Are you sure you want to make the group active?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    for (String member : target.group_member) {
                                        Query searchGroup = collectionProfile.whereEqualTo("name", member);
                                        searchGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        UserProfile user = document.toObject(UserProfile.class);
                                                        user.groups.add(target.getName());
                                                        collectionProfile.document(document.getId()).update("groups", user.groups);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    close_collection.document(target.getKey()).delete();
                                    DocumentReference addedDocRef = collection.document();
                                    String key = addedDocRef.getId();
//                                    target.group_member.clear();
                                    target.setKey(key);
                                    addedDocRef.set(target);
                                    Intent intent = new Intent(GroupProfile.this, PostLoginActivity.class);
                                    intent.putExtra("original_activity", "not main");
                                    startActivity(intent);
                                }
                            });
                    builder.show();
                } else {
                    Query searchGroup = collection.whereEqualTo("name", target.getName());
                    searchGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    groupDocument = document.getReference();
                                    Group g = document.toObject(Group.class);
                                    for (String s : g.pending_invitations) {
                                        if (s.equals(PostLoginActivity.username)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                                            builder.setMessage("You have already sent an invitation to this group!");
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

        //TODO
        //FIX LAYOUT
        
        //if creator or group member set visible
        boolean visible = false;
        Button upload = findViewById(R.id.upload);
        if (target.creator.equals(PostLoginActivity.username)) {
            visible = true;
            Button close = (Button) findViewById(R.id.close);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(270, 0, 0, 0);
            close.setLayoutParams(params);
            Button j = (Button) findViewById(R.id.join);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.setMargins(20, 0, 0, 0);
            j.setLayoutParams(params2);
        }
        for (int i = 0; i < currentMembers.size(); i++) {
            if (currentMembers.get(i).equals(PostLoginActivity.username)) {
                visible = true;
                Button close = (Button) findViewById(R.id.close);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(270, 0, 0, 0);
                close.setLayoutParams(params);
            }
        }
        if(!visible){
            upload.setVisibility(View.GONE);
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("group", target);

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
                if (!PostLoginActivity.username.equals(the_creator)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("Only the creator can close this group");
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfile.this);
                    builder.setMessage("Are you sure you want to close the group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    for (String member : target.group_member) {
                                        Query searchGroup = collectionProfile.whereEqualTo("name", member);
                                        searchGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        UserProfile user = document.toObject(UserProfile.class);
                                                        user.groups.remove(target.getName());
                                                        collectionProfile.document(document.getId()).update("groups", user.groups);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    DocumentReference addedDocRef = close_collection.document();
                                    String key = addedDocRef.getId();
//                                    target.group_member.clear();
                                    target.setKey(key);
                                    addedDocRef.set(target);
                                    collection.document(groupDocument.getId()).delete();
                                    Intent intent = new Intent(GroupProfile.this, PostLoginActivity.class);
                                    intent.putExtra("original_activity", "not main");
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }
            }
        });


        if (from_closed != null) {
            close.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);
            close.setEnabled(false);
            upload.setEnabled(false);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PostLoginActivity.class);
        intent.putExtra("original_activity", "this");
        startActivity(intent);
    }
}


