package com.example.joohj.animaltime;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {

    private TextView weather;
    private ImageView sky;
    private ImageView rain;
    private Button walk;

    public static StringBuilder sb;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private int permissionCheck;

    private boolean isGPSGranted = false;
    private String provider;
    private double longitude;
    private double latitude;
    private double altitude;

    //위치정보가 없을 때
    private final int DEFAULT_NX = 60;
    private final int DEFAULT_NY = 127;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 정보 권한에 대한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "날씨 정보 제공을 위해 위치 정보 권한에 대한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        else {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                Log.d("location", ">>> Location is null");
                showDefaultWeather();
            }
            else {
                if (location.getProvider() == null) {
                    Log.d("location", ">>> Location provider is null");
                    showDefaultWeather();
                }
                else {
                    Log.d("location", ">>> Get location");
                    provider = location.getProvider();
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    altitude = location.getAltitude();
                }
            }
        }

        walk = (Button)findViewById(R.id.walk_button);
        walk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WalkRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDefaultWeather() {
        String [] baseDateTime = findBaseDateTime();
        showWeather(baseDateTime[0], baseDateTime[1], DEFAULT_NX, DEFAULT_NY);
    }

    public void showWeather(String base_date, String base_time, int nx, int ny) {
        String url = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?"
                + "serviceKey=mbhu%2FCuVnzj6F3CvilKdZv53AVLUsDzmTSvORQ06sye9kfVI6FFrMTw26u%2FRBUeW6KiCfsCYkI%2FMEf2ZQgbpsw%3D%3D";
        url = url + "&base_date=" + base_date + "&base_time=" + base_time + "&nx=" + nx + "&ny=" + ny + "&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=xml";

        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();
    }

    //기상청 동네예보를 부를 때 주어야 하는 Base_date와 Base_time을 구한다.
    public String [] findBaseDateTime() {
        Calendar calendar = Calendar.getInstance();

        String base_date;
        String base_time;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour < 2) {
            calendar.add(Calendar.DATE, -1);
            base_time = "2300";
        }
        else if ((hour >= 2) && (hour < 5)) {
            base_time = "0200";
        }
        else if ((hour >= 5) && (hour < 8)) {
            base_time = "0500";
        }
        else if ((hour >= 8) && (hour < 11)) {
            base_time = "0800";
        }
        else if ((hour >= 11) && (hour < 14)) {
            base_time = "1100";
        }
        else if ((hour >= 14) && (hour < 20)) {
            base_time = "1400";
        }
        else if ((hour >= 20) && (hour < 23)) {
            base_time = "2000";
        }
        else {
            base_time = "2300";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        base_date = sdf.format(calendar.getTime());

        String [] result = {base_date, base_time};
        return result;
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int [] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                    isGPSGranted = true;
                    try {
                        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location == null) {
                            Log.d("location", ">>> Location is null");
                            showDefaultWeather();
                        }
                        else {
                            if (location.getProvider() == null) {
                                Log.d("location", ">>> Location provider is null");
                                showDefaultWeather();
                            }
                            else {
                                Log.d("location", ">>> Get location");
                                provider = location.getProvider();
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                                altitude = location.getAltitude();
                                //weather.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
                                //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                                //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                            }
                        }
                    }
                    catch (SecurityException e) {

                    }
                }
                else {
                    Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                    isGPSGranted = false;
                }
                return;
            }
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                //int responseCode = 300;
                BufferedReader br;
                //if (responseCode == 200) {
                //InputStream is = con.getInputStream();
                //Log.d("test", is.toString());
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //}
                //else {
                //br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                //}
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();
            }
            catch (Exception e) {
                Log.d("URL Connection", ">>> URL connection failed.");
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //weather.setText(s);
            //xml 파싱
            Forecast forecast = new Forecast();
            forecast.parseXML(s);

            //예보 화면에 표시
            String weatherInfo = "강수확률 " + forecast.getPop() + "%\n" + "온도 " + forecast.getT3h() + "℃";

            sky = (ImageView)findViewById(R.id.sky_image);
            if (forecast.getSky().equals("1")) {
                Log.d("parser", ">>> forecast(sky) : 1");
                sky.setImageResource(R.drawable.sunny);
            }
            else if (forecast.getSky().equals("2")) {
                Log.d("parser", ">>> forecast(sky) : 2");
                sky.setImageResource(R.drawable.little_cloudy);
            }
            else if (forecast.getSky().equals("3")) {
                Log.d("parser", ">>> forecast(sky) : 3");
                sky.setImageResource(R.drawable.cloud);
            }
            else if (forecast.getSky().equals("4")) {
                Log.d("parser", ">>> forecast(sky) : 4");
                sky.setImageResource(R.drawable.dark_clouds);
            }

            rain = (ImageView)findViewById(R.id.rain_image);
            //forecast.setPty("2");
            if (forecast.getPty().equals("0")) {
                Log.d("parser", ">>> forecast(pty) : 0");
                //sky.setImageResource(R.drawable.);
            }
            else if (forecast.getPty().equals("1")) {
                Log.d("parser", ">>> forecast(pty) : 2");
                rain.setImageResource(R.drawable.raindrop);
            }
            else if (forecast.getPty().equals("2")) {
                Log.d("parser", ">>> forecast(pty) : 2");
                rain.setImageResource(R.drawable.sleet);
            }
            else if (forecast.getPty().equals("3")) {
                Log.d("parser", ">>> forecast(pty) : 3");
                rain.setImageResource(R.drawable.snowflake);
            }
            weather = (TextView)findViewById(R.id.weather) ;
            weather.setText(weatherInfo);
        }
    }



}
