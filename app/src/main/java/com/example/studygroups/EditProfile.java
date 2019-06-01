package com.example.studygroups;

import android.app.AppComponentFactory;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class EditProfile extends AppCompatActivity {


    public static final String TAG = "Edit Profile";

    public static final int PICK_IMAGE_REQUEST = 1;

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
    private Button changeProfilePictureButton;

    private StorageTask uploadTask;

    private Uri imguri;


    private ImageView profilePicture;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("User Profile");
    DocumentReference userDoc;

    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        classInput = (EditText) findViewById(R.id.classesInput);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        backButton = (Button) findViewById(R.id.backButton);

        addedClasses = new ArrayList<String>();
        profilePicture = (ImageView) findViewById(R.id.profilePictureEdit);
        changeUsername = (EditText) findViewById(R.id.changeUsername);
        display_name = (EditText) findViewById(R.id.display_name);
        major = (EditText) findViewById(R.id.major);
        listview = (ListView) findViewById(R.id.classList);
        adapter = new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_list_item_1, addedClasses );
        listview.setAdapter(adapter);

        changeProfilePictureButton = (Button) findViewById(R.id.changePropilePic);


        saveChangesButton = (Button) findViewById(R.id.saveChanges);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

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
                        if(user.profilePicture != null){
                            imguri = Uri.parse(user.profilePicture);
                            profilePicture.setImageURI(imguri);
                        } else {
                            imguri = null;
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
                Intent p = new Intent(EditProfile.this, Profile.class);
                startActivity(p);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imguri != null){
                    if(uploadTask != null && uploadTask.isInProgress()){
                    } else{
                        FileUploader();
                    }
                    saveChanges();
                }
                Intent p = new Intent(EditProfile.this, Profile.class);
                startActivity(p);
            }
        });
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        initImageLoader();
        setProfileImage();


    }

    private void FileUploader(){

        final StorageReference ref = mStorageRef.child(System.currentTimeMillis()+"");
        uploadTask= ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println("Image uploaded!");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        System.out.println("Error in uploading image.");
                    }
                });
    }
    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void FileChooser(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imguri = data.getData();

//                Picasso.with(this).load(imguri).into(profilePicture);
            profilePicture.setImageURI(imguri);
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(universalImageLoader.getConfig());
    }

    private void setProfileImage(){
        if(imguri == null){
            String imgURL = "https://wallpaperstream.com/wallpapers/full/tulips/Tulips-Flowers-Bouquet-HD-Wallpaper.jpg";
            UniversalImageLoader.setImage(imgURL, profilePicture, null, "");
        }

    }


    public void saveChanges(){
        EditText username = (EditText)findViewById(R.id.changeUsername);
        String usernameString = username.getText().toString();
        EditText name = (EditText)findViewById(R.id.display_name);
        String nameString = name.getText().toString();
        EditText major = (EditText)findViewById(R.id.major);
        String majorString = major.getText().toString();




        //TODO update the database

        if(userDoc != null){
            user.username = usernameString;
            user.display_name = nameString;
            user.major = majorString;
            user.classes = addedClasses;
            user.profilePicture = imguri.toString();

            collection.document(userDoc.getId()).update(
                    "username", user.username,
                    "display_name", user.display_name,
                    "major", user.major,
                    "classes", user.classes,
                    "profilePicture", user.profilePicture
            );

        } else {
            user = new UserProfile(usernameString, nameString, majorString, imguri.toString(),addedClasses);
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
