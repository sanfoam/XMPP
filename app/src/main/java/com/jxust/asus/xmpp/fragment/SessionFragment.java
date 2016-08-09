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
import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;
import com.jxust.asus.xmpp.provider.ContactsProvider;
import com.jxust.asus.xmpp.provider.SmsProvider;
import com.jxust.asus.xmpp.service.IMService;
import com.jxust.asus.xmpp.utils.ThreadUtils;

/**
 * A simple {@link Fragment} subclass.
 * 会话的fragment
 */
public class SessionFragment extends Fragment {


    private ListView mListView;
    private CursorAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initData();
        initListener();
    }

    private void init() {
        registerContentObserver();  // 注册监听
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    private void initData() {
        setOrNotifyAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterContentObserver();    //反注册监听
    }

    private void setOrNotifyAdapter() {
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
                String[] selectionArgs = new String[]{IMService.mCurAccount, IMService.mCurAccount};

                final Cursor cursor = getActivity().getContentResolver().query(SmsProvider
                        .URI_SESSION, null, null, selectionArgs, ContactOpenHelper.ContactTable
                        .PINYIN);

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
                                View view = View.inflate(context, R.layout.item_session, null);
                                return view;
                            }

                            // 设置数据显示数据
                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                //   TextView tv = (TextView) view;
                                ImageView ivHead = (ImageView) view.findViewById(R.id.head);
                                TextView tvBody = (TextView) view.findViewById(R.id.body);
                                TextView tvNickName = (TextView) view.findViewById(R.id.nickname);

                                String body = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.BODY));
                                String account = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.SESSION_ACCOUNT));
                                String nickname = getNickNameByAccount(account);

                                tvBody.setText(body);
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

    public String getNickNameByAccount(String account) {
        String nickname = "";
        Cursor cursor = getActivity().getContentResolver().query(ContactsProvider.URI_CONTACT, null,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account}, null);
        if(cursor.getCount() > 0){  // 有数据
            cursor.moveToFirst();
            nickname = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable
                    .NICKNAME));
        }
        return nickname     ;
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private Cursor cursor;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);

                // 拿到JID(账号)-->发送消息的时候需要
                String account = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SMSTable.SESSION_ACCOUNT));
                // 拿到nickname-->显示效果
                String nickname = getNickNameByAccount(account);

                Intent intent = new Intent(getActivity(),ChatActivity.class);

                intent.putExtra(ChatActivity.CLICKACCOUNT,account);
                intent.putExtra(ChatActivity.CLICKNICKNAME,nickname);
                startActivity(intent);
            }
        });
    }


    /**
     * 注册监听
     */
    public void registerContentObserver() {

        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS,
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
            setOrNotifyAdapter();
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
