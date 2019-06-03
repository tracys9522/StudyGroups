package com.example.studygroups;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;


public class UploadImage extends AppCompatActivity {

    ImageButton take;
    Button save;
    ImageView capture;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collection = db.collection("Active Groups");
    CollectionReference profileCollection = db.collection("User Profile");
    DocumentReference groupDocument;
    DocumentReference profileDocument;

    ArrayList<String> image;
    byte [] datafile;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean status;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        Bundle bundle = getIntent().getExtras();
        Group target = null;
        if(bundle.getSerializable("group") != null){
            target = (Group)bundle.getSerializable("group");
            image = target.images;
            status = true;
        }
        if(getIntent().getStringExtra("profileAction") != null){
            status = false;
            username = getIntent().getStringExtra("username");
        }

        take = findViewById(R.id.take_picture);
        save = findViewById(R.id.save);
        capture = findViewById(R.id.image);

        if(status){
            Query search = collection.whereEqualTo("key",target.key);
            search.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groupDocument = document.getReference();
                        }
                    }
                }
            });

        } else {
            Query search = profileCollection.whereEqualTo("username",username);
            search.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            profileDocument = document.getReference();
                        }
                    }
                }
            });

        }


        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //image.add(filepath);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUploader();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArr);
            datafile = byteArr.toByteArray();
            capture.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void FileUploader(){
        StorageReference filepath;
        StorageReference imageRef;
        if(status){
            filepath = FirebaseStorage.getInstance().getReference("Files");
            imageRef = filepath.child("image"+new Date().getTime());

        }else {
            filepath = FirebaseStorage.getInstance().getReference("Images");
            imageRef = filepath.child("image"+new Date().getTime());

        }

        if(datafile == null){
            Toast.makeText(UploadImage.this, "Please upload an image", Toast.LENGTH_LONG).show();
            return;
        }

        UploadTask uploadTask = imageRef.putBytes(datafile);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Image uploaded!");
                        Toast.makeText(UploadImage.this, "Uploaded!", Toast.LENGTH_LONG).show();
                        String name = taskSnapshot.getStorage().getName();
                        System.out.println("FILEPATH: "+name);
                        save_info(name);
                        if(!status){
                            Intent intent = new Intent(UploadImage.this, Profile.class);
                            startActivity(intent);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        System.out.println("Error in uploading image.");
                        Toast.makeText(UploadImage.this, "Ops something went wrong...", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void save_info(String filename){
        if(status){
            image.add(filename);
            collection.document(groupDocument.getId()).update("images", image);

        } else {

            profileCollection.document(profileDocument.getId()).update("profilePicture", filename);
        }

    }
}