package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.os.AsyncTask;
import android.widget.Toast;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import android.content.Intent;
import java.net.MalformedURLException;




public class SignupActivity extends AppCompatActivity {

    EditText signup_id, signup_password, signup_name;
    String strNewID, strNewPassword, strNewname;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        signup_id = (EditText) findViewById(R.id.signup_id);
        signup_password = (EditText) findViewById(R.id.signup_password);
        signup_name = (EditText) findViewById(R.id.signup_name);

    }

    //insert 함수
    //회원가입 데이터 넣기
    private void insertToDatabase(String Id, String Pw,String Name) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
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

                    String link = "http://hyunjun0315.dothome.co.kr/php/signup.php?";
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


    public void onClicked(View view) {
        strNewID = signup_id.getText().toString();
        strNewPassword = signup_password.getText().toString();
        strNewname = signup_name.getText().toString();

        if(containsWhiteSpace(strNewID) == true && containsWhiteSpace(strNewname) == true && containsWhiteSpace(strNewname) == true) {
            insertToDatabase(strNewID, strNewPassword, strNewname);
            startActivity((new Intent(SignupActivity.this, loginActivity.class)));
            finish();
        } else {
            Toast.makeText(SignupActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
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

}