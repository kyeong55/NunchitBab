package com.example.taegyeong.nunchitbab.service;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.taegyeong.nunchitbab.model.Bab;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

@EService
public class NotificationListener extends NotificationListenerService {

    private BroadcastReceiver ringerReceiver;
    private BroadcastReceiver batteryReceiver;

    private SensorManager mSensorManager;
    private SensorEventListener proximityListener;
    private SensorEventListener lightListener;

    private final IBinder mBinder = new NotificationListenBinder();

    public class NotificationListenBinder extends Binder {

        public NotificationListener getService() {
            return NotificationListener.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        // 디바이스 설정=>일반=>보안=>알림 에서 해당 앱을 ON 시 서비스 시작됨
        super.onCreate();
        Log.d("testing", "service created");
        defineBroadcastReceivers();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        defineSensorListeners();
        save("nunchitbab_start", new JSONObject());
        registerBroadcastReceiver();
        registerSensorListener();
    }

    @Override
    public void onDestroy(){
        Log.d("testing", "service destroyed");
        unregisterAll();
        save("nunchitbab_end", new JSONObject());
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Notification 추가시 Callback
        Log.d("testing", "onNotificationPosted");
        JSONObject json = new JSONObject();
        try {
            json.put("package", sbn.getPackageName());
            json.put("posttime", sbn.getPostTime());
            save("noti_removed", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Notification 제거시 Callback
        Log.d("testing", "onNotificationRemoved");
        JSONObject json = new JSONObject();
        try {
            json.put("package", sbn.getPackageName());
            json.put("posttime", sbn.getPostTime());
            save("noti_removed", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    private void defineBroadcastReceivers() {
        ringerReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int ringerMode = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
                switch (ringerMode) {
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.d("getRingerMode", "Silent");
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.d("getRingerMode", "Vibrate");
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.d("getRingerMode", "Bell");
                }
                JSONObject json = new JSONObject();
                try {
                    json.put("mode", ringerMode);
                    save("ringer", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        batteryReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN);
                /**
                 * BATTERY_STATUS_UNKNOWN 1
                 * BATTERY_STATUS_CHARGING 2
                 * BATTERY_STATUS_DISCHARGING 3
                 * BATTERY_STATUS_NOT_CHARGING 4
                 * BATTERY_STATUS_FULL 5
                 * */
                switch (batteryStatus){
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        Log.d("debugging", "battery charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        Log.d("debugging", "battery discharging");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        Log.d("debugging", "battery full");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        Log.d("debugging", "battery not charging");
                        break;
                }
                JSONObject json = new JSONObject();
                try {
                    json.put("status", batteryStatus);
                    save("battery", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void defineSensorListeners(){
        proximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float proximityDistance = event.values[0];
                Log.d("debugging","proximity sensor changed: "+proximityDistance+"(cm)");
                JSONObject json = new JSONObject();
                try {
                    json.put("distance", proximityDistance);
                    save("proximity", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        lightListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightLevel = event.values[0];
                Log.d("debugging","light sensor changed: "+lightLevel+"(lx)");
                JSONObject json = new JSONObject();
                try {
                    json.put("level", lightLevel);
                    save("light", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    public void registerBroadcastReceiver(){
        IntentFilter ringerFilter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(ringerReceiver,ringerFilter);
        registerReceiver(batteryReceiver,batteryFilter);
    }

    public void registerSensorListener(){
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(proximityListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterAll(){
        mSensorManager.unregisterListener(proximityListener);
        mSensorManager.unregisterListener(lightListener);
    }

    @Background
    protected void save(String type, JSONObject json) {
        long timestamp = System.currentTimeMillis();
        Realm realm = Realm.getDefaultInstance();

        // [BEGIN] Realm Transaction
        realm.beginTransaction();

        Bab bab = realm.createObject(Bab.class);
        bab.setType(type);
        bab.setTimestamp(timestamp);
        bab.setJson(json.toString());

        realm.commitTransaction();
        // [COMMIT] Realm Transaction

        realm.close();
    }
}
