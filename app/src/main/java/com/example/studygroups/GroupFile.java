package com.example.studygroups;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class GroupFile extends AppCompatActivity {

    ListView file;

    ArrayList<String> currentFiles = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_file);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String group = (String) bundle.get("group");

        file = findViewById(R.id.file);
        imageView = findViewById(R.id.file_image);

        Query searchGroup = collection.whereEqualTo("name", group);
        searchGroup.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Group group = document.toObject(Group.class);
                        currentFiles = group.images;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupFile.this, android.R.layout.simple_selectable_list_item, currentFiles);
                    adapter.notifyDataSetChanged();
                    file.setAdapter(adapter);
                }
            }
        });

        file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String filename = currentFiles.get(position);
                StorageReference filepath = FirebaseStorage.getInstance().getReference("Files").child(filename);

                GlideApp.with(this)
                        .load(filepath)
                        .into(imageView);

            }
        });


    }
}
