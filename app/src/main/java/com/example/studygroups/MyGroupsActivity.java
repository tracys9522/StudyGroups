package com.example.studygroups;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MyGroupsActivity extends AppCompatActivity {

    private ListView listview;
    Group g;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String, Group> name2group = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        listview = findViewById(R.id.current_groups);

        db.collection("Active Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    if(snapshot.get("name") != null) {
                        Group group = snapshot.toObject(Group.class);
                        name2group.put((String) snapshot.get("name"), group);
                    }
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MyGroupsActivity.this,
                android.R.layout.simple_list_item_1,
                 PostLoginActivity.current_user.groups);

        listview.setAdapter(arrayAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String listItem = (String) listview.getItemAtPosition(position);
                Intent intent = new Intent(MyGroupsActivity.this, GroupProfile.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", name2group.get(listItem));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        if(PostLoginActivity.current_user.groups.size() == 0){
            TextView message = (TextView) findViewById(R.id.noPendingMessage);
            message.setText("You are not part of any groups");
            message.setVisibility(View.VISIBLE);
        }

        System.out.println("My Groups Size");
        System.out.println(PostLoginActivity.current_user.groups.size());

    }


}
