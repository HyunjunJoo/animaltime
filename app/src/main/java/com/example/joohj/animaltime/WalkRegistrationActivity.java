package com.example.joohj.animaltime;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WalkRegistrationActivity extends AppCompatActivity {

    private EditText walkTime;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_registration);

        walkTime = (EditText)findViewById(R.id.total_walk_time);
        submit = (Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View view) {
               long now = System.currentTimeMillis();
               Date day = new Date(now);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat id = new SimpleDateFormat("yyyyMMddHHmmss");

               String date = sdf.format(day);
               String time = walkTime.getText().toString();
               String walk_id = "walk"+ id.format(day);
               String url = "http://hyunjun0315.dothome.co.kr/php/insertWalk.php?";

               url = url + "walk_id=" + walk_id + "&date=" + date + "&time=" + time;
               Log.d("walk" ,">>> request url : " + url);
               NetworkTask networkTask = new NetworkTask(url, null);
               networkTask.execute();
           }
        });
    }

    public void sendRequest() {
        String text = submit.getText().toString();


    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            //Request 전송
            try {
                URL url = new URL(this.url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                //con.setDoOutput(true);
                //OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

                Log.d("walk" ,">>> request...");
                //wr.write(result);
                //wr.flush();
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("Context_Type", "application/json;charset=UTF-8");

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.d("walk" ,">>> request response : " + sb.toString());
                return sb.toString();
            }
            catch (MalformedURLException e) {

            }
            catch (IOException e) {

            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
