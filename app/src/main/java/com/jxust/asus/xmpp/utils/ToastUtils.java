package com.jxust.asus.xmpp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by asus on 2016/8/4.
 *
 * @author Administrator
 * @time 2016/8/4 15:03
 */
public class ToastUtils {
    /**
     * 显示可以在子线程中显示Toast
     * @param context
     * @param text
     */
    public static void showToastSafe(final Context context, final String text){
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
