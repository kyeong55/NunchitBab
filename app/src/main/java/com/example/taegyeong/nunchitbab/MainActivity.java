package com.example.taegyeong.nunchitbab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

@EActivity
public class MainActivity extends AppCompatActivity {

    private Intent notificationSettingIntent;

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
    }

    @Click(R.id.bttn_make_noti)
    protected void makeNotification() {
        notiListTextView.setText("Make Notification Button");
    }

    @Click(R.id.bttn_delete_db)
    @Background
    protected void deleteDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }

    @Click(R.id.bttn_backup_db)
    @Background
    protected void backupDatabase() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date());
        File cacheDir = getExternalCacheDir();
        File backupRealm = new File(cacheDir, "nunchitbab_" + timestamp + ".realm");
        Uri backupPath = Uri.fromFile(backupRealm);
        backupRealm.deleteOnExit();

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.writeCopyTo(backupRealm);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"youngjae.chang@kaist.ac.kr",
                "chemidong@kaist.ac.kr",
                "danny003@kaist.ac.kr"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, backupPath);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Nunchitbab] Captured at " + timestamp);
        startActivity(Intent.createChooser(emailIntent , "Submit backup via ..."));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
