package com.jxust.asus.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;

/**
 * Created by asus on 2016/8/6.
 *
 * @author Administrator
 * @time 2016/8/6 17:39
 */
public class SmsProvider extends ContentProvider {

    public static final String AUTHORITIES = SmsProvider.class.getCanonicalName();     // 得到类的完整路径

    static UriMatcher mUriMatcher;

    public static Uri URI_SMS = Uri.parse("content://" + AUTHORITIES + "/sms");

    public static final int SMS = 1;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 添加匹配规则
        mUriMatcher.addURI(AUTHORITIES, "/sms", SMS);
        // content://com.jxust.asus.xmpp.provider.SmsProvider/sms-->SMS
    }

    private SmsOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        // 创建表，创建数据库
        mHelper = new SmsOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /* =============================CRUD begin================================*/
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (mUriMatcher.match(uri)) {
            case SMS:
                // 插入之后对应的id
                long id = mHelper.getWritableDatabase().insert(SmsOpenHelper.T_SMS, "", values);
                if (id != -1) { // 说明插入成功
                    System.out.println("=================SmsProvider insertSuccess===============");
                    uri = ContentUris.withAppendedId(uri, id);
                    // 发送数据改变的信号,第二个参数表示是否需要指定一个特殊的observer
                    // 如果是null表示不指定observer，所有的都要接收
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
        }
        return uri;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                cursor = mHelper.getWritableDatabase().query(SmsOpenHelper.T_SMS,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                System.out.println("=================SmsProvider querySuccess===============");
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                updateCount = mHelper.getWritableDatabase().update(SmsOpenHelper.T_SMS, values,
                        selection, selectionArgs);
                if (updateCount > 0) {//说明更新成功
                    System.out.println("=================SmsProvider updateSuccess===============");
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
        }
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                // 对应于删除几条
                deleteCount = mHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection,
                        selectionArgs);
                if (deleteCount > 0) {
                    System.out.println("=================SmsProvider deleteSuccess===============");
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;
        }
        return deleteCount;
    }
    /* =============================CRUD end================================*/
}
