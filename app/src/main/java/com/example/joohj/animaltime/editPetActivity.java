package com.example.joohj.animaltime;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class editPetActivity extends AppCompatActivity {

    EditText pet_name, pet_age, pet_sex, pet_weight;
    String ID, petID, strID, strPetID, strName, strAge, strSex, strWeight;

    Button btnEditPet, btnDeletePet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpet);

        pet_name = (EditText) findViewById(R.id.pet_name);
        pet_sex = (EditText) findViewById(R.id.pet_sex);
        pet_age = (EditText) findViewById(R.id.pet_age);
        pet_weight = (EditText) findViewById(R.id.pet_weight);

        btnEditPet = (Button) findViewById(R.id.edit);
        btnDeletePet = (Button) findViewById(R.id.delete);

        //인텐트로 넘겨줄 UserID
        Intent userID;
        userID = getIntent();
        ID = userID.getStringExtra("userID");
        petID = userID.getStringExtra("petID");
        //


        btnEditPet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                strID = ID;
                strPetID = petID;
                strName = pet_name.getText().toString();
                strSex = pet_sex.getText().toString();
                strAge = pet_age.getText().toString();
                strWeight = pet_weight.getText().toString();

                if(containsWhiteSpace(strSex) == true && containsWhiteSpace(strAge) == true && strName.length() != 0
                        && containsWhiteSpace(strWeight) == true) {
                    insertToDatabase(strID, strPetID, strName, strSex, strAge, strWeight);
                    Intent intent = new Intent(editPetActivity.this, myPetActivity.class);
                    intent.putExtra("userID",ID);
                    finish();
                } else {
                    Toast.makeText(editPetActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDeletePet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePet(ID, petID);
            }
        });
    }
    private void insertToDatabase(String Id, String PetId, String Name, String Sex, String Age, String Weight) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editPetActivity.this, "Please Wait", null, true, true);
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
                    String user_id = (String) params[0];
                    String pet_id = (String) params[1];
                    String name = (String) params[2];
                    String sex = (String) params[3];
                    String age = (String) params[4];
                    String weight = (String) params[5];


                    String link = "http://hyunjun0315.dothome.co.kr/php/petEdit.php?";
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("pet_id", "UTF-8") + "=" + URLEncoder.encode(PetId, "UTF-8");
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
        task.execute(Id, PetId, Name, Sex, Age, Weight);

    }

    public boolean deleteDatabase(String Id, String PetId) {
        class DeleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editPetActivity.this, "Please Wait", null, true, true);
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
                    String user_id = (String) params[0];

                    String link = "http://hyunjun0315.dothome.co.kr/php/deletePet.php?";
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("pet_id", "UTF-8") + "=" + URLEncoder.encode(PetId, "UTF-8");


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

        DeleteData task = new DeleteData();
        task.execute(Id, PetId);

        return true;
    }

    void deletePet(String deleteUserID, String deletePetID){

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("반려동물 정보 삭제를 진행하시겠습니까? 삭제하시면 되돌릴 수 없습니다.").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDatabase(deleteUserID, deletePetID);
                        Intent intent = new Intent(editPetActivity.this, MainActivity.class);
                        intent.putExtra("userID",ID);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(editPetActivity.this, myPetActivity.class);
                        intent.putExtra("userID",ID);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle("반려동물 정보 삭제");
        alert.show();
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
