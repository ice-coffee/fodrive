package com.jinglingtec.ijiazu.invokeApps.ecar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.ecar.csp.http.EcarRegisterThread;
import com.ecar.ecarnet.EcarNetService;
import com.ecar.ecarnet.EcarSentMsgTypConfig;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.invokeApps.FokeyAdapter;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.RoutePlanActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
import com.jinglingtec.ijiazu.util.*;

import java.util.ArrayList;

//@SuppressLint("HandlerLeak")EcarNetService
public class EcarAdapter implements FokeyAdapter
{
    private static String TAG = "ecaradapter";

    private EcarBroadcastReceiver ecarReceiver;

    private Intent intentService;

    public void initialize(Context ctx)
    {
        registerEcarReceiver(ctx);
    }

    public void release(Context ctx)
    {
    }

    public void singleClick(Context ctx)
    {
        dialEcar(ctx);
    }

    public void doubleClick(Context ctx)
    {
    }

    public void longPressed(Context ctx)
    {
    }

    //注册监听
    public void registerEcarReceiver(Context ctx)
    {
        final String ACTION = EcarSentMsgTypConfig.SENT_MSG_TO_TIMA_BY_BROADCAST;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        ecarReceiver = new EcarBroadcastReceiver();
        ctx.registerReceiver(ecarReceiver, filter);
    }

    //停止翼卡服务
    private void unregisterEcarReceiver(Context ctx)
    {
        Log.i(TAG, "destoryecar");
        if (ecarReceiver != null)
        {
            ctx.unregisterReceiver(ecarReceiver);
        }
        ecarReceiver = null;
    }


    /**
     * 注册翼卡服务
     */
    public void register(final Context ctx, String localNumber)
    {
        if (null == localNumber)
        {
            localNumber = loadLocalMobileNum(ctx);
        }

        EcarRegisterThread registerThread = new EcarRegisterThread(ctx, localNumber, null, null)
        {
            @Override
            public void onHttpRegisterResponse(String result)
            {
                final String regSuccess = "success";
                if ((null != result) && (regSuccess.equals(result)))
                {
                    //success
                    Log.e(TAG, "register ecar success");
                    FoPreference.putBoolean(FoConstants.ECAR, true);
                }
                else
                {
                    //false
                    Log.e(TAG, "register ecar failed");
                }
            }
        };
        registerThread.start();
    }

    //启动翼卡服务
    public void dialEcar(Context ctx)
    {
        boolean regEcar = FoPreference.getBoolean(FoConstants.ECAR, false);
        Log.e(TAG, regEcar + "regecar");
        if (regEcar)
        {
            //每次启动翼卡服务都注册一次翼卡广播，并在接收到信息后反注册
            registerEcarReceiver(ctx);

            intentService = new Intent(ctx, EcarNetService.class);
            intentService.putExtra("ecarPhoneNumber", "4008005005");
            ctx.startService(intentService);
            Log.e(TAG, "拨打ecar 4008005005 3333333333333333333333");
        }
        else
        {
            //提示用户注册翼卡服务
            FoSettingOrDown.remindUsers(ctx, FoConstants.ECAR);
        }
    }

    class EcarBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context ctx, Intent intent)
        {
            // TODO Auto-generated method stub
            int msgType = intent.getIntExtra("msgType", -1);
            switch (msgType)
            {
                // 正在打电话
                case EcarSentMsgTypConfig.SENT_MSG_ECAR_CALLING:
                    break;
                // 退出
                case EcarSentMsgTypConfig.SENT_MSG_EXIT:
                    ctx.stopService(intentService);
                    intentService = null;
                    break;
                // 没有网络
                case EcarSentMsgTypConfig.SENT_MSG_SHOW_NO_NETWORK_DIALOG:
                    break;
                // 下发搜索的联系人
                case EcarSentMsgTypConfig.SENT_MSG_CONCACT_RESULT:
                    receivedEcarContacts(ctx, intent);
                    break;
                // 下发一键导航数据
                case EcarSentMsgTypConfig.SENT_MSG_FETCHDDEST_RESULT:
                    receivedEcarNaviTarget(ctx, intent);
                    break;
                // 获取联系人
                case EcarSentMsgTypConfig.SENT_MSG_TELPHONE_RESULT:
                    String phone = intent.getStringExtra("msgObject");
                    if (null == phone)
                    {
                        receiveNot(ctx, R.string.ecar_receive_little);
                        return;
                    }
                    Log.e(TAG, "电话号码：" + phone);
                    startCallPhone(ctx, null, phone);
                    break;
                // 没有注册
                case EcarSentMsgTypConfig.SENT_MSG_NOT_REGISTER:
                    //提示用户注册翼卡服务
                    FoSettingOrDown.remindUsers(ctx, FoConstants.ECAR);
                    break;
                // 获取错误信息
                case EcarSentMsgTypConfig.SENT_MSG_FETCH_RESULT_ERROR:
                    String error = intent.getStringExtra("msgObject");
                    receiveNot(ctx, R.string.ecar_receive_little);
                    Log.e(TAG, "error：" + error);
                    break;
                default:
                    break;
            }

            unregisterEcarReceiver(ctx);
        }

    }

    // received ecar contacts
    private void receivedEcarContacts(Context ctx, Intent intent)
    {
        Log.e(TAG, "收到ecar拨打电话命令 1111111111111");
        final ArrayList<Bundle> contacts = intent.getParcelableArrayListExtra("msgObject");

        if ((null == contacts) || (contacts.size() < 1))
        {
            receiveNot(ctx, R.string.ecar_receive_little);
            return;
        }

        //以下两个数据判断
        Bundle bundleContact = contacts.get(0);

        if (null == bundleContact)
        {
            receiveNot(ctx, R.string.ecar_receive_little);
            return;
        }

        for (int i = contacts.size() - 1; i > -1; i--)
        {
            String namephone = "姓名：" + contacts.get(i).getString("name") + ",电话号码:" + contacts.get(i).getString("phoneNumber");
            Log.e(TAG, namephone);
        }

        String contactName = bundleContact.getString("name");
        String contactNumber = bundleContact.getString("phoneNumber");

        if (null == contactName || null == contactNumber)
        {
            receiveNot(ctx, R.string.ecar_receive_little);
            return;
        }

        //准备拨打打电话
        startCallPhone(ctx, contactName, contactNumber);
    }

    //
    private void receivedEcarNaviTarget(Context ctx, Intent intent)
    {
        Bundle bundleNavi = intent.getExtras().getBundle("msgObject");

        //以下两个数据判断
        if (null == bundleNavi)
        {
            receiveNot(ctx, R.string.ecar_receive_little);
            return;
        }

        String poiName = bundleNavi.getString("poiName").toString();
        String longitude = bundleNavi.getString("longitude").toString();
        String latitude = bundleNavi.getString("latitude").toString();

        if (null == poiName || null == longitude || null == latitude)
        {
            receiveNot(ctx, R.string.ecar_receive_little);
            return;
        }

        //        TTSPlayerUtil.play(ctx, "导航到" + poiName);

        Address ecarAddress = new Address();
        ecarAddress.setAddress(poiName);
        ecarAddress.setName(poiName);
        ecarAddress.setLatitude(Double.valueOf(latitude));
        ecarAddress.setLongitude(Double.valueOf(longitude));

        ecarStartNavi(ctx, ecarAddress);

        Log.e(TAG, "地址：" + poiName + "，经纬：" + longitude + "，纬度：" + latitude);
    }

    // ecar start navigate
    private void ecarStartNavi(Context ctx, Address ecarAddress)
    {
        if (null == ctx)
        {
            return;
        }

        String startAddress = FoPreference.getString(FoConstants.START_ADDRESS);

        if (null == startAddress)
        {
            //提示用户初始化导航
            FoSettingOrDown.remindUsers(ctx, FoConstants.NAVI_ECAR);
            return;
        }
        FoPreference.putString(FoConstants.NAVI_TO, FoConstants.NAVI_HOME);
        Intent intent = new Intent(ctx, RoutePlanActivity.class);
        intent.putExtra(FoConstants.END_ADDRESS, new Gson().toJson(ecarAddress));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public void startCallPhone(final Context ctx, String name, final String phone)
    {
        if (null == ctx)
        {
            return;
        }

        //替换号码间隔符
        String number = phone.replace(" ", "").replace("-", "");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        Log.e(TAG, "收到ecar拨打电话命令 2222222222222222222");
        //        TTSPlayerUtil.playOutCall(ctx, phone, name);
    }

    // load local mobile number to ecar
    protected String loadLocalMobileNum(Context ctx)
    {
        String mobile = FoUtil.readLocalMobileNum(ctx);
        if ((null == mobile) || (FoConstants.MOBILELEN > mobile.length()))
        {
            return null;
        }
        if (FoConstants.MOBILELEN < mobile.length())
        {
            mobile = mobile.substring(mobile.length() - FoConstants.MOBILELEN);
        }

        return mobile;
    }


    private void receiveNot(Context ctx, int resid)
    {
        String ss = ctx.getResources().getString(resid);
        TTSPlayerUtil.play(ctx, ss);
    }

}
