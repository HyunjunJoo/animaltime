package com.example.joohj.animaltime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button btnSchedule;
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
        btnSchedule = (Button) findViewById(R.id.schedule_button);

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

            }
        });

        btnHealth.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSchedule.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
