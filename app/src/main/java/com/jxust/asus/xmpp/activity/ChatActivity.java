package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;
import com.jxust.asus.xmpp.provider.SmsProvider;
import com.jxust.asus.xmpp.service.IMService;
import com.jxust.asus.xmpp.utils.ThreadUtils;
import com.jxust.asus.xmpp.utils.ToastUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

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

    public void send(View v) {
        final String body = mEtBody.getText().toString();
        // 消息的发送放到子线程中
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
//                  1.需要去获得消息的管理者
                    ChatManager chatManager = IMService.conn.getChatManager();
//                  2.创建聊天对象
//                  chatManager.createChat(被发送对象的JID(唯一标识),消息的监听者);
                    MyMessageListener messageListener = new MyMessageListener();
                    Chat chat = chatManager.createChat(mClickAccount, messageListener);
//                  3.发送消息
                    Message msg = new Message();
                    msg.setFrom(IMService.mCurAccount); // 消息的来源，当前登录的用户
                    msg.setTo(mClickAccount);           // 消息发送的目的地
                    msg.setBody(body);                  // 输入框里面的内容
                    msg.setType(Message.Type.chat);     // 消息的类型，类型就是聊天
//                    msg.setProperty("key", "value");     // 额外的属性-->其实就是额外的信息,这里我们不使用

                    chat.sendMessage(msg);

                    // 发送消息，保存消息
                    // 我(from) --> other(to)    session_account===>other
                    saveMessage(mClickAccount, msg);

//                  4.清空输入框，在主线程中进行
                    ThreadUtils.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mEtBody.setText(null);  // 将输入框清空
                        }
                    });
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 保存消息 --> ContentResoler --> contentProvider --> sqlite
     *
     * @param msg
     */
    private void saveMessage(String sessionAccount, Message msg) {
        ContentValues values = new ContentValues();

        // session_account 表示的是 会话id --> 最近你和哪些人聊天
        // 我(from) --> other(to)    session_account===>other
        // other(from) -->我(to)     session_account===>other
        values.put(SmsOpenHelper.SMSTable.FROM_ACCOUNT, msg.getFrom());
        values.put(SmsOpenHelper.SMSTable.TO_ACCOUNT, msg.getTo());
        values.put(SmsOpenHelper.SMSTable.BODY, msg.getBody());
        values.put(SmsOpenHelper.SMSTable.STATUS, "offline");
        values.put(SmsOpenHelper.SMSTable.TYPE, msg.getType().name());
        values.put(SmsOpenHelper.SMSTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SMSTable.SESSION_ACCOUNT, sessionAccount);
        getContentResolver().insert(SmsProvider.URI_SMS, values);
    }


    // 接收消息的监听
    private class MyMessageListener implements MessageListener {


        // 处理消息
        @Override
        public void processMessage(Chat chat, Message message) {
            String body = message.getBody();
            if (body != null) {
                ToastUtils.showToastSafe(getApplicationContext(), body);
                System.out.println("body:" + message.getBody());
                System.out.println("type:" + message.getType());
            /*
            from:admin@127.0.0.1/Spark
            to:mayu@127.0.0.1/Smack
             */
                System.out.println("from:" + message.getFrom());
                System.out.println("to:" + message.getTo());

                // 收到消息，保存消息
                // other(from) -->我(to)     session_account===>other
                String participant = chat.getParticipant();         // 发送消息者
                System.out.println("发送者:" + participant);
                saveMessage(participant, message);
            }
        }
    }
}
