package com.example.studygroups;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActiveGroup.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActiveGroup#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ActiveGroup extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    DatabaseReference active_group_database;
    DatabaseReference closed_group_database;
    private OnFragmentInteractionListener mListener;

    ArrayList<String> active_keys = new ArrayList<>();
    FirebaseFirestore db;
    ArrayList<Group> result_group = new ArrayList<>();
    FloatingActionButton addButton;
    ListView active_list;

    public ActiveGroup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveGroup.
     */
    // TODO: Rename and change types and number of parameters
    public static ActiveGroup newInstance(String param1, String param2) {
        ActiveGroup fragment = new ActiveGroup();
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

        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_active_group, container, false);
        active_list = (ListView) ll.findViewById(R.id.active_list_view);
        addButton = ll.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroup.class);
                startActivity(intent);
            }
        });

//        db.collection("Active Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {
//
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                active_keys.clear();
//                for (DocumentSnapshot snapshot : documentSnapshots) {
//                    if(snapshot.get("name") != null) {
//                        active_keys.add((String) snapshot.get("name"));
//                        Group group = snapshot.toObject(Group.class);
//                        result_group.add(group);
//                    }
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, active_keys);
//                adapter.notifyDataSetChanged();
//                active_list.setAdapter(adapter);
//            }
//        });


        active_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group target = result_group.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("group",target);

                Intent intent = new Intent(getActivity(), GroupProfile.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return ll;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db.collection("Active Groups").addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                active_keys.clear();
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    if(snapshot.get("name") != null) {
                        active_keys.add((String) snapshot.get("name"));
                        Group group = snapshot.toObject(Group.class);
                        result_group.add(group);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, active_keys);
                adapter.notifyDataSetChanged();
                active_list.setAdapter(adapter);
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
