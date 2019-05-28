package com.example.studygroups;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ShowGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);
        String key = (String) getIntent().getStringExtra("key");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }


}
