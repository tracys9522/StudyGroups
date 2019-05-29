package com.example.studygroups;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupProfile extends AppCompatActivity {

    TextView name, type, department, course_no, professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        name = findViewById(R.id.set_name);
        type = findViewById(R.id.set_type);
        department = findViewById(R.id.set_department);
        course_no = findViewById(R.id.set_course);
        professor = findViewById(R.id.set_professor);

        Bundle bundle = getIntent().getExtras();
        Group target = (Group)bundle.getSerializable("group");

        name.setText(target.getName());
        type.setText(target.getType());
        department.setText(target.getDepartment());
        course_no.setText(target.getCourse_no());
        professor.setText(target.getProf());

        //join group activity
        //TODO

        Button join = findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
