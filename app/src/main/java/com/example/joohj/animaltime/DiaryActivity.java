package com.example.joohj.animaltime;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class DiaryActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    String imagename = "kkc";
    private StorageReference mImageStorageRef;
    private String mPhotoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_diary);

        imageView = (ImageView)findViewById(R.id.diary_image_upload);

        button = (Button)findViewById(R.id.diary_upload_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                uploadImage(data.getData());
            }
        }
    }

    private void uploadImage(Uri data){
        FirebaseApp.initializeApp(this);
        if ( mImageStorageRef == null ) {
            mImageStorageRef = FirebaseStorage.getInstance().getReference("/images/").child(imagename);
        }
        mImageStorageRef.putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if ( task.isSuccessful() ) {
                    mPhotoUrl = mImageStorageRef.getDownloadUrl().toString();
                }
            }
        });
    }
}