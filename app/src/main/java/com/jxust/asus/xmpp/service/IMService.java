package com.jxust.asus.xmpp.service;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by asus on 2016/8/5.
 *
 * @author Administrator
 * @time 2016/8/5 9:13
 */
public class IMService {
    public static XMPPConnection conn;  // 一旦被static修改就不再被GC所回收了

}
