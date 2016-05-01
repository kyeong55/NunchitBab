package com.example.taegyeong.nunchitbab;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by yjchang on 5/1/16.
 */
public class NunchitBabApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Realm Configuration
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("nunchitbab.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
