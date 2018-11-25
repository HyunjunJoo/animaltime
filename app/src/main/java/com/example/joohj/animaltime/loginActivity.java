package com.example.joohj.animaltime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.widget.TextView;

import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import java.util.List;
import android.os.Looper;
import android.util.Log;

public class loginActivity extends AppCompatActivity {


    EditText login_id, password;
    String strLogin, strPassword;
    ProgressDialog dialog = null;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void loginClicked(View v) {

        dialog = ProgressDialog.show(loginActivity.this, "",
                "Validating user...", true);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                login();
                Looper.loop();
            }
        }).start();
    }

    void login() {
        try {
            Toast.makeText(loginActivity.this, "로그인 함수실행", Toast.LENGTH_SHORT).show();
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/login.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("username", login_id.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));

            strLogin = login_id.getText().toString();
            strPassword = password.getText().toString();

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            Log.d("loginActivity", strLogin + strPassword);

            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });

            Log.d("loginActivity", "runOnUiThread");

            //로그인 성공했을 때 echo로 값
            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(loginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    }
                });

                //성공하고 다른 activity로 넘어감
                startActivity((new Intent(getApplicationContext(),MainActivity.class)));
                finish();

            } else {
                Toast.makeText(loginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    //회원가입
    public void signUpClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }


    public String getUserID() {
        return strLogin;
    }
}
