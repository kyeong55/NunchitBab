package com.example.taegyeong.nunchitbab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.taegyeong.nunchitbab.service.EventHandleService;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.UiThread;

@EActivity
public class MainActivity extends AppCompatActivity {

    private Intent notificationSettingIntent;
    private Intent eventHandleIntent;
    private EventHandleService eventHandleService;

    @ViewById(R.id.text_noti_list)
    TextView notiListTextView;

    @ViewById(R.id.text_proxi_distance)
    TextView proximityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationSettingIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(notificationSettingIntent);
            }
        });
        eventHandleIntent = new Intent(this, EventHandleService.class);
        startService(eventHandleIntent);
        bindService(eventHandleIntent, eventHandleConnection, Context.BIND_AUTO_CREATE);
    }

    @Click(R.id.bttn_make_noti)
    protected void makeNotification() {
        notiListTextView.setText("Make Notification Button");
    }

    @Click(R.id.bttn_list_noti)
    protected void listNoticiation() {
        notiListTextView.setText("Tagyeong so beautiful guy");
    }

    @Override
    protected void onDestroy(){
        stopService(eventHandleIntent);
        unbindService(eventHandleConnection);
        super.onDestroy();
    }

    private ServiceConnection eventHandleConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            eventHandleService = ((EventHandleService.EventHandleBinder) service).getService();
            eventHandleService.registerSensor(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float proximityDistance = event.values[0];
                    proximityTextView.setText("Proximity: "+proximityDistance+"cm");
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };
}
