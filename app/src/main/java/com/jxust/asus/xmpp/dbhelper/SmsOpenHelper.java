package com.jxust.asus.xmpp.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by asus on 2016/8/6.
 * 完成数据库、表创建
 *
 * @author Administrator
 * @time 2016/8/6 18:56
 */
public class SmsOpenHelper extends SQLiteOpenHelper {
    public static final String T_SMS = "t_sms";

    public class SMSTable implements BaseColumns {
        /**
         * from_account 消息来源,发送者
         * to_account   消息的目的地，接收者
         * body         消息的内容
         * status       消息状态
         * type         消息的类型
         * time         消息的时间
         * session_account  会话id --> 最近你和哪些人聊天
         */
        public static final String FROM_ACCOUNT = "from_account";
        public static final String TO_ACCOUNT = "to_account";
        public static final String BODY = "body";
        public static final String STATUS = "status";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String SESSION_ACCOUNT = "session_account";

    }

    public SmsOpenHelper(Context context) {
        super(context, "sms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + T_SMS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SMSTable.FROM_ACCOUNT + " TEXT, " +
                SMSTable.TO_ACCOUNT + " TEXT, " +
                SMSTable.BODY + " TEXT, " +
                SMSTable.STATUS + " TEXT, " +
                SMSTable.TYPE + " TEXT, " +
                SMSTable.TIME + " TEXT, " +
                SMSTable.SESSION_ACCOUNT + " TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
