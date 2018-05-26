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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    //This is a default proximity uuid of the RECO
    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    public static final boolean SCAN_RECO_ONLY = true;
    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    public static final boolean DISCONTINUOUS_SCAN = true;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;

    private ArrayList<RECOBeacon> mRangedBeacons;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private View mLayout;
    RECOBeacon recoBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent=new Intent(this.getIntent());

        mLayout = findViewById(R.id.mainLayout);
        getPermissionforBltth();





    }



    public void onSeatLookupClicked(View v){
        Intent intent=new Intent(MenuActivity.this,SeatLookupActivity.class);
        startActivity(intent);
    }


    public void onRangingToggleButtonClicked(View v) {
        ToggleButton toggle = (ToggleButton)v;
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

    public void onSeatInOutToggleButtonClicked(View v) {
        ToggleButton toggle = (ToggleButton)v;
        if(toggle.isChecked()) {

            if(true){ // 좌석상태 = true (사용가능)
                Log.i("MenuActivity", "onSeatInOutToggleButtonClicked off to on");
                Intent intent = new Intent(this, RecoBackgroundMonitoringService.class);
                startService(intent);
            } else{ // 좌석상태 = false (사용불가능)
                Log.i("MenuActivity", "onSeatInOutToggleButtonClicked on to off");
                stopService(new Intent(this, RecoBackgroundMonitoringService.class));
            }

        } else {
            Log.i("MenuActivity", "onSeatInOutToggleButtonClicked on to off");
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

    @Override
    protected void onResume() {
        super.onResume();

        if(this.isBackgroundMonitoringServiceRunning(this)) {
            ToggleButton toggle = (ToggleButton)findViewById(R.id.seatInOuToggleButton);
            toggle.setChecked(true);
        }

        if(this.isBackgroundRangingServiceRunning(this)) {
            ToggleButton toggle = (ToggleButton)findViewById(R.id.seatInOuToggleButton);
            toggle.setChecked(true);
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


}

