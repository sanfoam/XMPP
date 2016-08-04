package com.jxust.asus.xmpp.utils;

import android.os.Handler;

/**
 * Created by asus on 2016/8/3.
 *
 * @author Administrator
 * @time 2016/8/3 15:38
 */
public class ThreadUtils {


    /**
     * 子线程执行task
     */
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }


    // 主线程里面的Handler
    public static Handler mHandler = new Handler();

    /**
     * UI线程执行task
     */
    public static void runInUIThread(Runnable task){
        mHandler.post(task);
    }

}
