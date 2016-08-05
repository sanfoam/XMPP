package com.jxust.asus.xmpp.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;
import com.jxust.asus.xmpp.service.IMService;
import com.jxust.asus.xmpp.utils.PinyinUtil;
import com.jxust.asus.xmpp.utils.ThreadUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 * 联系人的fragment
 */
public class ContactsFragment extends Fragment {

    private View view;
    private ListView mListView;
    private Roster mRoster;
    private CursorAdapter mAdapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

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
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    private void init() {   // 在Activity一创建就进行注册监听
        // 注册监听
        registerContentObserver();
    }

    private void initListener() {
    }

    private void initData() {
        // 开启线程，同步花名册
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                // 得到所有的联系人
                // 需要连接对象
                XMPPConnection conn = IMService.conn;
                // 得到花名册
                mRoster = conn.getRoster();
                // 得到花名册中的所有的联系人
                final Collection<RosterEntry> entries = mRoster.getEntries();

                // 打印所有的联系人
                for (RosterEntry entry : entries) {
                    System.out.print(entry.toString() + "  ");
                    System.out.print("user:" + entry.getUser() + "  ");     // 对应的地址(JOD)
                    // account,用户的唯一标识
                    System.out.print("name:" + entry.getName() + "  ");     // nickname 别名
                    System.out.println("");
                }

                // 监听联系人的改变
                mRoster.addRosterListener(new MyRosterListener());

                for (RosterEntry entry : entries) {
                    saveOrUpdateEntry(entry);
                }
                setOrUpdateAdapter();
            }
        });


        /**
         * XMPP的地址模式
         * 统一的JID(jabber identifier)
         * JID=[ node”@” ] domain [ “/” resource ]
         * eg: cyber@cyberobject.com/res
         * domain:服务器域名
         * node: 用户名
         * resource:属于用户的位置或设备
         */

        /**
         * jxust: jxust@127.0.0.1 [Friends]  name:jxust  user:jxust@127.0.0.1  status:subscribe
         * type:none
         * sunfeng: sunfeng@127.0.0.1 [Friends]  name:sunfeng  user:sunfeng@127.0.0.1
         * status:null  type:both
         */

    }

    /**
     * 设置或者更新adapter
     */
    private void setOrUpdateAdapter() {

        // 判断adapter是否存在
        if (mAdapter != null) {
            // 刷新adapter就行了
            mAdapter.getCursor().requery();
            return;
        }

        // 对应查询记录
        final Cursor cursor = getActivity().getContentResolver().query(ContactsProvider
                .URI_CONTACT, null, null, null, null);

        // 假如没有数据的时候
        if (cursor.getCount() <= 0) {
            return;
        }

        // 设置Adapter，然后显示数据
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                // CursorAdapter专门针对于数据从数据库中来的时候
                // 如果convertView == null,返回一个具体的视图
                //   TextView tv = new TextView(context);
                // 设置数据显示数据
                //   TextView tv = (TextView) view;
                //    tv.setText(account);
                mAdapter = new CursorAdapter(getActivity(), cursor) {
                    // 如果convertView == null,返回一个具体的视图
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        //   TextView tv = new TextView(context);
                        View view = View.inflate(context, R.layout.item_contact, null);
                        return view;
                    }

                    // 设置数据显示数据
                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        //   TextView tv = (TextView) view;
                        ImageView ivHead = (ImageView) view.findViewById(R.id.head);
                        TextView tvAccount = (TextView) view.findViewById(R.id.account);
                        TextView tvNickName = (TextView) view.findViewById(R.id.nickname);

                        String account = cursor.getString(cursor.getColumnIndex
                                (ContactOpenHelper.ContactTable.ACCOUNT));
                        String nickname = cursor.getString(cursor.getColumnIndex
                                (ContactOpenHelper.ContactTable.NICKNAME));
                        tvAccount.setText(account);
                        tvNickName.setText(nickname);
                        //    tv.setText(account);

                    }
                };
                mListView.setAdapter(mAdapter);
            }
        });
    }

    private void saveOrUpdateEntry(RosterEntry entry) {
        ContentValues values = new ContentValues();
        String account = entry.getUser();
        //                    account = account.substring(0,account.indexOf("@"))+"@"+ LoginActivity
        // .SERVICENAME;
        String nickname = entry.getName();
        String pinyin = new PinyinUtil().getPinyin(entry.getName());
        System.out.println("acount:" + account);
        System.out.println("nickname:" + nickname);
        System.out.println("pinyin:" + pinyin);
        // 处理昵称
        if (nickname == null || "".equals((nickname))) {
            // 如果没有昵称就将用户名的改成昵称
            nickname = account.substring(0, account.indexOf("@"));   // billy@jxust.com  -->  billy
        }
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, pinyin);

        // 先update，后插入  重点
        int updateCount = getActivity().getContentResolver().update(ContactsProvider.URI_CONTACT,
                values, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) { // 没有更新到任何数据
            getActivity().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }

    private class MyRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {       // 联系人添加了
            System.out.println("--------------------entriesAdded-------------------------");
            // 对应更新数据库
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新，要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {     // 联系人修改了
            System.out.println("--------------------entriesUpdated-------------------------");
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 要么更新，要么插入
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {     // 联系人删除了
            System.out.println("--------------------entriesDeleted-------------------------");
            for (String address : addresses) {
                RosterEntry entry = mRoster.getEntry(address);
                // 执行删除操作
                getActivity().getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{entry.getUser
                                ()});
            }
        }

        @Override
        public void presenceChanged(Presence presence) {                // 联系人状态改变了
            System.out.println("--------------------presenceChanged-------------------------");
        }
    }

    @Override
    public void onDestroy() {   // 此方法是在Activity销毁之前执行的
        super.onDestroy();
        // 在Activity销毁之前反注册监听
        unRegisterContentObserver();
    }

    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {

        getActivity().getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT,
                true, mMyContentObserver);
    }

    /**
     * 反注册监听
     */
    public void unRegisterContentObserver(){
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    /*========================监听数据库记录的改变====================*/
    class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         * 如果数据库数据改变会在这个方法收到通知
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            // 更新adapter或者刷新adapter
            setOrUpdateAdapter();
        }
    }
}
