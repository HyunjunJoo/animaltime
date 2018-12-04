package com.example.joohj.animaltime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
<<<<<<< HEAD
import android.widget.Button;
>>>>>>> ba6d695514c75cbc0e066a0631e792cd01f885b6

public class MainActivity extends AppCompatActivity {

    private Button btnCalendar;
    private Button btnHealth;
    private Button btnJournal;
    private Button btnFacility;
    private Button btnUserInformation;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
=======
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

>>>>>>> ba6d695514c75cbc0e066a0631e792cd01f885b6
    }


}
