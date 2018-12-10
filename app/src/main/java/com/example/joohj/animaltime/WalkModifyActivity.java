package com.example.joohj.animaltime;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WalkModifyActivity extends AppCompatActivity {

    private TextView pet_name_view;
    private TextView walk_date_view;
    private EditText walk_time_view;
    private Button update;
    private Button delete;

    private String walk_id;
    private String pet_name;
    private String walk_date;
    private String walk_time;
    private String walk_admin_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_modify);

        Intent data = getIntent();
        walk_id = data.getStringExtra("walk_id");
        pet_name = data.getStringExtra("pet_name");
        walk_date = data.getStringExtra("walk_date");
        walk_time = data.getStringExtra("walk_time");
        walk_admin_id = data.getStringExtra("walk_admin_id");

        pet_name_view = (TextView)findViewById(R.id.pet_name);
        pet_name_view.setText(pet_name);

        walk_date_view = (TextView)findViewById(R.id.walk_date);
        walk_date_view.setText(walk_date);

        walk_time_view = (EditText)findViewById(R.id.walk_time);
        walk_time_view.setText(walk_time);

        update = (Button)findViewById(R.id.update_button);
        update.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp_time = walk_time_view.getText().toString();
                String url = "http://hyunjun0315.dothome.co.kr/php/updateWalk.php";

                ContentValues content = new ContentValues();
                content.put("walk_id", walk_id);
                content.put("walk_time", tmp_time);

                UpdateWalk updateWalk = new UpdateWalk(url, content);
                updateWalk.execute();
            }
        });

        delete = (Button)findViewById(R.id.delete_button);
        delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://hyunjun0315.dothome.co.kr/php/deleteWalk.php";

                ContentValues content = new ContentValues();
                content.put("walk_id", walk_id);
                content.put("walk_admin_id", walk_admin_id);

                DeleteWalk deleteWalk = new DeleteWalk(url, content);
                deleteWalk.execute();
            }
        });
    }

    public class UpdateWalk extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public UpdateWalk(String url, ContentValues values) {
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
                con.setDefaultUseCaches(false);
                con.setDefaultUseCaches(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");

                Log.d("walk" ,">>> request...");

                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                StringBuffer buffer = new StringBuffer();
                buffer.append("walk_id").append("=").append(values.get("walk_id")).append("&");
                buffer.append("walk_time").append("=").append(values.get("walk_time"));

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

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
            finish();
        }
    }

    public class DeleteWalk extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public DeleteWalk(String url, ContentValues values) {
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
                con.setDefaultUseCaches(false);
                con.setDefaultUseCaches(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");

                Log.d("walk" ,">>> request...");

                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                StringBuffer buffer = new StringBuffer();
                buffer.append("walk_id").append("=").append(values.get("walk_id")).append("&");
                buffer.append("walk_admin_id").append("=").append(values.get("walk_admin_id"));

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

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
            finish();
        }
    }
}
