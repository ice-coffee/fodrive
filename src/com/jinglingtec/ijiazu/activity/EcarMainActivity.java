package com.jinglingtec.ijiazu.activity;//package com.jinglingtec.ijiazu.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.ecar.csp.http.EcarRegisterThread;
//import com.ecar.ecarnet.EcarNetService;
//import com.ecar.ecarnet.EcarSentMsgTypConfig;
//import com.google.gson.Gson;
//import com.jinglingtec.ijiazu.R;
//import com.jinglingtec.ijiazu.invokeApps.baidunavi.RoutePlanActivity;
//import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
//import com.jinglingtec.ijiazu.util.*;
//
//import java.util.ArrayList;
//import java.util.concurrent.ThreadFactory;
//
//@SuppressLint("HandlerLeak")
//public class EcarMainActivity extends Activity
//{
//    private static String TAG = "MainActivity";
//    private Button mEcarButton;
//    private Button mRegister;
//    private EcarRegisterThread registerThread;// 翼卡注册线程
//    private MyBroadcastReceiver receiver;
//    private String ACTION = EcarSentMsgTypConfig.SENT_MSG_TO_TIMA_BY_BROADCAST;
//    private Intent intentService;
//
//    private TextView test;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ecarmain);
//        mEcarButton = (Button) findViewById(R.id.button1);
//        mRegister = (Button) findViewById(R.id.button2);
//
//        test = (TextView) findViewById(R.id.tv_test);
//
//        mEcarButton.setOnClickListener(clickListener);
//        mRegister.setOnClickListener(clickListener);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION);
//        receiver = new MyBroadcastReceiver();
//        registerReceiver(receiver, filter);
//
//    }
//
//    /**
//     * 注册
//     */
//    private void register()
//    {
//        registerThread = new EcarRegisterThread(this, "18515992572", null, null)
//        {
//
//            @Override
//            public void onHttpRegisterResponse(String result)
//            {
//                // TODO Auto-generated method stub
//                if (result.equals("success"))
//                {
//                    handler.sendEmptyMessage(100);
//                }
//                else
//                {
//                    handler.sendEmptyMessage(101);
//                }
//
//            }
//        };
//        registerThread.start();
//    }
//
//    private Handler handler = new Handler()
//    {
//        public void handleMessage(android.os.Message msg)
//        {
//            switch (msg.what)
//            {
//                case 100:
//                    showToast("注册成功");
//                    Log.e(TAG, "注册成功");
//                    showText("注册成功");
//                    break;
//                case 101:
//                    showToast("注册失败");
//                    Log.e(TAG, "注册失败");
//                    showText("注册失败");
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        ;
//    };
//
//    private final OnClickListener clickListener = new OnClickListener()
//    {
//
//        @Override
//        public void onClick(View view)
//        {
//            switch (view.getId())
//            {
//                // 启动翼卡服务
//                case R.id.button1:
//                    intentService = new Intent(EcarMainActivity.this,
//                            EcarNetService.class);
//                    intentService.putExtra("ecarPhoneNumber", "4008005005");
//                    startService(intentService);
//                    break;
//                // 注册
//                case R.id.button2:
//                    register();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private void showToast(String message)
//    {
//        Toast.makeText(EcarMainActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    class MyBroadcastReceiver extends BroadcastReceiver
//    {
//
//        @Override
//        public void onReceive(Context arg0, Intent intent)
//        {
//            // TODO Auto-generated method stub
//            int msgType = intent.getIntExtra("msgType", -1);
//            switch (msgType)
//            {
//                // 正在打电话
//                case EcarSentMsgTypConfig.SENT_MSG_ECAR_CALLING:
//
//                    break;
//                // 退出
//                case EcarSentMsgTypConfig.SENT_MSG_EXIT:
//                    EcarMainActivity.this.stopService(intentService);
//                    break;
//                // 没有网络
//                case EcarSentMsgTypConfig.SENT_MSG_SHOW_NO_NETWORK_DIALOG:
//                    showToast("您没有打开网络");
//                    showText("您没有打开网络");
//                    break;
//                // 下发搜索的联系人
//                case EcarSentMsgTypConfig.SENT_MSG_CONCACT_RESULT:
//                    ArrayList<Bundle> contacts = intent
//                            .getParcelableArrayListExtra("msgObject");
//                    String app = null;
//                    for (int i = 0; i < contacts.size(); i++)
//                    {
//
//                        String namephone = "姓名：" + contacts.get(i).getString("name") + ",电话号码:"
//                                + contacts.get(i).getString("phoneNumber");
//                        Log.i(TAG, namephone);
//                        app += namephone;
//
//                    }
//                    showText(app);
//                    break;
//                // 下发一键导航数据
//                case EcarSentMsgTypConfig.SENT_MSG_FETCHDDEST_RESULT:
//                    Bundle bundle = intent.getExtras().getBundle("msgObject");
//
//                    String poiName = bundle.getString("poiName").toString();
//                    String longitude = bundle.getString("longitude").toString();
//                    String latitude = bundle.getString("latitude").toString();
//
//                    Address ecarAddress = new Address();
//                    ecarAddress.setAddress(poiName);
//                    ecarAddress.setName(poiName);
//                    ecarAddress.setLatitude(Double.valueOf(latitude));
//                    ecarAddress.setLongitude(Double.valueOf(longitude));
//
//                    adaf(arg0, ecarAddress);
//
//                    String res = "地址：" + poiName
//                            + "，经纬：" + longitude
//                            + "，纬度：" + latitude;
//                    Log.i(TAG, res);
//                    showText(res);
//                    break;
//                // 获取联系人
//                case EcarSentMsgTypConfig.SENT_MSG_TELPHONE_RESULT:
//                    final String phone = intent.getStringExtra("msgObject");
//                    Log.i(TAG, "电话号码：" + phone);
//                    showText("电话号码：" + phone);
//                    ThreadUtil.execute(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            try
//                            {
//                                Thread.sleep(2000);
//                            }
//                            catch (Exception e)
//                            {
//                                e.getMessage();
//                            }
//
//                            TTSPlayerUtil.playOutCall(getApplicationContext(), phone, null);
//                        }
//                    });
//
//                    break;
//                // 没有注册
//                case EcarSentMsgTypConfig.SENT_MSG_NOT_REGISTER:
//                    showToast("您没有注册");
//                    showText("您没有注册");
//                    break;
//                // 获取错误信息
//                case EcarSentMsgTypConfig.SENT_MSG_FETCH_RESULT_ERROR:
//                    String error = intent.getStringExtra("msgObject");
//                    Log.i(TAG, "error：" + error);
//                    showText("error：" + error);
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    }
//
//    public void adaf(Context ctx, Address ecarAddress)
//    {
//
//
//        if (null == ctx)
//        {
//            return;
//        }
//        FoPreference.putString(FoConstants.NAVI_TO, FoConstants.NAVI_HOME);
//        Intent intent = new Intent(ctx, RoutePlanActivity.class);
//        intent.putExtra(FoConstants.END_ADDRESS, new Gson().toJson(ecarAddress));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ctx.startActivity(intent);
//    }
//
//    public void showText(String str)
//    {
//        test.setText(str);
//    }
//
//    @Override
//    protected void onDestroy()
//    {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
//}
