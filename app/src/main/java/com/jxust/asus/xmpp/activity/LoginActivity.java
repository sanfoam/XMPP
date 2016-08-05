package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.service.IMService;
import com.jxust.asus.xmpp.utils.ThreadUtils;
import com.jxust.asus.xmpp.utils.ToastUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class LoginActivity extends Activity {

    private TextView mEtUsername;
    private TextView mEtPassword;
    private Button mBtnLogin;

    public static final String HOST = "10.0.2.2";      // 主机IP
    public static final int PORT = 5222;           // 对应的端口号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("main", "onCreate");
        initView();
        initListener();
    }

    private void initView() {
        mEtUsername = (TextView) findViewById(R.id.et_username);
        mEtPassword = (TextView) findViewById(R.id.et_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        Log.i("main", "InitView");
    }

    private void initListener() {
        Log.i("main", "initListener");
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = mEtUsername.getText().toString();
                final String password = mEtPassword.getText().toString();
                // 判断用户名是否为空
                if (TextUtils.isEmpty(userName)) {    // 说明用户名为空
                    mEtUsername.setError("用户名不能为空");
                    return;
                }
                // 判断密码是否为空
                if (TextUtils.isEmpty(password)) {    // 说明密码为空
                    mEtPassword.setError("密码不能为空");
                    return;
                }

                // 在子线程里面进行连接操作
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 1.创建XMPP连接配置对象
                            ConnectionConfiguration config = new ConnectionConfiguration(HOST, PORT);

                            // 额外的配置，方便我们开发，上线的时候再改回来
                            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);  // 明文传输数据，方便开发调试
                            config.setDebuggerEnabled(true);        // 开启调试模式，方便我们查看具体发送的内容

                            // 2.开始创建连接对象
                            XMPPConnection conn = new XMPPConnection(config);

                            // 开始连接
                            conn.connect();

                            // 连接成功了
                            // 3.开始登录
                            conn.login(userName, password);

                            // 已经登录成功
                            ToastUtils.showToastSafe(getApplicationContext(),"登录成功");

                            finish();       // 关闭自身Activity
                            // 跳到主界面
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);

                            // 需要保存连接对象
                            IMService.conn = conn;

                        } catch (XMPPException e) {
                            e.printStackTrace();
                            // 登录失败
                           ToastUtils.showToastSafe(getApplicationContext(),"登录失败");
                        }
                    }
                });
            }
        });
    }
}

