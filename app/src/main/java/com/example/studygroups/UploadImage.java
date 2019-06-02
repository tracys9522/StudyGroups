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
    DocumentReference groupDocument;

    ArrayList<String> image;
    byte [] datafile;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        Bundle bundle = getIntent().getExtras();
        Group target = (Group)bundle.getSerializable("group");
        image = target.images;

        take = findViewById(R.id.take_picture);
        save = findViewById(R.id.save);
        capture = findViewById(R.id.image);

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
                    //collection.document(groupDocument.getId()).update("images", image);
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
        StorageReference filepath = FirebaseStorage.getInstance().getReference("Files");
        StorageReference imageRef = filepath.child("image"+new Date().getTime());
        UploadTask uploadTask = imageRef.putBytes(datafile);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Image uploaded!");
                        Toast.makeText(UploadImage.this, "Uploaded!", Toast.LENGTH_LONG).show();
                        Task<Uri> download = taskSnapshot.getStorage().getDownloadUrl();
                        String image = taskSnapshot.getStorage().getDownloadUrl().toString();
                        System.out.println("FILEPATH: "+image);
                        if(download.isSuccessful()){
                            String filePath = download.getResult().toString();
                            System.out.println("FILEPATH: "+filePath);
                        }
                        else{
                            System.out.println("DOESNT PRINT");
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
}
