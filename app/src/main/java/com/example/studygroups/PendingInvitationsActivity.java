package com.example.studygroups;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PendingInvitationsActivity extends AppCompatActivity {
    private ListView listview;
    Group g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invitations);
        listview = findViewById(R.id.pending_invitations);

        Bundle b = getIntent().getExtras();
        g = (Group) b.getSerializable("group");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                g.pending_invitations );

        listview.setAdapter(arrayAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listItem = (String) listview.getItemAtPosition(position);
                Intent intent = new Intent(PendingInvitationsActivity.this, Profile.class);
                intent.putExtra("showButtons", "not main");
                intent.putExtra("username", listItem);
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", g);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        if(g.pending_invitations.size() == 0){
            TextView message = (TextView) findViewById(R.id.noPendingMessage);
            message.setVisibility(View.VISIBLE);
        }

    }


    public void onBackPressed() {
            Intent intent = new Intent(this, GroupProfile.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("group", g);
            intent.putExtras(bundle);
            startActivity(intent);
    }




}
