package com.example.johnnyseo.swpj;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.perples.recosdk.RECOBeacon;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;
    private View mLayout;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ToggleButton seatInOuToggleButton;

    //This is a default proximity uuid of the RECO
    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    public static final boolean SCAN_RECO_ONLY = true;
    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    public static final boolean DISCONTINUOUS_SCAN = true;



    RECOBeacon recoBeacon;
    String beaconNum;
    String sendMsg = "";
    StringBuffer seatInfoCheck = new StringBuffer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout = findViewById(R.id.mainLayout);
        getPermissionforBltth();


        Intent intent2 = new Intent(this, RecoBackgroundRangingService.class);
        startService(intent2);


        setContentView(R.layout.activity_menu);
        Intent intent=new Intent(this.getIntent());

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intent2 = new Intent(this, RecoBackgroundRangingService.class);
        startService(intent2);

    }

    public void onSeatLookupClicked(View v){
        Intent intent=new Intent(MenuActivity.this,SeatLookupActivity.class);
        startActivity(intent);
    }


    public void onRangingToggleButtonClicked(View v) {
        ToggleButton toggle = (ToggleButton)v;
        SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
        beaconNum = pref.getString("beaconNum", null);
        Log.i("체크", "배려요청클릭 beaconNum = " + beaconNum);
        if(toggle.isChecked()) {
            if(true){ // 좌석상태 = true (사용가능)
                Log.i("MenuActivity", "onRangingToggleButtonClicked off to on");
                Intent intent = new Intent(this, RecoBackgroundRangingService.class);
                startService(intent);
            } else{ // 좌석상태 = false (사용불가능)
                stopService(new Intent(this, RecoBackgroundRangingService.class));
            }

        } else {
            Log.i("MenuActivity", "onRangingToggleButtonClicked on to off");
            stopService(new Intent(this, RecoBackgroundRangingService.class));
        }
    }

    public void onSeatInOutToggleButtonClicked(View v) throws InterruptedException {
        ToggleButton toggle = (ToggleButton)v;
        SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
        beaconNum = pref.getString("beaconNum", null);
        Log.i("체크", "착석퇴석 beaconNum = " + beaconNum);
        if(toggle.isChecked()) {
            // 비콘정보 JSON으로 받아오기
            new MenuActivity.JsonTask().execute("http://203.246.82.142:8080/SWPJ/JSONServer_beacon.jsp", beaconNum);

            // 값 받아오기위해 쓰레드를 잠시 멈춤
            long s = 2000;
            Thread.sleep(s);

            Log.i("체크", "JSON실행후 메뉴에서 seatInfoCheck=" + seatInfoCheck.toString());

            if(seatInfoCheck.toString().equals("true") && beaconNum!=null){ // 좌석상태 = true (사용가능)
                Toast.makeText(MenuActivity.this,"착석처리를 시작합니다.\n착석이 완료되면 알림센터에 표시됩니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RecoBackgroundMonitoringService.class);
                startService(intent);

            } else if (seatInfoCheck.toString().equals("false") && beaconNum!=null){ // 좌석상태 = false (사용불가능)
                Toast.makeText(MenuActivity.this,"좌석이 이용가능한 상태가 아닙니다.",Toast.LENGTH_SHORT).show();
                toggle.setChecked(false);
                stopService(new Intent(this, RecoBackgroundMonitoringService.class));

            }else{
                Log.i("체크", "비콘넘버 못받아옴, beaconNum=" + beaconNum);
                Toast.makeText(MenuActivity.this,"아직 비콘을 인식하지못했습니다. \n 비콘을 인식하고 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                stopService(new Intent(this, RecoBackgroundMonitoringService.class));
                toggle.setChecked(false);
            }


        } else {
            Toast.makeText(MenuActivity.this,"퇴석처리되었습니다.",Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, RecoBackgroundMonitoringService.class));
        }
    }




    public void onSetting(View v){
        Intent intent=new Intent(MenuActivity.this,SettingActivity.class);
        startActivity(intent);
    }






    /*=======================================================================
                                비콘동작에 필요한 메소드
    =========================================================================*/
    //사용자가 블루투스를 켜도록 요청
    public void getPermissionforBltth(){

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("MenuActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                Log.i("MenuActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //If the request to turn on bluetooth is denied, the app will be finished.
            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mLayout, R.string.location_permission_granted, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mLayout, R.string.location_permission_not_granted, Snackbar.LENGTH_LONG).show();
                }
            }
            default :
                break;
        }
    }

    private void requestLocationPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        Snackbar.make(mLayout, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                    }
                })
                .show();
    }




    @Override
    protected void onResume() {
        super.onResume();

        if(this.isBackgroundMonitoringServiceRunning(this)) {
            ToggleButton toggle = (ToggleButton)findViewById(R.id.seatInOuToggleButton);
            toggle.setChecked(true);
        }

        if(this.isBackgroundRangingServiceRunning(this)) {
            ToggleButton toggle = (ToggleButton)findViewById(R.id.seatInOuToggleButton);
            toggle.setChecked(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * In order to use RECO SDK for Android API 23 (Marshmallow) or higher,
     * the location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is required.
     *
     * This sample project requests "ACCESS_COARSE_LOCATION" permission only,
     * but you may request "ACCESS_FINE_LOCATION" permission depending on your application.
     *
     * "ACCESS_COARSE_LOCATION" permission is recommended.
     *
     * 안드로이드 API 23 (마시멜로우)이상 버전부터, 정상적으로 RECO SDK를 사용하기 위해서는
     * 위치 권한 (ACCESS_COARSE_LOCATION 혹은 ACCESS_FINE_LOCATION)을 요청해야 합니다.
     *
     * 본 샘플 프로젝트에서는 "ACCESS_COARSE_LOCATION"을 요청하지만, 필요에 따라 "ACCESS_FINE_LOCATION"을 요청할 수 있습니다.
     *
     * 당사에서는 ACCESS_COARSE_LOCATION 권한을 권장합니다.
     *
     */


    private boolean isBackgroundMonitoringServiceRunning(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
            if(RecoBackgroundMonitoringService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isBackgroundRangingServiceRunning(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
            if(RecoBackgroundRangingService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*===========================================================================================
                            서버와 통신하여 좌석 상태정보 불러오기
      ===========================================================================================*/
    private class JsonTask extends AsyncTask<String, String, String> {


        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... strings) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            Boolean testSeatInfo = true;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                connection.connect();
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                sendMsg = "beaconNum="+strings[1];
                osw.write(sendMsg);
                osw.flush();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }

                JSONArray json = null;
                json = new JSONArray(buffer.toString());
                Log.i("체크","Json="+json.toString()+", JSonLength = "+json.length());

                testSeatInfo = (Boolean) json.getJSONObject(json.length()-1).get("seatInfo");
                if(seatInfoCheck.toString().equals("true")||seatInfoCheck.toString().equals("false")){

                }else{
                    seatInfoCheck.delete(0,seatInfoCheck.length());
                    seatInfoCheck.append(testSeatInfo);
                }

                Log.i("체크","try문의 Json에서 받아온 seatInfoCheck="+seatInfoCheck.toString());
                Log.i("체크","try문의 Json에서 받아온 tsetSeatInfo="+testSeatInfo);
                return testSeatInfo.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("체크","finally문의 tsetSeatInfo="+testSeatInfo);
            return testSeatInfo.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("체크", "result값="+result);


        }
    }



}

