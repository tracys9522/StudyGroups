package com.example.studygroups;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link adv_search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link adv_search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class adv_search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> info = new ArrayList<>();

    private CheckBox box1;
    private CheckBox box2;
    private CheckBox box3;

    private Spinner staticSpinner;
    private Spinner staticSpinner2;
    private Spinner staticSpinner3;

    public adv_search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment adv_search.
     */
    // TODO: Rename and change types and number of parameters
    public static adv_search newInstance(String param1, String param2) {
        adv_search fragment = new adv_search();
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
    }

    //FirebaseFirestore myfirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_adv_search, container,false);

        //spinner for departments
        staticSpinner = v.findViewById(R.id.static_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.department));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(myAdapter);

        //spinner for course number
        staticSpinner2 = v.findViewById(R.id.static_spinner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.number));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner2.setAdapter(myAdapter2);

        //spinner for professor
        staticSpinner3 = v.findViewById(R.id.static_spinner3);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.professor));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner3.setAdapter(myAdapter3);

        //Search
        Button search = v.findViewById(R.id.search);
        Button reset = v.findViewById(R.id.reset);

        box1 = v.findViewById(R.id.type_title);
        box2 = v.findViewById(R.id.coursenum);
        box3 = v.findViewById(R.id.professor);

        //insert temp values ---- be replaced by Aneesh's part
        //myfirestore = FirebaseFirestore.getInstance();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                info.clear();

                String department= "";
                String course_no= "";
                String professor= "";

                if(box1.isChecked()){
                    department= staticSpinner.getSelectedItem().toString();
                    info.add(department);

                }
                else info.add("");

                if(box2.isChecked()){
                    course_no = staticSpinner2.getSelectedItem().toString();
                    info.add(course_no);
                }
                else info.add("");

                if(box3.isChecked()){
                    professor = staticSpinner3.getSelectedItem().toString();
                    info.add(professor);
                }
                else info.add("");

                /*
                Map<String,String> group = new HashMap<>();
                group.put("department",department);
                group.put("course_no",course_no);
                group.put("prof", professor);
                myfirestore.collection("Active Groups").add(group);
                */

                Intent intent = new Intent(getActivity(), adv_result.class);
                intent.putExtra("key",info);
                startActivity(intent);
            }
        });

        //refresh the page
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reload current fragment
                getFragmentManager().beginTransaction().detach(adv_search.this).attach(adv_search.this).commit();
            }

        });

        //return inflater.inflate(R.layout.fragment_adv_search, container, false);
        return v;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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