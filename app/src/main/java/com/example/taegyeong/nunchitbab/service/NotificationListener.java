package com.example.taegyeong.nunchitbab.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
}
