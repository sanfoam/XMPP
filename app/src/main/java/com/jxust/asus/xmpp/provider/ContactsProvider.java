package com.jxust.asus.xmpp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName(); // 得到一个类的完整路径

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
        if(mHelper != null){
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


        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
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
