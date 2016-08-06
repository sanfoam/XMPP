package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends Activity {

    public static final String CLICKACCOUNT = "clickAccount";
    public static final String CLICKNICKNAME = "clickNickname";
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.et_body)
    EditText mEtBody;


    private String mClickAccount;
    private String mClickNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
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
        mTitle.setText("与" + mClickNickname + "聊天中");

    }

    private void initData() {

    }

    private void initListener() {

    }

    public void send(View v){
        String body = mEtBody.getText().toString();

    }

}
