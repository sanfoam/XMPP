package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends Activity {

    public static final String CLICKACCOUNT = "clickAccount";
    public static final String CLICKNICKNAME = "clickNickname";
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.et_body)
    EditText etBody;
    @BindView(R.id.btn_send)
    Button btnSend;
    private String mClickAccount;
    private String mClickNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);     // 将inject变成了bind,这是ButterKnife8.0和以前版本不一样的地方
        init();
        initView();
        initData();
        initListener();
    }

    private void init() {
        mClickAccount = getIntent().getStringExtra(CLICKACCOUNT);
        mClickNickname = getIntent().getStringExtra(CLICKNICKNAME);
    }

    private void initView() {
        // 设置title


    }

    private void initData() {

    }

    private void initListener() {

    }


}
