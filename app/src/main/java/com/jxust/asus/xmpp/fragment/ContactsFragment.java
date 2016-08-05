package com.jxust.asus.xmpp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.service.IMService;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 * 联系人的fragment
 */
public class ContactsFragment extends Fragment {

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    private void initView(View view) {

    }

    private void init() {

    }

    private void initListener() {
    }

    private void initData() {
        // 得到所有的联系人
        // 需要连接对象
        XMPPConnection conn = IMService.conn;
        // 得到花名册
        Roster roster = conn.getRoster();
        // 得到花名册中的所有的联系人
        Collection<RosterEntry> entries = roster.getEntries();

        // 打印所有的联系人
        for (RosterEntry entry : entries) {
            System.out.print(entry.toString() + "  ");
            System.out.print("user:" + entry.getUser() + "  ");     // 对应的地址(JOD) account,用户的唯一标识
            System.out.print("name:" + entry.getName() + "  ");     // nickname 别名
            /**
             * XMPP的地址模式
             * 统一的JID(jabber identifier)
             * JID=[ node”@” ] domain [ “/” resource ]
             * eg: cyber@cyberobject.com/res
             * domain:服务器域名
             * node: 用户名
             * resource:属于用户的位置或设备
             */
//            System.out.print("status:" + entry.getStatus() + "  ");
//            System.out.println("type:" + entry.getType());
        }

        /**
         * jxust: jxust@127.0.0.1 [Friends]  name:jxust  user:jxust@127.0.0.1  status:subscribe  type:none
         * sunfeng: sunfeng@127.0.0.1 [Friends]  name:sunfeng  user:sunfeng@127.0.0.1  status:null  type:both
         */

    }
}
