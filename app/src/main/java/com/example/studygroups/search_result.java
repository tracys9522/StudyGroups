package com.example.studygroups;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class search_result extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");

    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<Group> result_group = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String value = intent.getStringExtra("search");
        System.out.println("SEARCH CRITERIA: "+value);
        setContentView(R.layout.activity_search_result);

        arrayList.clear();

        listView = findViewById(R.id.basic_list);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        if(value.equals("") || value == null){
            collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            Group group = document.toObject(Group.class);
                            String value = group.toString();
                            System.out.println(value);
                            arrayList.add(value);
                            result_group.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }

        else {
            Query query1 = collection.whereEqualTo("name", value);
            Query query = collection.whereEqualTo("department", value);
            Query query2 = collection.whereEqualTo("course_no", value);
            Query query3 = collection.whereEqualTo("prof", value);

            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            String value = group.toString();
                            System.out.println(value);
                            arrayList.add(value);
                            result_group.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            String value = group.toString();
                            System.out.println(value);
                            arrayList.add(value);
                            result_group.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            String value = group.toString();
                            System.out.println(value);
                            arrayList.add(value);
                            result_group.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            query3.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            String value = group.toString();
                            System.out.println(value);
                            arrayList.add(value);
                            result_group.add(group);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group target = result_group.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("group",target);

                Intent intent = new Intent(search_result.this, GroupProfile.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
