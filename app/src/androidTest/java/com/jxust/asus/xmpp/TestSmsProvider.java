package com.jxust.asus.xmpp;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;
import com.jxust.asus.xmpp.provider.SmsProvider;

/**
 * Created by asus on 2016/8/6.
 *
 * @author Administrator
 * @time 2016/8/6 19:56
 */
public class TestSmsProvider extends AndroidTestCase {

    public void testInsert() {
        /**
         * FROM_ACCOUNT 消息来源,发送者
         * TO_ACCOUNT   消息的目的地，接收者
         * BODY         消息的内容
         * STATUS       消息状态
         * TYPE         消息的类型
         * TIME         消息的时间
         * SESSION_ACCOUNT  会话id --> 最近你和哪些人聊天
         */
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SMSTable.FROM_ACCOUNT, "billy@127.0.0.1");
        values.put(SmsOpenHelper.SMSTable.TO_ACCOUNT, "mayu@127.0.0.1");
        values.put(SmsOpenHelper.SMSTable.BODY, "你好");
        values.put(SmsOpenHelper.SMSTable.STATUS, "offline");
        values.put(SmsOpenHelper.SMSTable.TYPE, "chat");
        values.put(SmsOpenHelper.SMSTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SMSTable.SESSION_ACCOUNT, "mayu@127.0.0.1");
        getContext().getContentResolver().insert(SmsProvider.URI_SMS, values);

    }

    public void testDelete() {
        getContext().getContentResolver().delete(SmsProvider.URI_SMS, SmsOpenHelper.SMSTable
                .FROM_ACCOUNT + "=?", new String[]{"billy@127.0.0.1"});
    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SMSTable.FROM_ACCOUNT, "billy@127.0.0.1");
        values.put(SmsOpenHelper.SMSTable.TO_ACCOUNT, "mayu@127.0.0.1");
        values.put(SmsOpenHelper.SMSTable.BODY, "好久不见");
        values.put(SmsOpenHelper.SMSTable.STATUS, "offline");
        values.put(SmsOpenHelper.SMSTable.TYPE, "chat");
        values.put(SmsOpenHelper.SMSTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SMSTable.SESSION_ACCOUNT, "mayu@127.0.0.1");
        getContext().getContentResolver().update(SmsProvider.URI_SMS, values, SmsOpenHelper.SMSTable
                .FROM_ACCOUNT + "=?", new String[]{"billy@127.0.0.1"});
    }

    public void testQuery() {
        Cursor cursor = getContext().getContentResolver().query(SmsProvider.URI_SMS, null, null,
                null, null);
        // 得到所有的列
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            for (int i = 0; i < columnCount; i++) {
                System.out.print(cursor.getString(i) + " ");
            }
            System.out.println("");
        }

    }
}
