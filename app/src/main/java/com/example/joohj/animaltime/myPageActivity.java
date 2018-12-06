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

public class myPageActivity extends AppCompatActivity {

    EditText passwordText, chkPasswordText, userNameText;
    String password, chkPassword, userName;
    Button submit, leave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        passwordText = (EditText) findViewById(R.id.signup_password);
        chkPasswordText = (EditText) findViewById(R.id.signup_password_chk);
        userNameText = (EditText) findViewById(R.id.signup_name);

        submit = (Button) findViewById(R.id.submit);
        leave = (Button) findViewById(R.id.leave);

        Intent userID;
        userID = getIntent();

        String identifier = userID.getStringExtra("userID");


        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUser(identifier);
            }
        });

        leave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveUser(identifier);
            }
        });
    }
    void submitUser(String identifier){
        password = passwordText.getText().toString();
        chkPassword = chkPasswordText.getText().toString();
        userName = userNameText.getText().toString();

        if(!password.equals(chkPassword)){
            Toast.makeText(myPageActivity.this, "동일한 비밀번호를 사용해주세요.", Toast.LENGTH_SHORT).show();
        } else if(containsWhiteSpace(password) == false) {
            Toast.makeText(myPageActivity.this, "비밀번호는 공백이 들어갈 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            insertToDatabase(identifier, password, userName);
            Intent intent = new Intent(myPageActivity.this, MainActivity.class);
            intent.putExtra("userID",identifier);
            startActivity(intent);
            finish();
        }

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


    void leaveUser(String identifier){

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("회원 탈퇴를 진행하시겠습니까? 탈퇴하시면 모든 정보를 잃게됩니다.").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDatabase(identifier);
                        Intent intent = new Intent(myPageActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle("회원 탈퇴");
        alert.show();
    }

    private void insertToDatabase(String Id, String Pw,String Name) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(myPageActivity.this, "Please Wait", null, true, true);
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
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String Name = (String) params[2];

                    String link = "http://hyunjun0315.dothome.co.kr/php/mypage.php?";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");

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
        task.execute(Id, Pw, Name);

    }

    public boolean deleteDatabase(String Id) {
        class DeleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(myPageActivity.this, "Please Wait", null, true, true);
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
                    String Id = (String) params[0];

                    String link = "http://hyunjun0315.dothome.co.kr/php/deleteuser.php?";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");

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
        task.execute(Id);

        return true;
    }

}
