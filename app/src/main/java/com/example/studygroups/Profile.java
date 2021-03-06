package com.example.studygroups;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> classes = new ArrayList<String>();
    ArrayList<String> groups = new ArrayList<String>();

    private TextView userName;
    private TextView majorText;
    private ListView classesList;
    private ListView groupsList;
    private ImageView profilePicture;
    private String showButtons;

    private ArrayAdapter<String> groupsAdapter;
    private ArrayAdapter<String> classesAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("User Profile");
    CollectionReference active = db.collection("Active Groups");

    DocumentReference userDoc;
    Button acceptButton;
    Button rejectButton;
    String email;
    Group group;
    Group target;
    private String imguri;
    String path;

    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imguri = null;
        userName = (TextView) findViewById(R.id.userName);
        majorText = (TextView) findViewById(R.id.majorText);
        classesList = (ListView) findViewById(R.id.classes);
        groupsList = (ListView) findViewById(R.id.groups);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        acceptButton = (Button) findViewById(R.id.accept_button);
        rejectButton = (Button) findViewById(R.id.reject_button);

        editProfile = (Button) findViewById(R.id.editProfileButton);


        email = getIntent().getStringExtra("username");
        showButtons = getIntent().getStringExtra("showButtons");
        Bundle bundle = getIntent().getExtras();
        try {
            group = (Group) bundle.getSerializable("group");
        } catch (java.lang.NullPointerException exception) {
            group = null;
        }
        if(email != null && !email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            editProfile.setVisibility(View.INVISIBLE);
        } else {
            editProfile.setVisibility(View.VISIBLE);
        }

        Query query = collection.whereEqualTo("username", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userDoc = document.getReference();
                        UserProfile user = document.toObject(UserProfile.class);

                        if (user.display_name != null) {
                            userName.setText(user.display_name);
                        }
                        if (user.major != null) {
                            majorText.setText(user.major);
                        }
                        if (user.profilePicture != null) {
                            imguri = user.profilePicture;
                            String filename = user.profilePicture;
                            StorageReference filepath = FirebaseStorage.getInstance().getReference("Images").child(filename);
                            File localFile = null;
                            try {
                                localFile = File.createTempFile("images", "jpeg");
                                path = localFile.getPath();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            filepath.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
                                    Bitmap myBitmap = BitmapFactory.decodeFile(path);
                                    profilePicture.setImageBitmap(myBitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        } else {
                            imguri = null;
                        }
                        if (user.classes.size() > 0) {
                            for (int i = 0; i < user.classes.size(); i++) {
                                classes.add(i, user.classes.get(i));
                            }
                            classesAdapter.notifyDataSetChanged();
                        } else {
                            LinearLayout classView = (LinearLayout) findViewById(R.id.classesView);
                            classView.setVisibility(View.GONE);
                        }
                        ArrayList<String> tempGroups = user.groups;
                        if (tempGroups != null) {
                            for (int i = 0; i < user.groups.size(); i++) {
                                groups.add(i, user.groups.get(i));
                            }
                            groupsAdapter.notifyDataSetChanged();
                        }


                    }
                }
            }
        });

        if (showButtons == null) {
            acceptButton.setVisibility(View.GONE);
            acceptButton.setClickable(false);

            rejectButton.setVisibility(View.GONE);
            rejectButton.setClickable(false);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.addGroupMember(email);
                Query query = active.whereEqualTo("name", group.name);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference groupDoc = document.getReference();
                                Map<String, Object> answers = new HashMap<>();
                                answers.put("pending_invitations", group.pending_invitations);
                                answers.put("group_member", group.group_member);
                                active.document(groupDoc.getId()).update(answers);
                                rejectButton.setVisibility(View.GONE);
                                rejectButton.setClickable(false);
                                acceptButton.setText("Accepted!");
                                break;
                            }
                        }
                    }
                });
                Query query2 = collection.whereEqualTo("username", email);
                query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                user.addGroup(group);
                                Map<String, Object> answers = new HashMap<>();
                                answers.put("groups", user.groups);
                                collection.document(document.getReference().getId()).update(answers);
                                break;
                            }
                        }
                    }
                });

            }
        });


        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.removePendingInvitation(email);
                Query query = active.whereEqualTo("name", group.name);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference groupDoc = document.getReference();
                                Map<String, Object> answers = new HashMap<>();
                                answers.put("pending_invitations", group.pending_invitations);
                                active.document(groupDoc.getId()).update(answers);
                                acceptButton.setVisibility(View.GONE);
                                acceptButton.setClickable(false);
                                rejectButton.setText("Rejected!");
                                break;
                            }
                        }
                    }
                });
            }
        });


        classesAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                classes);

        classesList.setAdapter(classesAdapter);

        groupsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                PostLoginActivity.current_user.groups);

        groupsList.setAdapter(groupsAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("username", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(intent);

            }
        });

        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lookup = groups.get(position);
                Query query = active.whereEqualTo("name", lookup);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference groupDoc = document.getReference();
                                target = document.toObject(Group.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("group", target);

                                Intent intent = new Intent(Profile.this, GroupProfile.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , email);
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                emailIntent.putExtra(Intent.EXTRA_TEXT   , message);
                emailIntent.setType("text/plain"); // <-- HERE
                startActivity(emailIntent); // <-- AND HERE
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(showButtons != null){
            Intent intent = new Intent(Profile.this, PendingInvitationsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", group);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_profile:
                Intent p = new Intent(Profile.this, Profile.class);
                p.putExtra("username", PostLoginActivity.username);
                startActivity(p);
                break;
            case R.id.nav_search:
                Intent s = new Intent(Profile.this, Search.class);
                startActivity(s);
                break;
            case R.id.nav_messages:
                Intent m = new Intent(Profile.this, Messages.class);
                startActivity(m);
                break;
            case R.id.nav_create_group:
                Intent cg = new Intent(Profile.this, CreateGroup.class);
                startActivity(cg);
                break;
            case R.id.nav_groups:
                Intent g = new Intent(this, MyGroupsActivity.class);
                startActivity(g);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent l = new Intent(this, MainActivity.class);
                startActivity(l);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
