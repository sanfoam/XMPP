package com.jxust.asus.xmpp.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;
import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;
import com.jxust.asus.xmpp.provider.SmsProvider;
import com.jxust.asus.xmpp.utils.PinyinUtil;
import com.jxust.asus.xmpp.utils.ThreadUtils;
import com.jxust.asus.xmpp.utils.ToastUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * Created by asus on 2016/8/5.
 *
 * @author Administrator
 * @time 2016/8/5 9:13
 */
public class IMService extends Service {
    public static XMPPConnection conn;  // 一旦被static修改就不再被GC所回收了
    public static String mCurAccount;   // 当前登录用户的JID(账户)

    private Roster mRoster;
    private MyRosterListener mRosterListener;

    private ChatManager mChatManager;
    private MyMessageListener mMessageListener;
    private Chat mChat;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("----------------service onCreate---------------------");
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
            /*------------------同步花名册 begin--------------------*/
                System.out.println("------------------同步花名册 begin--------------------");
                // 得到所有的联系人
                // 需要连接对象
                XMPPConnection conn = IMService.conn;
                // 得到花名册
                mRoster = conn.getRoster();
                // 得到花名册中的所有的联系人
                final Collection<RosterEntry> entries = mRoster.getEntries();

                // 监听联系人的改变
                mRosterListener = new MyRosterListener();
                mRoster.addRosterListener(mRosterListener);


                for (RosterEntry entry : entries) {
                    saveOrUpdateEntry(entry);
                }
                System.out.println("------------------同步花名册 end--------------------");
                /*------------------同步花名册 end--------------------*/
                /*------------------创建消息管理者 注册监听 begin--------------------*/
//                  1.需要去获得消息的管理者
                if (mChatManager == null) {
                    mChatManager = IMService.conn.getChatManager();
                }
                /*------------------创建消息管理者 注册监听 end--------------------*/
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("----------------service onStartCommand---------------------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {   // 在Service销毁以后再移除Fragment
        super.onDestroy();
        System.out.println("----------------service onDestroy---------------------");
        // 移除rosterListener
        if (mRosterListener != null && mRoster != null) {
            mRoster.removeRosterListener(mRosterListener);
        }

        // 移除messageListener
        if (mChat != null && mMessageListener != null) {
            mChat.removeMessageListener(mMessageListener);
        }
    }

    private class MyRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {       // 联系人添加了
            System.out.println("--------------------entriesAdded-------------------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新，要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {     // 联系人修改了
            System.out.println("--------------------entriesUpdated-------------------------");
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新，要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {     // 联系人删除了
            System.out.println("--------------------entriesDeleted-------------------------");
            for (String account : addresses) {
                // 执行删除操作
                getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
            }
        }

        @Override
        public void presenceChanged(Presence presence) {                // 联系人状态改变了
            System.out.println("--------------------presenceChanged-------------------------");
        }
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

    /**
     * 发送消息
     */
    private void sendMessage(final Message msg) {
        // 放到子线程中执行相关操作,因为在调用处(ChatActivity)中已经是子线程了，所以在这不新建线程
//      2.创建聊天对象
        try {
            if (mMessageListener == null) {
                mMessageListener = new MyMessageListener();
            }
//                  chatManager.createChat(被发送对象的JID(唯一标识)也就是消息发给谁,消息的监听者);
            if (mChat == null) {
                mChat = mChatManager.createChat(msg.getTo(), mMessageListener);
            }
            mChat.sendMessage(msg);
            // 发送消息，保存消息
            // 我(from) --> other(to)    session_account===>other
            saveMessage(msg.getTo(), msg);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

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

    private void saveOrUpdateEntry(RosterEntry entry) {
        ContentValues values = new ContentValues();
        String account = entry.getUser();
        //  account = account.substring(0,account.indexOf("@"))+"@"+ LoginActivity.SERVICENAME;
        String nickname = entry.getName();
        String pinyin = new PinyinUtil().getPinyin(entry.getName());
        System.out.println("acount:" + account);
        System.out.println("nickname:" + nickname);
        System.out.println("pinyin:" + pinyin);
        // 处理昵称
        if (nickname == null || "".equals((nickname))) {
            // 如果没有昵称就将用户名的改成昵称
            nickname = account.substring(0, account.indexOf("@"));   // billy@jxust.com  -->  billy
        }
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, pinyin);

        // 先update，后插入  重点
        int updateCount = getContentResolver().update(ContactsProvider.URI_CONTACT,
                values, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) { // 没有更新到任何数据
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }


}
