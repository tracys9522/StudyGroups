package com.example.studygroups;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PendingInvitationsActivity extends AppCompatActivity {
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invitations);
        listview = findViewById(R.id.pending_invitations);

        Bundle b = getIntent().getExtras();
        Group g = (Group) b.getSerializable("group");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                g.pending_invitations );

        listview.setAdapter(arrayAdapter);




    }



}
