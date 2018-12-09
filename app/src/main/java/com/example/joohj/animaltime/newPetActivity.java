package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class newPetActivity extends AppCompatActivity {

    EditText pet_name, pet_age, pet_sex, pet_weight;
    String strID, strName, strAge, strSex, strWeight;
    Button btnAddPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpet);

        pet_name = (EditText) findViewById(R.id.pet_name);
        pet_sex = (EditText) findViewById(R.id.pet_sex);
        pet_age = (EditText) findViewById(R.id.pet_age);
        pet_weight = (EditText) findViewById(R.id.pet_weight);

        btnAddPet = (Button) findViewById(R.id.register_newpet);

        //인텐트로 넘겨줄 UserID
        Intent userID;
        userID = getIntent();
        String ID = userID.getStringExtra("userID");
        //

        btnAddPet.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                strID = ID;
                strName = pet_name.getText().toString();
                strSex = pet_sex.getText().toString();
                strAge = pet_age.getText().toString();
                strWeight = pet_weight.getText().toString();


                if(containsWhiteSpace(strSex) == true && containsWhiteSpace(strAge) == true && strName.length() != 0
                        && containsWhiteSpace(strWeight) == true) {
                    insertToDatabase(strID, strName, strSex, strAge, strWeight);
                    startActivity((new Intent(newPetActivity.this, myPetActivity.class)));
                    finish();
                } else {
                    Toast.makeText(newPetActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertToDatabase(String Id, String Name, String Sex, String Age, String Weight) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(newPetActivity.this, "Please Wait", null, true, true);
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
                    String id = (String) params[0];
                    String name = (String) params[1];
                    String sex = (String) params[2];
                    String age = (String) params[3];
                    String weight = (String) params[4];


                    String link = "http://hyunjun0315.dothome.co.kr/php/petRegister.php?";
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(Sex, "UTF-8");
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(Age, "UTF-8");
                    data += "&" + URLEncoder.encode("weight", "UTF-8") + "=" + URLEncoder.encode(Weight, "UTF-8");

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
        task.execute(Id, Name, Sex, Age, Weight);

    }

    public static boolean containsWhiteSpace(String testCode){

        if(testCode.length() == 0) {
            return false;
        }
        for(int i = 0; i < testCode.length(); i++) {
            if(Character.isWhitespace(testCode.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
