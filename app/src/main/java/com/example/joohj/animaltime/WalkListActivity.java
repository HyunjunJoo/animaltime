package com.example.joohj.animaltime;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WalkListActivity extends AppCompatActivity {

    private Button select_pet;
    private TextView pet_name;
    private ListView walk_list;

    private ArrayList<Pet> pets = new ArrayList<Pet>();
    private ArrayList<Walk> walks = new ArrayList<Walk>();
    private MyListAdapter adapter;
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_list);

        //user id 가져오기
        Intent userID;
        userID = getIntent();
        ID = userID.getStringExtra("userID");

        //user id에 해당하는 반려동물 정보 가져오기
        SelectPet networkTask = new SelectPet("http://hyunjun0315.dothome.co.kr/php/selectPet.php", null);
        networkTask.execute();

        select_pet = (Button)findViewById(R.id.select_pet_button);
        pet_name = (TextView)findViewById(R.id.pet_name);
        walk_list = (ListView)findViewById(R.id.walk_list);

        //반려동물 선택
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

                if(items.length == 0) {
                    Toast.makeText(WalkListActivity.this, "등록된 반려동물이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WalkListActivity.this);
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
                                    Toast.makeText(WalkListActivity.this, items[selectedIndex[0]], Toast.LENGTH_SHORT).show();
                                    pet_name.setText(items[selectedIndex[0]]);

                                    //반려동물 선택 후 해당 산책 기록 보여주기
                                    String pet_id = null;
                                    for(int i = 0; i < pets.size(); i++) {
                                        String temp = pets.get(i).getName();
                                        if(temp.equals(pet_name.getText())) {
                                            pet_id = pets.get(i).getPet_id();
                                        }
                                    }

                                    ContentValues content = new ContentValues();
                                    content.put("pet_id", Integer.parseInt(pet_id));
                                    content.put("user_id", ID);

                                    SelectWalk selectWalk = new SelectWalk("http://hyunjun0315.dothome.co.kr/php/selectWalk.php", content);
                                    selectWalk.execute();

                                } }).create().show();
                }
            }
        });

        walk_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Walk selectedWalk = walks.get(i);
                Intent intent = new Intent(getApplicationContext(), WalkModifyActivity.class);
                intent.putExtra("walk_id", selectedWalk.getWalk_id());
                intent.putExtra("pet_name",  selectedWalk.getPet_name());
                intent.putExtra("walk_date",  selectedWalk.getWalk_date());
                intent.putExtra("walk_time",  selectedWalk.getWalk_time());
                intent.putExtra("walk_admin_id",  selectedWalk.getWalk_admin_id());
                startActivity(intent);
                finish();
            }
        });

    }

    public class SelectWalk extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public SelectWalk(String url, ContentValues values) {
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
                buffer.append("pet_id").append("=").append(values.get("pet_id"));

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
                String pet_name = null, walk_date = null, walk_time = null, walk_id = null, walk_admin_id = null, weight;
                Log.d("walk" ,">>> json: " + s);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject jObject = results.getJSONObject(i);
                    pet_name = jObject.getString("pet_name");
                    walk_id = jObject.getString("walk_id");
                    walk_date = jObject.getString("walk_date");
                    walk_time = jObject.getString("walk_time");
                    walk_admin_id = jObject.getString("walk_admin_id");
                    String result = ">>> json to string : " + pet_name + " / " + walk_id + " / " + walk_date + " / " + walk_time + " / " + walk_admin_id;
                    Log.d("walk-list" , result);
                    Walk walk = new Walk(pet_name, walk_id, walk_date, walk_time, walk_admin_id);
                    walks.add(walk);
                }

                adapter = new MyListAdapter(WalkListActivity.this, walks);
                walk_list.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyListAdapter extends BaseAdapter{
        Context context;
        ArrayList<Walk> walk_item_list;
        TextView date;
        TextView time;

        public MyListAdapter(Context context, ArrayList<Walk> walk_item_list) {
            this.context = context;
            this.walk_item_list = walk_item_list;
        }

        @Override
        public int getCount() {
            return this.walk_item_list.size();
        }

        @Override
        public Object getItem(int i) {
            return walk_item_list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.custom_listview, null);
                date = (TextView)view.findViewById(R.id.date);
                time = (TextView)view.findViewById(R.id.time);
                date.setText(walk_item_list.get(i).getWalk_date());
                time.setText(walk_item_list.get(i).getWalk_time());
            }

            //date = (TextView)findViewById(R.id.date);
            //time = (TextView)findViewById(R.id.time);


            return view;
        }
    }

    public class Walk {
        private String pet_name;
        private String walk_id;
        private String walk_date;
        private String walk_time;
        private String walk_admin_id;

        public Walk(String pet_name, String walk_id, String walk_date, String walk_time, String walk_admin_id) {
            this.pet_name = pet_name;
            this.walk_id = walk_id;
            this.walk_date = walk_date;
            this.walk_time = walk_time;
            this.walk_admin_id = walk_admin_id;
        }

        public String getWalk_admin_id() {
            return walk_admin_id;
        }

        public void setWalk_admin_id(String walk_admin_id) {
            this.walk_admin_id = walk_admin_id;
        }

        public String getPet_name() {
            return pet_name;
        }

        public void setPet_name(String pet_name) {
            this.pet_name = pet_name;
        }

        public String getWalk_id() {
            return walk_id;
        }

        public void setWalk_id(String walk_id) {
            this.walk_id = walk_id;
        }

        public String getWalk_date() {
            return walk_date;
        }

        public void setWalk_date(String walk_date) {
            this.walk_date = walk_date;
        }

        public String getWalk_time() {
            return walk_time;
        }

        public void setWalk_time(String walk_time) {
            this.walk_time = walk_time;
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

                Log.d("walk-list" ,">>> request...");

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
                Log.d("walk-list" ,">>> request response : " + sb.toString());
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
                    Log.d("walk-list" , result);
                    Pet pet = new Pet(pet_id, user_id, name);
                    pets.add(pet);

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
