package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.utils.ThreadUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 停留3s进入到登录界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                // 停留3秒
                SystemClock.sleep(3000);

                // 进入到登录界面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
