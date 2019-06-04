package com.example.studygroups;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore reff;
    static HashMap<String, Integer> group_name_map = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionProfile = db.collection("User Profile");
    Group g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reff = FirebaseFirestore.getInstance();
        CheckBox professor_checkbox = findViewById(R.id.professor_checkbox);
        CheckBox type_checkbox = findViewById(R.id.type_checkbox);
        CheckBox course_no_checkbox = findViewById(R.id.course_number_checkbox);
        CheckBox department_checkbox = findViewById(R.id.department_checkbox);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void addGroup(View view){
        String group_name = ((EditText) findViewById(R.id.group_name)).getText().toString();
        String type = ((EditText) findViewById(R.id.type_input)).getText().toString().toUpperCase();
        String professor = ((EditText) findViewById(R.id.professor_input)).getText().toString().toUpperCase();
        String course_no = ((EditText) findViewById(R.id.course_number_input)).getText().toString();
        String department = ((EditText) findViewById(R.id.department_input)).getText().toString().toUpperCase();
        String description = ((EditText) findViewById(R.id.groupDescription)).getText().toString();
        if(description == null){
            description = "";
        }

        if(group_name_map.get(group_name) == null){
            group_name_map.put(group_name, 1);
        } else {
            String temp = group_name;
            group_name = group_name + " (" + Integer.toString(group_name_map.get(group_name)) + ")";
            group_name_map.put(temp, group_name_map.get(temp) + 1);
        }

        System.out.println(group_name);

        //TODO
        //get creator
        String creator = PostLoginActivity.username;

        g = new Group(group_name, type, department, course_no, professor, creator,description);
        DocumentReference addedDocRef = reff.collection("Active Groups").document();
        String key = addedDocRef.getId();
        g.setKey(key);
        addedDocRef.set(g);

        Query searchUser = collectionProfile.whereEqualTo("username", PostLoginActivity.current_user.getUsername());
        searchUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference userDocument = document.getReference();
                        Map<String, Object> groups = new HashMap<>();
                        PostLoginActivity.current_user.addGroup(g);
                        groups.put("groups", PostLoginActivity.current_user.groups);
                        userDocument.update(groups);
                        break;
                    }
                }
            }
        });





        Intent myIntent = new Intent(this, PostLoginActivity.class);
        myIntent.putExtra("original_activity", "create");
        startActivity(myIntent);
    }


    public void cancel(View view){
        Intent myIntent = new Intent(this, PostLoginActivity.class);
        myIntent.putExtra("original_activity", "create");
        startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
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
                Intent p = new Intent(this, Profile.class);
                startActivity(p);
                break;
            case R.id.nav_search:
                Intent s = new Intent(this, Search.class);
                startActivity(s);
                break;
            case R.id.nav_messages:
                Intent m = new Intent(this, Messages.class);
                startActivity(m);
                break;
            case R.id.nav_create_group:
                Intent cg = new Intent(this, CreateGroup.class);
                startActivity(cg);
                break;
            case R.id.nav_groups:
                Intent g = new Intent(this, PostLoginActivity.class);
                g.putExtra("original_activity", "not main");
                startActivity(g);
                break;
            case R.id.nav_logout:
                Intent l = new Intent(this, PostLoginActivity.class);
                l.putExtra("original_activity", "not main");
                startActivity(l);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
