package com.jxust.asus.xmpp;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by asus on 2016/8/5.
 * 测试用例
 *
 * @author Administrator
 * @time 2016/8/5 14:45
 */
public class TestContactsProvider extends AndroidTestCase {

    public void testInsert() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "billy@jxust.com");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "大傻");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "dasha");
        getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
    }

    public void testDelete() {
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT, ContactOpenHelper
                .ContactTable.ACCOUNT + "=?", new String[]{"billy@jxust.com"});
    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "billy@jxust.com");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "二傻");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "ersha");
        getContext().getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{"billy@jxust.com"});
    }

    public void testQuery() {
        Cursor cursor = getContext().getContentResolver().query(ContactsProvider.URI_CONTACT,
                null,null,null,null);
        int columnCount = cursor.getColumnCount();  // 一共多少列
        while (cursor.moveToNext()) {
            // 循环打印列
            for (int i = 0; i < columnCount; i++) {
                System.out.print(cursor.getString(i) + "       ");
            }
            System.out.println("");
        }
    }

    public void testPinyin(){
//        String pinyinString = PinyinHelper.convertToPinyinString(内容, 分隔符, 拼音的格式);
        // PinyinFormat.WITHOUT_TONE表示就是拼音
        // PinyinFormat.WITH_TONE_MARK表示有拼音的音调
        String pinyinString = PinyinHelper.convertToPinyinString("江西理工大学", "", PinyinFormat.WITHOUT_TONE);
        System.out.println(pinyinString);
    }

}
