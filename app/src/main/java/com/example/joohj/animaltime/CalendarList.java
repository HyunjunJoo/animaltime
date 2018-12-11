package com.example.joohj.animaltime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CalendarList extends AppCompatActivity {

    Button modify_button, delete_button, new_button;
    TextView content, time, alarm_check;
    ListView listView;

    List<Calendar> calendar_list = new ArrayList<Calendar>(24);
    private ArrayAdapter adapter;
    private int calendar_list_size = 0;
    private int idx = 0;
    private String calendar_date = null;
    private String calendar_data = null;
    private String user_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_list);

        modify_button = (Button) findViewById(R.id.calendar_modify_button);
        delete_button = (Button) findViewById(R.id.calendar_delete_button);
        new_button = (Button) findViewById(R.id.calendar_new_button);
        content = (TextView) findViewById(R.id.calendar_content);
        time = (TextView) findViewById(R.id.calendar_time);
        alarm_check = (TextView) findViewById(R.id.calendar_time);
        listView = (ListView) findViewById(R.id.calendar_listView);

        Intent userID;
        userID = getIntent();
        user_id = userID.getStringExtra("userID");
        Intent date;
        date = getIntent();
        calendar_date = date.getStringExtra("calendar_date");

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                calendar_data = get_calendar_from_database(user_id, calendar_date);
                if (calendar_data == null)
                    return;
                parsing_calendar(calendar_list, calendar_data);
                setting_new_calendar(idx);
                Looper.loop();
            }
        }).start();
        Toast.makeText(getApplicationContext(), calendar_data, Toast.LENGTH_LONG).show();
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice, calendar_list);
        listView.setAdapter(adapter);

        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToDatabase("1", user_id, calendar_date, time.getText().toString(), content.getText().toString(), alarm_check.getText().toString());
            }
        });

    }

    private void setting_new_calendar(int idx) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }).start();
    }

    private String get_calendar_from_database(String user_id, String calendar_date) {
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpResponse response;

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/calendar_download.php");

            nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
//            nameValuePairs.add(new BasicNameValuePair("calendar_date", calendar_date));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);

            //로그인 성공했을 때 echo로 값
            if (!strResponse.equalsIgnoreCase(user_id)) {
                return strResponse;

            } else {
                Toast.makeText(CalendarList.this, "일정이 없습니다.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (Exception e) {
            //Toast.makeText(Diarylist.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void insertToDatabase(String calendar_no, String user_id, String calendar_date, String calendar_time, String calendar_content, String alarm_check) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CalendarList.this, "Please Wait", null, true, true);
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
                    String pcalendar_no = (String) params[0];
                    String puser_id = (String) params[1];
                    String pcalendar_date = (String) params[2];
                    String pcalendar_time = (String) params[3];
                    String pcalendar_content = (String) params[4];
                    String palarm_check = (String) params[5];
                    String link = "http://hyunjun0315.dothome.co.kr/php/calendar.php?";
                    String data = URLEncoder.encode("calendar_no", "UTF-8") + "=" + URLEncoder.encode(pcalendar_no, "UTF-8");
                    data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(puser_id, "UTF-8");
                    data += "&" + URLEncoder.encode("calendar_date", "UTF-8") + "=" + URLEncoder.encode(pcalendar_date, "UTF-8");
                    data += "&" + URLEncoder.encode("calendar_time", "UTF-8") + "=" + URLEncoder.encode(pcalendar_time, "UTF-8");
                    data += "&" + URLEncoder.encode("calendar_content", "UTF-8") + "=" + URLEncoder.encode(pcalendar_content, "UTF-8");
                    data += "&" + URLEncoder.encode("alarm_check", "UTF-8") + "=" + URLEncoder.encode(palarm_check, "UTF-8");

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
        task.execute(calendar_no, user_id, calendar_date, calendar_time, calendar_content, alarm_check);
    }

    private void parsing_calendar(List<Calendar> calendar_list, String calendar_data) {

        String calendar_no = null;
        String user_id = null;
        String calendar_date = null;
        String calendar_time = null;
        String calendar_content = null;
        String alarm_check = null;

        String temp = null;

        int flag = 0;
        int start_idx = 0;

        for (int i = 0; i < calendar_data.length(); ++i) {
            if (calendar_data.charAt(i) == '\n') {
                temp = calendar_data.substring(start_idx, i);
                start_idx = i + 1;

                switch (flag) {
                    case 0:
                        calendar_no = temp;
                        break;
                    case 1:
                        user_id = temp;
                        break;
                    case 2:
                        calendar_date = temp;
                        break;
                    case 3:
                        calendar_time = temp;
                        break;
                    case 4:
                        calendar_content = temp;
                        break;
                    case 5:
                        alarm_check = temp;
                        break;
                }
                if (flag == 5) {
                    flag = 0;
                    Calendar calendar = new Calendar(calendar_no, user_id, calendar_date, calendar_time, calendar_content, alarm_check);
                    calendar_list_size++;
                    calendar_list.add(calendar);
                } else {
                    flag++;
                }
            }
        }
    }
}