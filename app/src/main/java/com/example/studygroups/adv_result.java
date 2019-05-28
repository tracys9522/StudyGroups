package com.example.studygroups;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class adv_result extends AppCompatActivity {

    ListView listView;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        ArrayList<String> search_criteria = null;

        if(b != null){
            search_criteria = b.getStringArrayList("key");
            Log.i("List", "Passed Array List :: " + search_criteria);
        }

        setContentView(R.layout.activity_adv_result);

        listView = (ListView) findViewById(R.id.adv_list);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        String department = search_criteria.get(0);
        String course_no = search_criteria.get(1);
        String professor = search_criteria.get(2);

        Query query = null;

        if(!department.equals(""))
        {
            query = collection.whereEqualTo("department",department);
            if(!course_no.equals("") && !professor.equals("")){
                query = collection.whereEqualTo("department",department)
                        .whereEqualTo("course_no",course_no)
                        .whereEqualTo("prof",professor);
            }
            if(!course_no.equals("") && professor.equals("")){
                query = collection.whereEqualTo("department",department)
                        .whereEqualTo("course_no",course_no);
            }
            if(course_no.equals("") && !professor.equals("")){
                query = collection.whereEqualTo("department",department)
                        .whereEqualTo("prof",professor);
            }
        }

        //department is empty
        //if course_num is not empty
        else if(!course_no.equals("")){
            //if professor is not empty
            if(!professor.equals("")){
                query = collection.whereEqualTo("course_no",course_no)
                        .whereEqualTo("prof",professor);
            }
            //only has course_num
            else {
                query = collection.whereEqualTo("course_no", course_no);
            }
        }

        //department is empty
        //course_num is also empty
        else{
            if(!professor.equals("")){
                query = collection.whereEqualTo("prof", professor);
            }
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    arrayList.clear();
                    for(QueryDocumentSnapshot document: task.getResult()){
                        String value = document.toObject(Group.class).toString();
                        System.out.println(value);
                        arrayList.add(value);

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


}
