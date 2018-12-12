package com.example.joohj.animaltime;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    List<String> calendar_content_time_list = new ArrayList<String>(24);
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
        alarm_check = (TextView) findViewById(R.id.alarm_check);
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
//        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_selectable_list_item, calendar_content_time_list);
//        listView.setAdapter(adapter);


        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToDatabase(String.valueOf(System.currentTimeMillis()) + user_id, user_id, calendar_date, time.getText().toString(), content.getText().toString(), alarm_check.getText().toString());
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

                if(alarm_check.getText().toString().charAt(0) == 'o') {
                    // 알람 기능 구현
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


                    Intent intent = new Intent(getApplicationContext(), Broadcast.class);

                    PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


                    System.out.println(calendar_date.substring(0, 4)+ "\n" + calendar_date.substring(4, 6) + "\n" + calendar_date.substring(6, 8));
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.set(Integer.parseInt(calendar_date.substring(0, 4)),
                            Integer.parseInt(calendar_date.substring(4, 6)),
                            Integer.parseInt(calendar_date.substring(6, 8)),
                            Integer.parseInt(time.getText().toString().substring(0, 2)) - 1, Integer.parseInt(time.getText().toString().substring(3, 5)), 0);
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(String.valueOf(position));


                content.setText(calendar_list.get(position).calendar_content);
                time.setText(calendar_list.get(position).calendar_time);
                alarm_check.setText(calendar_list.get(position).alarm_check);


                modify_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();
                                modify_calendar_from_database(position, time.getText().toString(), content.getText().toString(), alarm_check.getText().toString());
                                setting_new_calendar(idx);
                                Looper.loop();
                            }
                        }).start();
                    }
                });

                delete_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();
                                delete_calendar_from_database(position);
                                calendar_content_time_list.remove(position);
                                calendar_list.remove(position);
                                calendar_list_size--;
                                if (calendar_list_size == 0)
                                    finish();
                                else{
                                    if (idx > 0)
                                        --idx;
                                }
                                setting_new_calendar(idx);
                                Looper.loop();
                            }
                        }).start();
                    }
                });
            }
        });


    }

    private void modify_calendar_from_database(int listIdx, String time, String content, String alarm_check) {
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpResponse response;

        try {
            System.out.println(String.valueOf(calendar_list.get(listIdx).calendar_no));
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/calendar_modify.php");

            nameValuePairs = new ArrayList<NameValuePair>(4);

            nameValuePairs.add(new BasicNameValuePair("calendar_no", calendar_list.get(listIdx).calendar_no));
            nameValuePairs.add(new BasicNameValuePair("calendar_time", time));
            nameValuePairs.add(new BasicNameValuePair("calendar_content",content));
            nameValuePairs.add(new BasicNameValuePair("alarm_check", alarm_check));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);
//            System.out.println(response + "\n" + strResponse);

            calendar_content_time_list.set(listIdx, content + "\n" + time);
            calendar_list.get(listIdx).calendar_time = time;
            calendar_list.get(listIdx).calendar_content = content;
            calendar_list.get(listIdx).alarm_check = alarm_check;

            //로그인 성공했을 때 echo로 값
            if (!strResponse.equalsIgnoreCase("1")) {
                Toast.makeText(CalendarList.this, "수정 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CalendarList.this, "수정 성공", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //Toast.makeText(Diarylist.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void delete_calendar_from_database(int listIdx){
        HttpPost httppost;
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpResponse response;

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://hyunjun0315.dothome.co.kr/php/calendar_delete.php");

            nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("calendar_no", calendar_list.get(listIdx).calendar_no));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

//            Toast.makeText(CalendarList.this, calendar_list.get(listIdx).calendar_no, Toast.LENGTH_SHORT).show();
            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);
//            System.out.println(response + "\n" + strResponse);


            //로그인 성공했을 때 echo로 값
            if (!strResponse.equalsIgnoreCase("1")) {
                Toast.makeText(CalendarList.this, "실패.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CalendarList.this, "일정 삭제", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            //Toast.makeText(Diarylist.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void setting_new_calendar(int idx) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_selectable_list_item, calendar_content_time_list);
                        listView.setAdapter(adapter);
                    }
                });

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

            nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
            nameValuePairs.add(new BasicNameValuePair("calendar_date", calendar_date));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httppost);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String strResponse = httpclient.execute(httppost, responseHandler);
            System.out.println(strResponse);

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

        calendar_list.clear();;
        calendar_content_time_list.clear();
        calendar_list_size = 0;

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
                        alarm_check = temp.substring(0, temp.length() - 1);
                        break;
                }
                if (flag == 5) {
                    flag = 0;
                    Calendar calendar = new Calendar(calendar_no, user_id, calendar_date, calendar_time, calendar_content, alarm_check);
                    calendar_list_size++;
                    calendar_content_time_list.add(calendar_content + "\n" + calendar_time);
                    calendar_list.add(calendar);
                } else {
                    flag++;
                }
            }
        }
    }
}