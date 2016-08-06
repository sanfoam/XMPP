package com.jxust.asus.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;

/**
 * Created by asus on 2016/8/5.
 *
 * @author Administrator
 * @time 2016/8/5 11:00
 */
public class ContactsProvider extends ContentProvider {

    // 主机地址的常量-->当前类的完整路径
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();
    // 得到一个类的完整路径

    // 地址匹配对象
    static UriMatcher mUriMatcher;

    // 对应联系人表的一个uri常量
    public static Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact");

    public static final int CONTACT = 1;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 添加一个匹配的规则
        mUriMatcher.addURI(AUTHORITIES, "/contact", CONTACT);
        // content://com.jxust.asus.xmpp.provider.ContactsProvider/contact-->CONTACT
    }

    private ContactOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new ContactOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }

    /***************
     * crud begin
     **************/
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // 数据是存到SQLite-->创建db文件，建立表-->SQLiteOpenHelper
        int code = mUriMatcher.match(uri);

        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                // 新插入的id
                long id = db.insert(ContactOpenHelper.T_CONTACT, "", values);
                if (id != -1) {   // 插入成功
                    System.out.println("----ContactsProvider--------insertSuccess------");
                    // 拼接最新的uri
                    // 如果成功
                    // content://com.jxust.asus.xmpp.provider.ContactsProvider/contact会变成
                    // content://com.jxust.asus.xmpp.provider.ContactsProvider/contact/id
                    uri = ContentUris.withAppendedId(uri, id);
                    // 通知ContentObserver数据改变了
                    // 第二参数为null表示所有的observer都可以收到，如果不为null则表示只有指定的observer才可以收到
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        int deleteCount = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                // 返回的值deleteCount表示的就是影响的行数，selection表示的就是条件，selectionArgs表示的就是条件的参数
                deleteCount = db.delete(ContactOpenHelper.T_CONTACT, selection, selectionArgs);
                if (deleteCount > 0) {
                    System.out.println("----ContactsProvider--------deleteSuccess------");
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        int updateCount = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                // updateCount表示的就是更新条数,selection 表示的是条件 selectionArgs表示的就是条件的参数 values表示的就是要更新的成分
                updateCount = db.update(ContactOpenHelper.T_CONTACT, values, selection,
                        selectionArgs);
                if (updateCount > 0) {
                    System.out.println("----ContactsProvider--------updateSuccess------");
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        int code = mUriMatcher.match(uri);
        Cursor cursor = null;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();
                // projection表示的就是要查询的食物，sortOrder表示的就是排序
                cursor = db.query(ContactOpenHelper.T_CONTACT, projection, selection,
                        selectionArgs, null, null, sortOrder);
                System.out.println("----ContactsProvider--------querySuccess------");
                break;
        }
        return cursor;  // 返回查询出的数量
    }

    /***************
     * crud end
     **************/

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

}
