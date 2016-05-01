package com.example.taegyeong.nunchitbab;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.taegyeong.nunchitbab.service.NotificationListener;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class MainActivity extends AppCompatActivity {

    private Intent notificationSettingIntent;
    private Intent notificationListenerIntent;
    private NotificationListener notificationListenerService;

    @ViewById(R.id.text_noti_list)
    TextView notiListTextView;

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
        notificationListenerIntent = new Intent(this, NotificationListener.class);
//        bindService(notificationListenerIntent, notificationListenerConnection, Context.BIND_AUTO_CREATE);
    }

    @Click(R.id.bttn_make_noti)
    protected void makeNotification() {
        notiListTextView.setText("Make Notification Button");
    }

    @Click(R.id.bttn_list_noti)
    protected void listNoticiation() {
        notiListTextView.setText("Tagyeong so beautiful guy");
    }

    @Click(R.id.bttn_ringer)
    protected void ringerNoti() {
        notiListTextView.setText("Tagyeong so beautiful guy");
    }

    @Override
    protected void onDestroy(){
//        unbindService(notificationListenerConnection);
        super.onDestroy();
    }

//    private ServiceConnection notificationListenerConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            notificationListenerService = ((NotificationListener.NotificationListenBinder) service).getService();
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {}
//    };
}
