package com.example.joohj.animaltime;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalkRegistrationActivity extends AppCompatActivity {

    private EditText walkTime;
    private Button submit;
    private Button select_pet;
    private TextView pet_name;

    private ArrayList<Pet> pets = new ArrayList<Pet>();
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_registration);

        //user id 가져오기
        Intent userID;
        userID = getIntent();
        ID = userID.getStringExtra("userID");

        //user id에 해당하는 반려동물 정보 가져오기
        SelectPet networkTask = new SelectPet("http://hyunjun0315.dothome.co.kr/php/selectPet.php", null);
        networkTask.execute();

        select_pet = (Button)findViewById(R.id.select_pet_button);
        pet_name = (TextView)findViewById(R.id.pet_name);
        walkTime = (EditText)findViewById(R.id.total_walk_time);
        submit = (Button)findViewById(R.id.submit_button);

        List<String> selectedItems = new ArrayList<String> ();
        int selected_index;
        select_pet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] items = new String[pets.size()];
                final int [] selectedIndex = {0};
                for(int i = 0; i < pets.size(); i++) {
                    items[i] = pets.get(i).getName();
                    //state[i] = false;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(WalkRegistrationActivity.this);
                dialog.setTitle("반려동물을 선택하세요")
                        .setSingleChoiceItems( items, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedIndex[0] = which;
                                    }
                        } )
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(WalkRegistrationActivity.this, items[selectedIndex[0]], Toast.LENGTH_SHORT).show();
                                pet_name.setText(items[selectedIndex[0]]);
                                //selected_index = selectedIndex[0];
                            } }).create().show();
            }
        });

        submit.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View view) {
               long now = System.currentTimeMillis();
               Date day = new Date(now);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               SimpleDateFormat id = new SimpleDateFormat("yyyyMMddHHmmss");

               String pet_id = null;
               for(int i = 0; i < pets.size(); i++) {
                   String temp = pets.get(i).getName();
                   if(temp.equals(pet_name.getText())) {
                       pet_id = pets.get(i).getPet_id();
                   }
               }
               Log.d("walk" ,">>> selected pet id : " + pet_id);

               String date = sdf.format(day);
               String time = walkTime.getText().toString();
               String walk_id = "walk"+ pet_id + id.format(day);
               String walk_admin_id = "WlkAdm"+ pet_id + id.format(day);

               String url = "http://hyunjun0315.dothome.co.kr/php/insertWalk.php";

               //url = url + "walk_id=" + walk_id + "&date=" + date + "&time=" + time + "&user_id=" + ID;
               Log.d("walk" ,">>> request url : " + url);
               ContentValues content = new ContentValues();
               content.put("walk_id", walk_id);
               content.put("walk_admin_id", walk_admin_id);
               content.put("date", date);
               content.put("time", time);
               content.put("user_id", ID);
               content.put("pet_id", Integer.parseInt(pet_id));
               InsertWalk networkTask = new InsertWalk(url, content);
               networkTask.execute();

               finish();
           }
        });
    }

    public void sendRequest() {

    }

    public class InsertWalk extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public InsertWalk(String url, ContentValues values) {
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
                buffer.append("user_id").append("=").append(values.get("user_id")).append("&");
                buffer.append("pet_id").append("=").append(values.get("pet_id")).append("&");
                buffer.append("walk_id").append("=").append(values.get("walk_id")).append("&");
                buffer.append("walk_admin_id").append("=").append(values.get("walk_admin_id")).append("&");
                buffer.append("date").append("=").append(values.get("date")).append("&");
                buffer.append("time").append("=").append(values.get("time"));

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

        }
    }

    public class SelectPet extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public SelectPet(String url, ContentValues values) {
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
            try {
                JSONArray results = new JSONArray(s);
                String pet_id = null, user_id = null, name = null, sex, age, weight;
                Log.d("walk" ,">>> json: " + s);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jObject = results.getJSONObject(i);
                    pet_id = jObject.getString("pet_id");
                    user_id = jObject.getString("user_id");
                    name = jObject.getString("name");
                    String result = ">>> json to string : " + pet_id + " / " + user_id + " / " + name;
                    Log.d("walk" , result);
                    Pet pet = new Pet(pet_id, user_id, name);
                    //if (pet != null) {
                        pets.add(pet);
                    //}
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class Pet {
        private String pet_id;
        private String user_id;
        private String name;

        public Pet (String pet_id, String user_id, String name) {
            this.pet_id = pet_id;
            this.user_id = user_id;
            this.name = name;
        }

        public String getPet_id() {
            return pet_id;
        }

        public void setPet_id(String pet_id) {
            this.pet_id = pet_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
