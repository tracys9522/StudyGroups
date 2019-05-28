package com.example.studygroups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class GroupCreateActivity extends AppCompatActivity {

    FirebaseFirestore reff;
    static HashMap<String, Integer> group_name_map = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        reff = FirebaseFirestore.getInstance();
        CheckBox professor_checkbox = findViewById(R.id.professor_checkbox);
        CheckBox type_checkbox = findViewById(R.id.type_checkbox);
        CheckBox course_no_checkbox = findViewById(R.id.course_number_checkbox);
        CheckBox department_checkbox = findViewById(R.id.department_checkbox);
    }

    public void addGroup(View view){
        String group_name = ((EditText) findViewById(R.id.group_name)).getText().toString();
        String type = ((EditText) findViewById(R.id.type_input)).getText().toString();
        String professor = ((EditText) findViewById(R.id.professor_input)).getText().toString();
        String course_no = ((EditText) findViewById(R.id.course_number_input)).getText().toString();
        String department = ((EditText) findViewById(R.id.department_input)).getText().toString();

        if(group_name_map.get(group_name) == null){
            group_name_map.put(group_name, 1);
        } else {
            String temp = group_name;
            group_name = group_name + " (" + Integer.toString(group_name_map.get(group_name)) + ")";
            group_name_map.put(temp, group_name_map.get(temp) + 1);
        }

        System.out.println(group_name);
        Group g = new Group(group_name, type, department, course_no, professor);
        DocumentReference addedDocRef = reff.collection("Active Groups").document();
        String key = addedDocRef.getId();
        g.setKey(key);

        addedDocRef.set(g);
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }


    public void cancel(View view){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

}
