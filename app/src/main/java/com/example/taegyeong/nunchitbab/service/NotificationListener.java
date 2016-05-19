package com.example.taegyeong.nunchitbab.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.taegyeong.nunchitbab.model.Bab;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by taegyeong on 16. 5. 19..
 *
 * Modified by Youngjae on 16. 5. 19..
 */
@EService
public class NotificationListener extends NotificationListenerService {

    //TODO: decide sample period of sensor in microseconds
    private final int SAMPLE_PERIOD_US = 1000;

    private final String DEBUGLOG_NOTI = "notification_log";
    private final String DEBUGLOG_SENSOR = "sensor_log";

    private BroadcastReceiver ringerReceiver;
    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver screenReceiver;
    private BroadcastReceiver unlockReceiver;

    private SensorManager mSensorManager;
    private SensorEventListener proximityListener;
    private SensorEventListener lightListener;
    private SensorEventListener accelListener;

    private final IBinder mBinder = new NotificationListenBinder();

    public class NotificationListenBinder extends Binder {
        public NotificationListener getService() {
            return NotificationListener.this;
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        Log.d(DEBUGLOG_NOTI, "onNotificationPosted");
        Notification mNotification=sbn.getNotification();
        Bundle extras = mNotification.extras;
        JSONObject json = new JSONObject();
        try {
            json.put("id", sbn.getId());
            json.put("package", sbn.getPackageName());
            json.put("posttime", sbn.getPostTime());
            json.put("title", extras.getString(Notification.EXTRA_TITLE));
            save("noti_posted", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        Log.d(DEBUGLOG_NOTI, "onNotificationRemoved");
        Notification mNotification=sbn.getNotification();
        Bundle extras = mNotification.extras;
        JSONObject json = new JSONObject();
        try {
            json.put("id", sbn.getId());
            json.put("package", sbn.getPackageName());
            json.put("posttime", sbn.getPostTime());
            json.put("title", extras.getString(Notification.EXTRA_TITLE));
            save("noti_removed", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(DEBUGLOG_NOTI, "service created");
        save("nunchitbab_start", new JSONObject());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        defineBroadcastReceivers();
        defineSensorListeners();
        registerBroadcastReceiver();
        registerSensorListener(SAMPLE_PERIOD_US);
    }

    @Override
    public void onDestroy(){
        Log.d(DEBUGLOG_NOTI, "service destroyed");
        save("nunchitbab_end", new JSONObject());

        unregisterAll();

        super.onDestroy();
    }

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
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

    private void defineBroadcastReceivers() {

        ////////////
        // RINGER //
        ////////////
        ringerReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int ringerMode = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE))
                        .getRingerMode();
                JSONObject json = new JSONObject();
                try {
                    switch (ringerMode) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Log.d("getRingerMode", "Silent");
                            json.put("mode", "silent");
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            Log.d("getRingerMode", "Vibrate");
                            json.put("mode", "vibrate");
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            Log.d("getRingerMode", "Bell");
                            json.put("mode", "bell");
                            break;
                    }
                    save("ringer", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /////////////
        // BATTERY //
        /////////////
        batteryReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                /**
                 * BATTERY_STATUS_UNKNOWN       1
                 * BATTERY_STATUS_CHARGING      2
                 * BATTERY_STATUS_DISCHARGING   3
                 * BATTERY_STATUS_NOT_CHARGING  4
                 * BATTERY_STATUS_FULL          5
                 * */
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                            BatteryManager.BATTERY_STATUS_UNKNOWN);
                    Log.d(DEBUGLOG_SENSOR, "battery status " + batteryStatus);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("status", batteryStatus);
                        save("battery", json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ////////////
        // SCREEN //
        ////////////
        screenReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                /**
                 * SCREEN_OFF   0
                 * SCREEN_ON    1
                 * */
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_OFF)){
                    Log.d(DEBUGLOG_SENSOR, "screen off");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("onoff", 0);
                        save("screen", json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (action.equals(Intent.ACTION_SCREEN_ON)){
                    Log.d(DEBUGLOG_SENSOR, "screen on");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("onoff", 1);
                        save("screen", json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ////////////
        // UNLOCK //
        ////////////
        unlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_USER_PRESENT)) {
                    Log.d(DEBUGLOG_SENSOR, "unlocked");
                    save("unlock", new JSONObject());
                }
            }
        };
    }

    private void defineSensorListeners(){

        ///////////////
        // PROXIMITY //
        ///////////////
        proximityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float proximityDistance = event.values[0];
                Log.d(DEBUGLOG_SENSOR,"proximity sensor changed: "+proximityDistance+"(cm)");
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

        ///////////
        // LIGHT //
        ///////////
        lightListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightLevel = event.values[0];
                Log.d(DEBUGLOG_SENSOR,"light sensor changed: "+lightLevel+"(lx)");
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

        ///////////
        // ACCEL //
        ///////////
        accelListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float xAccel = event.values[0];
                float yAccel = event.values[1];
                float zAccel = event.values[2];
                double magAccel = Math.sqrt(xAccel * xAccel + yAccel * yAccel + zAccel * zAccel);
                Log.d(DEBUGLOG_SENSOR,"accel sensor changed: "+magAccel+"(m/s^2)");
                JSONObject json = new JSONObject();
                try {
                    json.put("x", xAccel);
                    json.put("y", yAccel);
                    json.put("z", zAccel);
                    json.put("mag", magAccel);
                    save("accel", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    public void registerBroadcastReceiver(){

        // RINGER
        IntentFilter ringerFilter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(ringerReceiver, ringerFilter);

        // BATTERY
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryFilter);

        // SCREEN
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, screenFilter);

        // UNLOCK
        IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, unlockFilter);
    }

    public void registerSensorListener(int samplePeriod){

        // PROXIMITY
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(proximityListener, proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL, samplePeriod);

        // LIGHT
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(lightListener, lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL, samplePeriod);

        // ACCEL
        Sensor accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(accelListener, accelSensor,
                SensorManager.SENSOR_DELAY_NORMAL, samplePeriod);
    }

    public void unregisterAll(){

        // Broadcast Receiver
        unregisterReceiver(ringerReceiver);
        unregisterReceiver(batteryReceiver);
        unregisterReceiver(screenReceiver);
        unregisterReceiver(unlockReceiver);

        // Sensor Listener
        mSensorManager.unregisterListener(proximityListener);
        mSensorManager.unregisterListener(lightListener);
        mSensorManager.unregisterListener(accelListener);
    }
}
