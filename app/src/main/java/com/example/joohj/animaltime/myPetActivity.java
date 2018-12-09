package com.example.joohj.animaltime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class myPetActivity extends AppCompatActivity {

    Button btnAddPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypet);

        //인텐트로 넘겨줄 UserID
        Intent userID;
        userID = getIntent();
        String ID = userID.getStringExtra("userID");

        btnAddPet = (Button) findViewById(R.id.btnAdd);


        ListView listview ;
        petListViewAdapter adapter = new petListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.petlist);
        listview.setAdapter(adapter);

//        adapter.addItem("초코","Female","5세","3.8kg") ;
//        adapter.addItem("흰둥이","Male","3세","1.7kg") ;
//        adapter.addItem("뽀삐","Female","2세","2.8kg") ;

        btnAddPet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myPetActivity.this, newPetActivity.class);
                intent.putExtra("userID",ID);
                startActivity(intent);
            }
        });
    }
}
