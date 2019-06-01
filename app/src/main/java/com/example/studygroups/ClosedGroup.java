package com.example.studygroups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ClosedGroup extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseReference closed_group_database;
    FirebaseFirestore db;
    ArrayList<String> closed_keys = new ArrayList<>();
    ArrayList<Group> result_group = new ArrayList<>();

    public ClosedGroup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClosedGroup.
     */
    // TODO: Rename and change types and number of parameters
    public static ClosedGroup newInstance(String param1, String param2) {
        ClosedGroup fragment = new ClosedGroup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_closed_group, container, false);
        final ListView closed_list = (ListView) ll.findViewById(R.id.closed_list_view);
        db.collection("Closed Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                closed_keys.clear();
                ArrayList<String> temp = new ArrayList<>();
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    if(snapshot.get("name") != null) {
                        closed_keys.add((String) snapshot.get("name"));
                        Group group = snapshot.toObject(Group.class);
                        result_group.add(group);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, closed_keys);
                adapter.notifyDataSetChanged();
                closed_list.setAdapter(adapter);
            }
        });


//        c.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Group target = result_group.get(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("group",target);
//
//                Intent intent = new Intent(getActivity(), GroupProfile.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });


        return ll;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
