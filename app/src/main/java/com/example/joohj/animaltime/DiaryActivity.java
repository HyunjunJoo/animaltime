package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {

    Button upload_button, save_button;
    String imagename = null;
    EditText context, title;
    private StorageReference mImageStorageRef;
    private String photoUrl = null;
    private String diary_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_diary);

        title = (EditText)findViewById(R.id.diary_title);
        context = (EditText)findViewById(R.id.diary_context);
        upload_button = (Button)findViewById(R.id.diary_upload_button);
        save_button = (Button)findViewById(R.id.diary_save_button);

        Intent userID;
        userID = getIntent();
        String user_id = userID.getStringExtra("userID");

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = getDate();
                diary_id = date.substring(0, 9) + date.substring(11) + user_id;

                insertToDatabase(diary_id, user_id, title.toString(), context.toString(), date, photoUrl);
                Toast.makeText(DiaryActivity.this,  "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(date);
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
                    photoUrl = mImageStorageRef.getDownloadUrl().toString();
                }
            }
        });
    }

    private void insertToDatabase(String diary_id, String user_id, String title, String context, String date, String url) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DiaryActivity.this, "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String pdiary_id = (String) params[0];
                    String puser_id = (String) params[1];
                    String ptitle = (String) params[2];
                    String pcontext = (String) params[3];
                    String pdate = (String) params[4];
                    String purl = (String) params[5];

                    String link = "http://hyunjun0315.dothome.co.kr/php/diary.php?";
                    String data = URLEncoder.encode("diary_id", "UTF-8") + "=" + URLEncoder.encode(pdiary_id, "UTF-8");
                    data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(puser_id, "UTF-8");
                    data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(ptitle, "UTF-8");
                    data += "&" + URLEncoder.encode("context", "UTF-8") + "=" + URLEncoder.encode(pcontext, "UTF-8");
                    data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(pdate, "UTF-8");
                    data += "&" + URLEncoder.encode("url", "UTF-8") + "=" + URLEncoder.encode(purl, "UTF-8");

                    link+=data;

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(diary_id, user_id, title, context, date, url);
    }
}