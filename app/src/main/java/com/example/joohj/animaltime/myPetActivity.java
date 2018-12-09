package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

public class myPetActivity extends AppCompatActivity {

    Button btnAddPet;
    String petJsonString;
    petListViewAdapter adapter = new petListViewAdapter();
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypet);

        //인텐트로 넘겨줄 UserID
        Intent userID;
        userID = getIntent();
        ID = userID.getStringExtra("userID");

        btnAddPet = (Button) findViewById(R.id.btnAdd);
        
        ListView listview ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.petlist);
        listview.setAdapter(adapter);

        GetData task = new GetData("http://hyunjun0315.dothome.co.kr/php/petList.php?",null);
        task.execute();

//        adapter.addItem("초코","Female","5세","3.8kg") ;
//        adapter.addItem("흰둥이","Male","3세","1.7kg") ;
//        adapter.addItem("뽀삐","Female","2세","2.8kg") ;

        btnAddPet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myPetActivity.this, newPetActivity.class);
                intent.putExtra("userID",ID);
                startActivity(intent);
            }
        });
    }

    public class GetData extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public GetData (String url, ContentValues values) {
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
                buffer.append("user_id").append("=").append(ID);

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    Log.d("TAG", "line : " + line);
                    sb.append(line);
                    break;
                }

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
            try {
                JSONArray results = new JSONArray(s);

                for (int i = 0; i < results.length(); i++) {

                    JSONObject jObject = results.getJSONObject(i);

                    String pet_name = jObject.getString("name");
                    String pet_sex = jObject.getString("sex");
                    String pet_age = jObject.getString("age");
                    String pet_weight = jObject.getString("weight");

                    Log.d("TAG", "petname : " + pet_name);

                    adapter.addItem(pet_name,pet_sex,pet_age,pet_weight) ;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
