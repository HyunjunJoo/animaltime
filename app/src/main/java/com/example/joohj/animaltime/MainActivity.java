package com.example.joohj.animaltime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnCalendar;
    private Button btnHealth;
    private Button btnJournal;
    private Button btnFacility;
    private Button btnUserInformation;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFacility = (Button) findViewById(R.id.facility_button);
        btnUserInformation = (Button) findViewById(R.id.userInformation_button);
        btnJournal = (Button) findViewById(R.id.journal_button);
        btnHealth = (Button) findViewById(R.id.health_button);
        btnCalendar = (Button) findViewById(R.id.calendar_button);

        btnFacility.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUserInformation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnJournal.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        btnHealth.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCalendar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });

    }
}
