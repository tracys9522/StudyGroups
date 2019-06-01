package com.example.studygroups;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class Profile extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> classes = new ArrayList<String>();
    ArrayList<String> groups = new ArrayList<String>();

    private TextView userName;
    private TextView majorText;
    private ListView classesList;
    private ListView groupsList;
    private ImageView profilePicture;

    private ArrayAdapter<String> groupsAdapter;
    private ArrayAdapter<String> classesAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("User Profile");
    DocumentReference userDoc;

    private Uri imguri;



    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (TextView) findViewById(R.id.userName);
        majorText = (TextView) findViewById(R.id.majorText);
        classesList= (ListView) findViewById(R.id.classes);
        groupsList = (ListView) findViewById(R.id.groups);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);


        classesAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                classes );

        classesList.setAdapter(classesAdapter);

        groupsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                groups );

        groupsList.setAdapter(groupsAdapter);

        Query query = collection.whereEqualTo("username","thackson@scu.edu");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        userDoc = document.getReference();
                        UserProfile user = document.toObject(UserProfile.class);

                        if(user.display_name != null){
                            userName.setText(user.display_name);
                        }
                        if(user.major != null){
                            majorText.setText(user.major);
                        }
                        if(user.profilePicture != null){
                            imguri = Uri.parse(user.profilePicture);
                            profilePicture.setImageURI(imguri);
                        }else {
                            imguri = null;
                        }
                        if(user.classes.size() > 0){
                            for(int i = 0; i < user.classes.size(); i++) {
                                classes.add(i, user.classes.get(i));
                            }
                            classesAdapter.notifyDataSetChanged();
                        }
                        ArrayList<String> tempGroups = user.groups;
                        if(tempGroups != null){
                            for(int i = 0; i < user.groups.size(); i++) {
                                groups.add(i,user.groups.get(i));
                            }
                            groupsAdapter.notifyDataSetChanged();
                        }



                    }
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editProfile = (Button) findViewById(R.id.editProfileButton);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);


            }
        });

        initImageLoader();
        setProfileImage();

    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        if(imguri == null){
            String imgURL = "https://wallpaperstream.com/wallpapers/full/tulips/Tulips-Flowers-Bouquet-HD-Wallpaper.jpg";
            UniversalImageLoader.setImage(imgURL, profilePicture, null, "");
        }

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
                Intent p = new Intent(Profile.this, Profile.class);
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
                Intent g = new Intent(Profile.this, MainActivity.class);
                startActivity(g);
                break;
            case R.id.nav_logout:
                Intent l = new Intent(Profile.this, MainActivity.class);
                startActivity(l);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
