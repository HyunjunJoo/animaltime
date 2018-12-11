package com.example.joohj.animaltime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    Button btn_lookup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        btn_lookup = (Button) findViewById(R.id.btn_lookup);

        Intent userID;
        userID = getIntent();
        String user_id = userID.getStringExtra("userID");


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                                 @Override
                                                 public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                                     btn_lookup.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             Intent intent = new Intent(getApplicationContext(), CalendarList.class);
                                                             intent.putExtra("userID", user_id);
                                                             intent.putExtra("calendar_date", String.valueOf(year) + String.valueOf(month) + String.valueOf(dayOfMonth));

                                                             startActivity(intent);
                                                             finish();
                                                         }
                                                     });
                                                 }
                                             });
    }
}
