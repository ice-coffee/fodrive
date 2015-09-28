package com.jinglingtec.ijiazu.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoContacts;
import com.jinglingtec.ijiazu.util.TTSPlayerUtil;

/**
 * Created by coffee on 14-12-30.
 */
public class TelephoneService extends Service
{
    //    private String outCall = "android.intent.action.NEW_OUTGOING_CALL";
    private String inCall = "android.intent.action.PHONE_STATE";
    //    private outCallReceiver outReceiver;
    private inCallReceiver inReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        //        IntentFilter outfilter = new IntentFilter(outCall);
        //        outReceiver = new outCallReceiver();
        //        registerReceiver(outReceiver, outfilter);

        IntentFilter infilter = new IntentFilter(inCall);
        inReceiver = new inCallReceiver();
        registerReceiver(inReceiver, infilter);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(inReceiver);
        //        unregisterReceiver(outReceiver);
        super.onDestroy();
    }

    //    class outCallReceiver extends BroadcastReceiver
    //    {
    //        @Override
    //        public void onReceive(final Context context, Intent intent)
    //        {
    //            //去电
    //            if (intent.getAction().equals(outCall))
    //            {
    //                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
    //
    //                FoContacts contacts = new FoContacts(context);
    //                String name = contacts.getPhoneContacts(phoneNumber);
    //
    //                if (name != null)
    //                {
    //                    TTSPlayerUtil.play(context,context.getResources().getString(R.string.call_to) + name);
    //                }
    //                else
    //                {
    //                    TTSPlayerUtil.play(context,context.getResources().getString(R.string.call_to) + phoneNumber);
    //                }
    //
    //            }
    //        }
    //
    //
    //    }

    class inCallReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            if (intent.getAction().equals(inCall))
            {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

                String incoming_number = intent.getStringExtra("incoming_number");

                FoContacts contacts = new FoContacts(context);
                String name = contacts.getPhoneContacts(incoming_number);

                switch (tm.getCallState())
                {
                    case TelephonyManager.CALL_STATE_RINGING:

                        //电话显示“未知”
                        if (null == incoming_number && null == name)
                        {
                            TTSPlayerUtil.play(context, context.getResources().getString(R.string.unknow_telephone));
                            return;
                        }

                        if (name != null)
                        {
                            TTSPlayerUtil.play(context, name + context.getResources().getString(R.string.to_the_telephone));
                        }
                        else
                        {
                            TTSPlayerUtil.play(context, incoming_number + context.getResources().getString(R.string.to_the_telephone));
                        }

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        //                    speechSynthesizer.pause();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        break;

                }
            }


        }

    }
}
