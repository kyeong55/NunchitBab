package com.example.taegyeong.nunchitbab;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

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
