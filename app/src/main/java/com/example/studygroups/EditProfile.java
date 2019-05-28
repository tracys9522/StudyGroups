package com.example.studygroups;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.content.Intent;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.nostra13.universalimageloader.core.ImageLoader;

import com.google.firebase.FirebaseApp;


import com.nostra13.universalimageloader.core.ImageLoader.*;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import io.opencensus.tags.Tag;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "Taz";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listview;
    private ArrayList<String> addedClasses;
    private ArrayAdapter<String> adapter;
    private EditText classInput;
    private Button addClassButton;
    private Button backButton;
    private Button saveChangesButton;
    private EditText changeUsername;
    private EditText display_name;
    private EditText major;
    private UserProfile user;


    private ImageView profilePicture;

    private OnFragmentInteractionListener mListener;

    private HashMap<String,String> info = new HashMap<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("User Profile");
    DocumentReference userDoc;

    private View view;

    protected View mView;

    // TODO: Rename and change types and number of parameters
    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);



        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        this.mView = view;

        classInput = (EditText) view.findViewById(R.id.classesInput);
        addClassButton = (Button) view.findViewById(R.id.addClassButton);
        backButton = (Button) view.findViewById(R.id.backButton);

        addedClasses = new ArrayList<String>();
        profilePicture = (ImageView) view.findViewById(R.id.profilePictureEdit);
        changeUsername = (EditText) view.findViewById(R.id.changeUsername);
        display_name = (EditText) view.findViewById(R.id.display_name);
        major = (EditText) view.findViewById(R.id.major);
        listview = (ListView) view.findViewById(R.id.classList);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, addedClasses );
        listview.setAdapter(adapter);


        saveChangesButton = (Button) view.findViewById(R.id.saveChanges);


        Query query = collection.whereEqualTo("username","thackson@scu.edu");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        userDoc = document.getReference();
                        user = document.toObject(UserProfile.class);
                        if(user.username != null){
                            changeUsername.setText(user.username);
                        }
                        if(user.display_name != null){
                            display_name.setText(user.display_name);
                        }
                        if(user.major != null){
                            major.setText(user.major);
                        }
                        if(user.classes.size() > 0){
                            for(int i = 0; i < user.classes.size(); i++){
                                addedClasses.add(i, user.classes.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedClasses.add(classInput.getText().toString());
                adapter.notifyDataSetChanged();
                classInput.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p = new Intent(getActivity(), Profile.class);
                startActivity(p);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(view);

                Intent p = new Intent(getActivity(), Profile.class);
                startActivity(p);
            }
        });

        initImageLoader();
        setProfileImage();



        return view;
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        String imgURL = "https://wallpaperstream.com/wallpapers/full/tulips/Tulips-Flowers-Bouquet-HD-Wallpaper.jpg";
        UniversalImageLoader.setImage(imgURL, profilePicture, null, "");

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

    public void saveChanges(View view){
        EditText username = (EditText)view.findViewById(R.id.changeUsername);
        String usernameString = username.getText().toString();
        EditText name = (EditText)view.findViewById(R.id.display_name);
        String nameString = name.getText().toString();
        EditText major = (EditText)view.findViewById(R.id.major);
        String majorString = major.getText().toString();


        //TODO update the database

        if(userDoc != null){
            user.username = usernameString;
            user.display_name = nameString;
            user.major = majorString;
            user.classes = addedClasses;

            collection.document(userDoc.getId()).update(
                    "username", user.username,
                    "display_name", user.display_name,
                    "major", user.major,
                    "classes", user.classes
            );

        } else {
            user = new UserProfile(usernameString, nameString, majorString, addedClasses);
            collection
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

        }





    }

}
