package com.jxust.asus.xmpp.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.activity.ChatActivity;
import com.jxust.asus.xmpp.dbhelper.ContactOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;
import com.jxust.asus.xmpp.utils.ThreadUtils;


/**
 * A simple {@link Fragment} subclass.
 * 联系人的fragment
 */
public class ContactsFragment extends Fragment {

    private View view;
    private ListView mListView;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private Cursor cursor;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);

                // 拿到JID(账号)-->发送消息的时候需要
                String account = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                // 拿到nickname-->显示效果
                String nickname = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));

                Intent intent = new Intent(getActivity(),ChatActivity.class);

                intent.putExtra(ChatActivity.CLICKACCOUNT,account);
                intent.putExtra(ChatActivity.CLICKNICKNAME,nickname);
                startActivity(intent);
            }
        });
    }

    private void initData() {

        setOrUpdateAdapter();

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

        // 开启线程，同步花名册
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                // 对应查询记录
                final Cursor cursor = getActivity().getContentResolver().query(ContactsProvider
                        .URI_CONTACT, null, null, null, ContactOpenHelper.ContactTable.PINYIN);

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
                        // TextView tv = new TextView(context);
                        // 设置数据显示数据
                        // TextView tv = (TextView) view;
                        // tv.setText(account);
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
        });
    }


    @Override
    public void onDestroy() {   // 此方法是在Activity销毁之前执行的
        super.onDestroy();

        // 按照常理，我们Fragment销毁了，那么我们就不应该去继续去监听
        // 但是，实际上，我们是需要一直监听对应roster的改变
        // 所以我们要把联系人的监听和同步操作放到Service去
        // 在Activity销毁之前反注册监听
        unRegisterContentObserver();

    }


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
    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }


    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    /*========================监听数据库记录的改变====================*/
    class MyContentObserver extends ContentObserver {

        /**
         * 如果数据库数据改变会在这个方法收到通知
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            // 更新adapter或者刷新adapter
            setOrUpdateAdapter();
        }

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }
    }
}
