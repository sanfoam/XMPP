package com.jxust.asus.xmpp.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;
import com.jxust.asus.xmpp.dbhelper.SmsOpenHelper;
import com.jxust.asus.xmpp.provider.SmsProvider;
import com.jxust.asus.xmpp.service.IMService;
import com.jxust.asus.xmpp.utils.ThreadUtils;

import org.jivesoftware.smack.packet.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends Activity {

    public static final String CLICKACCOUNT = "clickAccount";
    public static final String CLICKNICKNAME = "clickNickname";
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.et_body)
    EditText mEtBody;

    private String mClickAccount;
    private String mClickNickname;
    private CursorAdapter mAdapter;
    private IMService mImService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        init();
        initView();
        initData();
        initListener();
    }

    private void init() {
        registerContentObserver();  // 注册监听

        // 通过混合模式来绑定服务(service)
        Intent service = new Intent(ChatActivity.this,IMService.class);
        /**
         * 参数1表示的是要绑定的服务
         * 参数2表示的是连接对象，implements ServiceConnection
         * 参数3 BIND_AUTO_CREATE表示的意思如果服务还没创建的话就创建,如果服务还没绑定就绑定
         */
        bindService(service, mMyServiceConnection,BIND_AUTO_CREATE);

        mClickAccount = getIntent().getStringExtra(CLICKACCOUNT);
        mClickNickname = getIntent().getStringExtra(CLICKNICKNAME);

    }

    private void initView() {
        // 设置title
        mTitle.setText("与" + mClickNickname + "聊天中");
    }

    private void initData() {
        setAdapterOrNotify();

    }

    private void setAdapterOrNotify() {
        // 1.首先判断是否存在adapter
        if (mAdapter != null) {
            // 刷新操作
            Cursor cursor = mAdapter.getCursor();
            cursor.requery();
            mListView.setSelection(cursor.getCount() - 1);    // 滚动到最后一行
            return;
        }

        // 数据的查询在子线程完成
        ThreadUtils.runInThread(new Runnable() {

            @Override
            public void run() {
                final Cursor cursor = getContentResolver().query(SmsProvider.URI_SMS, null, null,
                        null, "time asc");  // asc 升序，desc 降序
                // 如果没有数据，直接返回
                if (cursor.getCount() < 1) {
                    return;
                }


                // Adapter的创建需要放到主线程中
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        // CursorAdapter : getView --> newView --> bindView

                        // 如果convertView == null 的时候会调用 --> 返回根布局
                        // 设置具体数据
                        mAdapter = new CursorAdapter(ChatActivity.this, cursor) {
                            public static final int RECEIVE = 0;
                            public static final int SEND = 1;
                            // 如果convertView == null 的时候会调用 --> 返回根布局
                           /* @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                TextView tv = new TextView(context);
                                return tv;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                // 设置具体数据
                                TextView tv = (TextView) view;
                                String body = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.BODY));
                                tv.setText(body);
                            }*/

                            /**
                             * 获取消息类型
                             * @param position
                             * @return
                             */
                            @Override
                            public int getItemViewType(int position) {
                                cursor.moveToPosition(position);
                                // 取出消息的创建者
                                String fromAccount = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.FROM_ACCOUNT));
                                if (!IMService.mCurAccount.equals(fromAccount)) { //
                                    // 表示消息不是"我"创建的,接收操作
                                    return RECEIVE;
                                } else {    // 表示消息是"我"创建的，发送操作
                                    return SEND;
                                }

                                // 接收   --> 如果当前的账号 不等于  消息的创建者
                                // 发送

//                                return super.getItemViewType(position); // 0 1
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                ViewHolder holder = null;
                                if (getItemViewType(position) == RECEIVE) {     // 表示用户是在接收消息
                                    if (convertView == null) {
                                        convertView = View.inflate(ChatActivity.this, R.layout
                                                .item_chat_receive, null);
                                        holder = new ViewHolder();
                                        convertView.setTag(holder);

                                        // holder赋值
                                        holder.body = (TextView) convertView.findViewById(R.id
                                                .body);
                                        holder.time = (TextView) convertView.findViewById(R.id
                                                .time);
                                        holder.head = (ImageView) convertView.findViewById(R.id
                                                .head);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                } else {    // 表示用户是在发送消息
                                    if (convertView == null) {
                                        convertView = View.inflate(ChatActivity.this, R.layout
                                                .item_chat_send, null);
                                        holder = new ViewHolder();
                                        convertView.setTag(holder);

                                        // holder赋值
                                        holder.body = (TextView) convertView.findViewById(R.id
                                                .body);
                                        holder.time = (TextView) convertView.findViewById(R.id
                                                .time);
                                        holder.head = (ImageView) convertView.findViewById(R.id
                                                .head);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                }
                                // 得到数据，展示数据
                                cursor.moveToPosition(position);

                                String time = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.TIME));

                                String body = cursor.getString(cursor.getColumnIndex
                                        (SmsOpenHelper.SMSTable.BODY));

                                // 格式化时间
                                String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        .format(new Date(Long.parseLong(time)));

                                holder.time.setText(formatTime);    // 将格式化后的时间显示出来
                                holder.body.setText(body);      // 显示消息内容

                                return super.getView(position, convertView, parent);
                            }

                            @Override
                            public int getViewTypeCount() {
                                return super.getViewTypeCount() + 1;    // 2
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                            }

                            class ViewHolder {
                                TextView body;  // 消息内容
                                TextView time;  // 消息发送的时间
                                ImageView head; // 头像

                            }
                        };
                        mListView.setAdapter(mAdapter);
                        mListView.setSelection(cursor.getCount() - 1);    // 滚动到最后一行
                    }
                });
            }
        });
    }

    private void initListener() {

    }

    public void send(View v) {
        final String body = mEtBody.getText().toString();
        if (!body.equals("")) {     // 避免发送空消息
            // 由于要去服务里面调用sendMessage方法，所以要使用子线程去执行操作
            ThreadUtils.runInThread(new Runnable() {
                @Override
                public void run() {
//          3.初始化了一个消息
                    Message msg = new Message();
                    msg.setFrom(IMService.mCurAccount); // 消息的来源，当前登录的用户
                    msg.setTo(mClickAccount);           // 消息发送的目的地
                    msg.setBody(body);                  // 输入框里面的内容
                    msg.setType(Message.Type.chat);     // 消息的类型，类型就是聊天
//          msg.setProperty("key", "value");     // 额外的属性-->其实就是额外的信息,这里我们不使用

            // 调用服务里面的sendMessage这个方法来发送消息
                mImService.sendMessage(msg);

//          4.清空输入框，回到主线程中进行
                    ThreadUtils.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mEtBody.setText("");  // 将输入框清空
                        }
                    });
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterContentObserver();    // 反注册监听
        if(mMyServiceConnection != null) {
            unbindService(mMyServiceConnection);    // 解绑服务,直接传入连接对象即可
        }
    }

    /* ==============使用ContentObserver时刻监听记录的改变 begin=================*/
    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);
    }

    /**
     * 反注册监听
     */
    public void unRegisterContentObserver() {
        getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 接收到数据记录的改变
         * 如果数据库数据改变会在这个方法收到通知
         *
         * @param selfChange
         */
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 设置adapter或者更新adapter
            setAdapterOrNotify();
        }
    }
    /* ==============使用ContentObserver时刻监听记录的改变 end=================*/


    // 创建连接对象
    MyServiceConnection mMyServiceConnection = new MyServiceConnection();

    // ServiceConnection的作用就是用于获取service和activity的连接信息
    class MyServiceConnection implements ServiceConnection {

        // service连接
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("=========onServiceConnected=========");
            IMService.MyBinder binder = (IMService.MyBinder) service;
            // 通过Binder获得service实例
            mImService = binder.getService();
        }

        // service断开连接
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("=========onServiceDisconnected=========");
        }
    }
}
