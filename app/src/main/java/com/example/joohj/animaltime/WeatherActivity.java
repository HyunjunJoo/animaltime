package com.example.joohj.animaltime;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {

    TextView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weather = (TextView)findViewById(R.id.weather) ;

        try {
            URL url = new URL("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?"
                    + "serviceKey=mbhu%2FCuVnzj6F3CvilKdZv53AVLUsDzmTSvORQ06sye9kfVI6FFrMTw26u%2FRBUeW6KiCfsCYkI%2FMEf2ZQgbpsw%3D%3D"
                    + "&base_date=20181202&base_time=0500&nx=60&ny=127&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=xml");

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            int parseEvent = parser.getEventType();
        }
        catch (Exception e) {

        }
    }
}
