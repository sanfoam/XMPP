package com.jxust.asus.xmpp.utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by asus on 2016/8/5.
 *
 * @author Administrator
 * @time 2016/8/5 15:57
 */
public class PinyinUtil {

    public static String getPinyin(String str){
        String pinyinString = PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
        return pinyinString;
    }
}
