package com.example.taegyeong.nunchitbab.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class EventHandleService extends Service {

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private SensorEventListener mSensorEventListener;

    private float proximityDistance;

    private final IBinder mBinder = new EventHandleBinder();

    public class EventHandleBinder extends Binder {

        public EventHandleService getService() {
            return EventHandleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("debugging", "EventHandleService create");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onDestroy(){
        Log.d("debugging", "EventHandleService destroy");
        unregisterSensor();
        super.onDestroy();
    }

    public void registerSensor(){
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                proximityDistance = event.values[0];
                Log.d("debugging","proximity sensor changed: "+proximityDistance);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        mSensorManager.registerListener(mSensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void registerSensor(SensorEventListener listener){
        mSensorEventListener = listener;
        mSensorManager.registerListener(listener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unregisterSensor(){
        mSensorManager.unregisterListener(mSensorEventListener);
    }
}
