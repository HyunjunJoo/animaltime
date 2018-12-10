package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Diarylist extends AppCompatActivity {

    Button prev_button, next_button, modify_button, delete_button, new_button;
    TextView title, context, date;
    ImageView image;

    List<Diary> diary_list = new ArrayList<Diary>(100);
    private String diary_data = null;
    private int idx = 0;
    private int diary_list_size = 0;
    private String user_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diarylist);

        prev_button = (Button) findViewById(R.id.diarylist_prev_button);
        next_button = (Button) findViewById(R.id.diarylist_next_button);
        delete_button = (Button) findViewById(R.id.diarylist_delete_button);
        modify_button = (Button) findViewById(R.id.diarylist_modify_button);
        new_button = (Button) findViewById(R.id.diarylist_new_button);

        title = (TextView) findViewById(R.id.diarylist_title);
        context = (TextView) findViewById(R.id.diarylist_context);
        date = (TextView) findViewById(R.id.diarylist_date);

        image = (ImageView) findViewById(R.id.diarylist_image);

        Intent userID;
        userID = getIntent();
        user_id = userID.getStringExtra("userID");

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                diary_data = get_diary_from_database(user_id);
                if (diary_data == null)
                    return;
                parsing_diary(diary_list, diary_data);
                setting_new_diary(idx);
                Looper.loop();
            }
        }).start();

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idx != 0){
                    idx--;
                    setting_new_diary(idx);
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idx + 1 < diary_list_size){
                    idx++;
                    setting_new_diary(idx);
                }
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        delete_diary_from_database();
                        diary_list.remove(idx);
                        diary_list_size--;
                        if (diary_list_size == 0)
                            finish();
                        else{
                            if (idx > 0)
                                --idx;
                        }
                        setting_new_diary(idx);
                        Looper.loop();
                    }
                }).start();
            }
        });

        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                intent.putExtra("userID", user_id);
                intent.putExtra("diary_id", diary_list.get(idx).diary_id);
                intent.putExtra("title", diary_list.get(idx).title);
                intent.putExtra("context", diary_list.get(idx).context);
                intent.putExtra("url", diary_list.get(idx).url);
                startActivity(intent);
                finish();
            }
        });

        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                intent.putExtra("userID", user_id);
                startActivity(intent);
            }
        });

    }

    private void setting_new_diary(int idx){

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                new LoadImage().execute(diary_list.get(idx).url);
                title.setText(diary_list.get(idx).title);
                context.setText(diary_list.get(idx).context);
                date.setText(diary_list.get(idx).date);
                Looper.loop();
            }
        }).start();
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
                Toast.makeText(Diarylist.this, "다이어리가 없습니다.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        catch(Exception e)
        {
            //Toast.makeText(Diarylist.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void delete_diary_from_database(){
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpResponse response;

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/diary_delete.php");

            nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("diary_id", diary_list.get(idx).diary_id));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);

            //로그인 성공했을 때 echo로 값
            if (strResponse.equalsIgnoreCase("1")) {
                Toast.makeText(Diarylist.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                //return strResponse;

            } else {
                Toast.makeText(Diarylist.this, "삭제실패: "+strResponse, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        catch(Exception e)
        {
            //Toast.makeText(Diarylist.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
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
                    diary_list_size++;
                    diary_list.add(diary);
                }else{
                    flag++;
                }
            }
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        ProgressDialog pDialog;
        Bitmap mBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Diarylist.this);
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

        protected void onPostExecute(Bitmap imagebit) {

            if (imagebit != null) {
                image.setImageBitmap(imagebit);
                pDialog.dismiss();

            } else {
                pDialog.dismiss();
                Toast.makeText(Diarylist.this, "이미지가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

}
