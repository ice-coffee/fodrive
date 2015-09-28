package com.jinglingtec.ijiazu.receivers;//package com.jinglingtec.ijiazu.receivers;
//
//import android.app.ActivityManager;
//import android.app.ActivityManager.*;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import com.baidu.speechsynthesizer.SpeechSynthesizer;
//import com.jinglingtec.ijiazu.R;
//import com.jinglingtec.ijiazu.util.FoContacts;
//import com.jinglingtec.ijiazu.util.FoVoice;
//import com.jinglingtec.ijiazu.util.ThreadUtil;
//
//import java.util.List;
//
///**
// * Created by coffee on 14-11-6.
// */
//public class IjiazuTelephoneReceiver extends BroadcastReceiver
//{
//    private SpeechSynthesizer speechSynthesizer;
//    private String outCall = "android.intent.action.NEW_OUTGOING_CALL";
//    private ActivityManager mAm;
//
//    public static IjiazuTelephoneReceiver instance = null;
//
//    public static IjiazuTelephoneReceiver getInstance()
//    {
//        if (null == instance)
//        {
//            instance = new IjiazuTelephoneReceiver();
//            return instance;
//        }
//        else
//        {
//            return instance;
//        }
//    }
//
//    @Override
//    public void onReceive(final Context context, Intent intent)
//    {
//        mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        String packageName = context.getPackageName();
//        if (!showRunningTasks(packageName))
//        {
//            return;
//        }
//        speechSynthesizer = FoVoice.getSpeechSynthesizer(context);
//
//        Log.e("telephonereceiver", "receiver");
//        //去电
//        if (intent.getAction().equals(outCall))
//        {
//            final String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Log.e("telephonereceiver", "outcall");
//
//            FoContacts contacts = new FoContacts(context);
//            final String name = contacts.getPhoneContacts(phoneNumber);
//            ThreadUtil.execute(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    if (name != null)
//                    {
//                        int ret = speechSynthesizer.speak(context.getResources().getString(R.string.call_to) + name);
//                        if (ret != 0)
//                        {
//                            Log.e("excepion", "eeeeeeeeee");
//                        }
//                    }
//                    else
//                    {
//                        int ret = speechSynthesizer.speak(context.getResources().getString(R.string.call_to) + phoneNumber);
//                        if (ret != 0)
//                        {
//                            Log.e("excepion", "eeeeeeeeee");
//                        }
//                    }
//
//                }
//            });
//        }
//        else
//        {
//            Log.e("telephonereceiver", "incall");
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
//
//            final String incoming_number = intent.getStringExtra("incoming_number");
//
//            FoContacts contacts = new FoContacts(context);
//            final String name = contacts.getPhoneContacts(incoming_number);
//
//            switch (tm.getCallState())
//            {
//                case TelephonyManager.CALL_STATE_RINGING:
//                    ThreadUtil.execute(new Runnable()
//                    {
//
//                        @Override
//                        public void run()
//                        {
//
//                            if (name != null)
//                            {
//
//                                int ret = speechSynthesizer.speak(name + context.getResources().getString(R.string.to_the_telephone));
//                                if (ret != 0)
//                                {
//                                    Log.e("excepion", "eeeeeeeeee");
//                                }
//                            }
//                            else
//                            {
//                                int ret = speechSynthesizer.speak(incoming_number + context.getResources().getString(R.string.to_the_telephone));
//                                if (ret != 0)
//                                {
//                                    Log.e("excepion", "eeeeeeeeee");
//                                }
//                            }
//
//                        }
//                    });
//
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE:
//                    speechSynthesizer.pause();
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//
//                    break;
//
//            }
//        }
//    }
//
//    // 查看app是否运行中的任务
//    public boolean showRunningTasks(String packageName)
//    {
//        List<RunningTaskInfo> taskList = mAm.getRunningTasks(100);
//        for (RunningTaskInfo rti : taskList)
//        {
//            String name = rti.baseActivity.getPackageName();
//            if (null != packageName && null != name)
//            {
//                if (packageName.equals(name))
//                {
//                    return true;
//                }
//            }
//
//        }
//        return false;
//    }
//}
