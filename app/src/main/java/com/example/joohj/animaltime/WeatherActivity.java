package com.example.joohj.animaltime;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
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
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {

    TextView weather;
    public static StringBuilder sb;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weather = (TextView)findViewById(R.id.weather) ;

        /*
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 정보 권한에 대한 승인이 필요합니다.", Toast.LENGTH_LONG).show();

            boolean isRefused = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (isRefused) {
                Toast.makeText(this, "날씨 정보 제공을 위해 위치 정보 권한에 대한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                Toast.makeText(this, "날씨 정보 제공을 위해 위치 정보 권한에 대한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String provider = location.getProvider();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();
        weather.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
        */
        String url = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?"
                        + "serviceKey=mbhu%2FCuVnzj6F3CvilKdZv53AVLUsDzmTSvORQ06sye9kfVI6FFrMTw26u%2FRBUeW6KiCfsCYkI%2FMEf2ZQgbpsw%3D%3D"
                        + "&base_date=20181204&base_time=2300&nx=60&ny=127&numOfRows=10&pageSize=10&pageNo=1&startPage=1&_type=xml";
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();
    }

    /*
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            weather.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    */

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

                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserCreator.newPullParser();

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.d("test", ">>> testing....");
                con.setRequestMethod("GET");
                Log.d("test", ">>> testing....2");
                int responseCode = con.getResponseCode();
                //int responseCode = 300;
                Log.d("test", ">>> testing....3" + responseCode);
                BufferedReader br;
                Log.d("test", ">>> testing....4");
                //if (responseCode == 200) {
                //InputStream is = con.getInputStream();
                //Log.d("test", is.toString());
                Log.d("test", ">>> testing....5");
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Log.d("test", ">>> testing....6");
                //}
                //else {
                //br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                //}
                sb = new StringBuilder();
                Log.d("test", ">>> testing....7");
                String line;
                Log.d("test", ">>> testing....8");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();
                Log.d("test", ">>> printing....");
                Log.d("test", sb.toString());
            }
            catch (Exception e) {

            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            weather.setText(s);
        }
    }



}
