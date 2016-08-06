package com.jxust.asus.xmpp.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;
import com.jxust.asus.xmpp.utils.PinyinUtil;
import com.jxust.asus.xmpp.utils.ThreadUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
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
    private Roster mRoster;
    private MyRosterListener mRosterListener;

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
