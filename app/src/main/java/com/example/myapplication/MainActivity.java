package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.lang.Math;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;


public class MainActivity extends Activity {

    private Unit tempUnit;
    private ArrayList<Unit> btList ;
    int state = 0;
    static int scanTime = 10;
    int flag1 = 0 , flag2 = 0 , flag3 = 0 , flag4 = 0;
    BluetoothLeScanner mLEScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        btList = new ArrayList<>();
        mLEScanner = BTAdapter.getBluetoothLeScanner();
        //跟系統要掃描權限(for Android 6.0up)
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        // 檢查手機硬體是否為BLE裝置
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "硬體不支援", Toast.LENGTH_SHORT).show();
            finish();

        }

        // 檢查手機是否開啟藍芽裝置
        if (BTAdapter == null || !BTAdapter.isEnabled()) {
            Toast.makeText(this, "未開啟藍芽，將自動開啟", Toast.LENGTH_SHORT).show();
            BTAdapter.enable();
        }
        //註冊receiver 有點像button listener的感覺
        //registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        //registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        Button enter = (Button) findViewById(R.id.button);
        Button scan = (Button) findViewById(R.id.scan);

        //按下去就先清空文字 + 停止掃描後開始掃描
        enter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                state = 0;
                tempUnit = new Unit();
                flag1 = 0;
                flag2 = 0;
                flag3 = 0;
                flag4 = 0;
                mLEScanner.startScan(callback);
            }
        });
        scan.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                state = 1;
                flag1 = 0;
                flag2 = 0;
                flag3 = 0;
                flag4 = 0;
                tempUnit = new Unit();
                mLEScanner.startScan(callback);
            }
        });
    }

    private ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice tempD = result.getDevice();
            TextView text = (TextView) findViewById(R.id.textView1);
            text.setMovementMethod(new ScrollingMovementMethod());
            if (tempD.getAddress().equals("C4:BE:84:ED:8B:6B") && flag1 != scanTime) {
                tempUnit.rssi1 += result.getRssi();
                flag1 ++;
            } else if (tempD.getAddress().equals("C4:BE:84:ED:C4:9E") && flag2 != scanTime) {
                tempUnit.rssi2 += result.getRssi();
                flag2 ++;
            } else if (tempD.getAddress().equals("C4:BE:84:ED:A5:4D") && flag3 != scanTime) {
                tempUnit.rssi3 += result.getRssi();
                flag3 ++;
            }
            if(flag1 == scanTime && flag2 == scanTime && flag3 == scanTime && flag4 == 0){
                tempUnit.rssi1 /= scanTime;
                tempUnit.rssi2 /= scanTime;
                tempUnit.rssi3 /= scanTime;
                flag4 = 1;
                if(state == 0) {
                    tempUnit.x = ((EditText) findViewById(R.id.xText)).getText().toString();
                    tempUnit.y = ((EditText) findViewById(R.id.yText)).getText().toString();
                    btList.add(tempUnit);
                    text.setText("");
                    for (int i = 0; i < btList.size(); i++) {
                        text.setText(text.getText() + "(" + btList.get(i).x + " , " + btList.get(i).y + ")");
                        text.setText(text.getText() + String.valueOf(btList.get(i).rssi1) + "  " + String.valueOf(btList.get(i).rssi2) + "  " + String.valueOf(btList.get(i).rssi3) + "\n");
                    }
                }
                else if(state == 1){
                    double min1 = Math.pow((tempUnit.getRssi1()-btList.get(0).getRssi1()),2) + Math.pow((tempUnit.getRssi2()-btList.get(0).getRssi2()),2) + Math.pow((tempUnit.getRssi3()-btList.get(0).getRssi3()),2);
                    double min2 = min1;
                    double min3 = min1;
                    int index1 = 0,index2 = 0 , index3 = 0;
                    for(int i = 1 ; i < btList.size() ; i++){
                        double temp = Math.pow((tempUnit.getRssi1() - btList.get(i).getRssi1()), 2) + Math.pow((tempUnit.getRssi2() - btList.get(i).getRssi2()), 2) + Math.pow((tempUnit.getRssi3()-btList.get(i).getRssi3()),2);
                        if(min1 > temp){
                            min3 = min2;
                            min2 = min1;
                            min1 = temp;
                            index3 = index2;
                            index2 = index1;
                            index1 = i;
                        }
                        else if(min2 > temp){
                            min3 = min2;
                            min2 = temp;
                            index3 = index2;
                            index2 = i;
                        }
                        else if(min3 > temp){
                            min3 = temp;
                            index3 = i;
                        }
                    }
                    //text.setText(text.getText() + "(" + btList.get(index1).x + " , " + btList.get(index1).y + ")\n" +"(" + btList.get(index2).x + " , " + btList.get(index2).y + ")\n" +"(" + btList.get(index3).x + " , " + btList.get(index3).y + ")\n" );
                    double resultx = ((Double.valueOf(btList.get(index1).x)) / min1 + (Double.valueOf(btList.get(index2).x)) / min2 + (Double.valueOf(btList.get(index3).x)) / min3) / (1 / min1 + 1 / min2 + 1 / min3);
                    double resulty = ((Double.valueOf(btList.get(index1).y)) / min1 + (Double.valueOf(btList.get(index2).y)) / min2 + (Double.valueOf(btList.get(index3).y)) / min3) / (1 / min1 + 1 / min2 + 1 / min3);
                    TextView res = (TextView) findViewById(R.id.textView2);
                    res.setText("");
                    res.setText(String.format("(%.2f , %.2f)\n",resultx,resulty) + String.valueOf(tempUnit.getRssi1()) + " "  + String.valueOf(tempUnit.getRssi2()) + " " + String.valueOf(tempUnit.getRssi3()));
                }
            mLEScanner.stopScan(callback);
            }
        }
    };
    //如果觸發上面receiver註冊的事件，就會跑這裡
    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //如果是觸發發現「藍芽裝置」就會跑這裡

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice tempD = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE); //取得rssi
                if(tempD.getAddress().equals("C4:BE:84:ED:8B:6B")){
                    tempUnit.rssi1 = rssi;
                }
                else if(tempD.getAddress().equals("C4:BE:84:ED:C4:9E")){
                    tempUnit.rssi2 = rssi;
                }
                else if(tempD.getAddress().equals("C4:BE:84:ED:A5:4D")){
                    tempUnit.rssi3 = rssi;
                }
            }
            //如果是觸發發現「掃描結束」就會跑這裡
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if(state == 0) {
                    Toast.makeText(MainActivity.this, "掃描結束", Toast.LENGTH_LONG).show();
                    tempUnit.x = ((EditText) findViewById(R.id.xText)).getText().toString();
                    tempUnit.y = ((EditText) findViewById(R.id.yText)).getText().toString();
                    btList.add(tempUnit);
                    TextView text = (TextView) findViewById(R.id.textView1);
                    text.setMovementMethod(new ScrollingMovementMethod());
                    text.setText("");
                    for (int i = 0; i < btList.size(); i++) {
                        text.setText(text.getText() + "(" + btList.get(i).x + " , " + btList.get(i).y + ")");
                        text.setText(text.getText() + String.valueOf(btList.get(i).rssi1) + "  " + String.valueOf(btList.get(i).rssi2)  + "  " + String.valueOf(btList.get(i).rssi3) + "\n");
                    }
                }
                else if(state == 1) {
                    Toast.makeText(MainActivity.this, "掃描結束", Toast.LENGTH_LONG).show();
                    double min = Math.pow((tempUnit.getRssi1()-btList.get(0).getRssi1()),2) + Math.pow((tempUnit.getRssi2()-btList.get(0).getRssi2()),2) + Math.pow((tempUnit.getRssi3()-btList.get(0).getRssi3()),2);
                    int index = 0;
                    for(int i = 1 ; i < btList.size() ; i++){
                        if(min > (Math.pow((tempUnit.getRssi1()-btList.get(i).getRssi1()),2) + Math.pow((tempUnit.getRssi2()-btList.get(i).getRssi2()),2) + Math.pow((tempUnit.getRssi3()-btList.get(i).getRssi3()),2))) {
                            index = i;
                            min = Math.pow((tempUnit.getRssi1() - btList.get(i).getRssi1()), 2) + Math.pow((tempUnit.getRssi2() - btList.get(i).getRssi2()), 2) + Math.pow((tempUnit.getRssi3()-btList.get(i).getRssi3()),2);
                        }
                    }
                    TextView res = (TextView) findViewById(R.id.textView2);
                    res.setText("");
                    res.setText("(" + btList.get(index).x + " , " + btList.get(index).y + ")\n" + String.valueOf(tempUnit.getRssi1()) + " "  + String.valueOf(tempUnit.getRssi2()) + " " + String.valueOf(tempUnit.getRssi3()));
                }
            }
        }
    };


}

