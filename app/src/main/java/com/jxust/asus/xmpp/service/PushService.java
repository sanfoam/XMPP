package com.jxust.asus.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jxust.asus.xmpp.utils.ToastUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by asus on 2016/8/9.
 *
 * @author Administrator
 * @time 2016/8/9 16:30
 */
public class PushService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("===========PushService onCreate=============");
        // 第二个参数表示的是是否要过滤操作
        IMService.conn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                String body = message.getBody();
                ToastUtils.showToastSafe(getApplicationContext(),body); // 模拟客户端收到从服务端直接推送的消息
            }
        }, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("===========PushService onDestroy=============");
    }
}
