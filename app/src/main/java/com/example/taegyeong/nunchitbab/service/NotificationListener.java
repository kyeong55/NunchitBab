package com.example.taegyeong.nunchitbab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.taegyeong.nunchitbab.model.Bab;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;

import io.realm.Realm;

@EService
public class NotificationListener extends NotificationListenerService {

//    public NotificationListenService() {
//    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    @Override
    public void onCreate() {
        // 디바이스 설정=>일반=>보안=>알림 에서 해당 앱을 ON 시 서비스 시작됨
        super.onCreate();
        BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                AudioManager cRinger = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                switch (cRinger.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.d("getRingerMode", "Silent");
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.d("getRingerMode", "Vibrate");
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.d("getRingerMode", "Bell");
                }
            }
        };
        IntentFilter filter=new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(receiver,filter);
        Log.d("testing", "service created");
    }

    @Override
    public void onDestroy(){
        Log.d("testing", "service destroyed");
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Notification 추가시 Callback
        Log.d("testing", "onNotificationPosted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Notification 제거시 Callback
        Log.d("testing", "onNotificationRemoved");
    }

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    @Background
    protected void save(String type, String value) {
        long timestamp = System.currentTimeMillis();
        Realm realm = Realm.getDefaultInstance();

        // [BEGIN] Realm Transaction
        realm.beginTransaction();

        Bab bab = realm.createObject(Bab.class);
        bab.setType(type);
        bab.setTimestamp(timestamp);
        // todo: change to json appropriately
        bab.setJson(value);

        realm.commitTransaction();
        // [COMMIT] Realm Transaction

        realm.close();
    }
}
