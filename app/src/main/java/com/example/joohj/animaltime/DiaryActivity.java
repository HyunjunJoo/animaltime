package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    Button upload_button, save_button, test_button;
    String imagename = null;
    EditText context, title;
    ImageView test_imageview;
    private StorageReference mImageStorageRef;
    private String photoUrl = null;
    private String diary_id = null;
    private String diary_data = null;
    private List<Diary> diary_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_diary);

        title = (EditText)findViewById(R.id.diary_title);
        context = (EditText)findViewById(R.id.diary_context);
        upload_button = (Button)findViewById(R.id.diary_upload_button);
        save_button = (Button)findViewById(R.id.diary_save_button);
        test_button = (Button)findViewById(R.id.diary_test_button);
        diary_list = new ArrayList<Diary>(100);
        test_imageview = (ImageView)findViewById(R.id.diary_image);

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
                diary_id = date.substring(0, 10) + date.substring(11) + user_id;

                insertToDatabase(diary_id, user_id, title.getText().toString(), context.getText().toString(), date, photoUrl);
            }
        });

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        diary_data = get_diary_from_database(user_id);
                        parsing_diary(diary_list, diary_data);
                        new LoadImage().execute(diary_list.get(0).url);
                        Looper.loop();
                    }
                }).start();
            }
        });

    }
    private class LoadImage extends AsyncTask<String, String, Bitmap>{
        ProgressDialog pDialog;
        Bitmap mBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DiaryActivity.this);
            pDialog.setMessage("이미지 로딩중입니다...");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                mBitmap = BitmapFactory
                        .decodeStream((InputStream) new URL(args[0])
                                .getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mBitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                test_imageview.setImageBitmap(image);
                pDialog.dismiss();

            } else {
                pDialog.dismiss();
                Toast.makeText(DiaryActivity.this, "이미지가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void parsing_diary(List<Diary> diary_list, String diary_data){

        String user_id = null;
        String diary_id = null;
        String title = null;
        String context = null;
        String date = null;
        String url = null;

        String temp = null;

        int flag = 0;
        int start_idx = 0;

        for (int i = 0; i < diary_data.length(); ++i){
            if (diary_data.charAt(i) == '\n'){
                temp = diary_data.substring(start_idx, i);
                start_idx = i + 1;

                switch (flag){
                    case 0:
                        diary_id = temp;
                        break;
                    case 1:
                        user_id = temp;
                        break;
                    case 2:
                        title = temp;
                        break;
                    case 3:
                        context = temp;
                        break;
                    case 4:
                        date = temp;
                        break;
                    case 5:
                        url = temp;
                        break;
                }
                if (flag == 5){
                    flag = 0;
                    Diary diary = new Diary(diary_id, user_id, title, context, date, url);
                    diary_list.add(diary);
                }else{
                    flag++;
                }
            }
        }
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

        imagename = getDate();
        mImageStorageRef = FirebaseStorage.getInstance().getReference("/images/").child(imagename);
        UploadTask uploadTask = mImageStorageRef.putFile(data);

         // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return mImageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    photoUrl = downloadUri.toString();
                } else {
                    // Handle failures
                    // ...
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

    private String get_diary_from_database(String user_id) {
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpResponse response;

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/diary_download.php");

            nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("user_id", user_id));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);

            //로그인 성공했을 때 echo로 값
            if (!strResponse.equalsIgnoreCase(user_id)) {
                return strResponse;

            } else {
                Toast.makeText(DiaryActivity.this, "다이어리가 없습니다.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        catch(Exception e)
        {
            Toast.makeText(DiaryActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}