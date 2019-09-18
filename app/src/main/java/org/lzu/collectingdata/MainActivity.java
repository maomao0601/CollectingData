package org.lzu.collectingdata;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.text.DecimalFormat;


public class MainActivity extends BaseActivity {
    private CurrentBarView cbv1;
    private CurrentBarView cbv2;
    private TextView tv_bar1;
    private TextView tv_bar2;
    private LineChart mChart;
    private ImageView iv_bluetooth;

    private DynamicLineChartManager dynamicLineChartManager;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private int[] mDatas = {
            14, 14, 14, 14, 14, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 11, 11, 11, 20, 18, 17, 17, 16, 15, 15, 14, 14, 14, 13, 13, 13,
            13, 13, 12, 12, 12, 12, 12, 11, 11, 11, 11, 11, 20, 16, 15, 14, 13, 12, 12, 11, 10, 11, 11, 10, 11, 10, 11, 11, 11, 11, 11,
            11, 11, 11, 11, 13, 12, 11, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 13, 12,
            11, 11, 11, 11, 11, 11, 11, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 29, 23, 21, 19, 17, 16, 16, 16, 16, 15, 15,
            15, 15, 14, 14, 14, 14, 14, 14, 14, 14, 35, 33, 28, 24, 21, 19, 17, 16, 15, 14, 15, 15, 15, 14, 14, 14, 14, 13, 13, 13, 14,
            13, 13, 13, 13, 13, 33, 26, 22, 21, 17, 17, 16, 15, 15, 14, 15, 14, 14, 14, 14, 14, 14, 14, 13, 13, 13, 13, 13, 13, 13, 13
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final int voltage = msg.arg1;
            final int index = msg.arg2;
            // C = (E + 15.09055)/6.31117
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DecimalFormat df = new DecimalFormat("0.00000");
                    Double c =  (voltage + 15.09055)/6.31117;
                    Double maxC = (50 + 15.09055)/6.31117;
                    float percent1 = 1 - voltage/50.0f;
                    float percent2 = (float)(1 - c/maxC);
                    cbv2.setPercent(percent2);
                    cbv1.setPercent(percent1);
                    if(index >= 66 && index <= 112) {
                        cbv2.setCurColor(getResources().getColor(R.color.color_red));
                        cbv1.setCurColor(getResources().getColor(R.color.color_red));
                        tv_bar1.setTextColor(getResources().getColor(R.color.color_red));
                        tv_bar2.setTextColor(getResources().getColor(R.color.color_red));
                    } else {
                        cbv2.setCurColor(getResources().getColor(R.color.color_green));
                        cbv1.setCurColor(getResources().getColor(R.color.color_green));
                        tv_bar1.setTextColor(getResources().getColor(R.color.color_green));
                        tv_bar2.setTextColor(getResources().getColor(R.color.color_green));
                    }
                    tv_bar1.setText(SpanUtils.init(df.format(c)).setSize(ScreenInfo.intValue(R.integer.font20)).append("mmolL-1").setSize(ScreenInfo.intValue(R.integer.font16)).create());
                    tv_bar2.setText(SpanUtils.init("" + voltage).setSize(ScreenInfo.intValue(R.integer.font20)).append("mV").setSize(ScreenInfo.intValue(R.integer.font16)).create());
                }
            });
        }
    };

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        cbv1 = findViewById(R.id.cbv1);
        cbv2 = findViewById(R.id.cbv2);
        tv_bar1 = findViewById(R.id.tv_bar1);
        tv_bar2 = findViewById(R.id.tv_bar2);
        mChart = findViewById(R.id.dynamic_chart);
        iv_bluetooth = findViewById(R.id.iv_bluetooth);
        requestPermission();
        getBluetoothState();
        dynamicLineChartManager = new DynamicLineChartManager(mChart);
        dynamicLineChartManager.setYAxis(50, 0, 5);
        dynamicLineChartManager.setDescription("");
        dynamicLineChartManager.setLowLimitLine(18,"threshold");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mDatas.length; i++) {
                    int voltage = mDatas[i];
                    dynamicLineChartManager.addEntry(i, voltage);
                    Message msg = new Message();
                    msg.arg1 = voltage;
                    msg.arg2 = i;
                    mHandler.handleMessage(msg);
                    try {
                        Thread.sleep(Constant.INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void getBluetoothState() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = defaultAdapter.isEnabled();
        if (enabled) {
            iv_bluetooth.setImageResource(R.drawable.ic_bluetooth);
        } else {
            iv_bluetooth.setImageResource(R.drawable.ic_bluetooth_disabled);
        }
        registerReceiver(mReceiver, mkFilter());
    }

    private IntentFilter mkFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (intExtra) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e("gg", "STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            iv_bluetooth.setImageResource(R.drawable.ic_bluetooth);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e("gg", "STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            iv_bluetooth.setImageResource(R.drawable.ic_bluetooth_disabled);
                            break;
                    }
                    break;
            }
        }
    };

    private void requestPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
