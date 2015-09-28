package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.jinglingtec.ijiazu.R;

/**
 * 语音播报
 */
public class TTSPlayerUtil
{
    private static String TAG = "ecaradapter";

    private static SpeechSynthesizer speechSynthesizer;

    public static void play(final Context context, final String str)
    {
        Log.e("speechstring", str);
        speechSynthesizer = FoVoice.getSpeechSynthesizer(context);

        ThreadUtil.execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (null == speechSynthesizer || null == str)
                {
                    return;
                }

                int ret = speechSynthesizer.speak(str);
                if (ret != 0)
                {
                    Log.e("excepion", "eeeeeeeeee");
                }
            }
        });

    }

    public static void playOutCall(final Context context, final String phone_number, String phone_name)
    {
        speechSynthesizer = FoVoice.getSpeechSynthesizer(context);

        if (null == phone_number)
        {
            return;
        }

        if (null == phone_name)
        {
            FoContacts contacts = new FoContacts(context);
            phone_name = contacts.getPhoneContacts(phone_number);
        }

        final String contact_name = phone_name;

        ThreadUtil.execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (null == speechSynthesizer)
                {
                    return;
                }

                int ret = 0;

                if (null != contact_name)
                {
                    ret = speechSynthesizer.speak(context.getResources().getString(R.string.call_to) + contact_name);
                }
                else
                {
                    ret = speechSynthesizer.speak(context.getResources().getString(R.string.call_to) + phone_number);
                }

                //暂停三秒让去电语音播放完毕
                try
                {
                    Thread.sleep(3000);
                } catch (Exception e)
                {
                    Log.e("", e.getMessage());
                }

                if (ret != 0)
                {
                    Log.e("excepion", "eeeeeeeeee");
                }
                else
                {

                    //替换号码间隔符
                    String number = phone_number.replace(" ", "").replace("-", "");

                    if (null == number)
                    {
                        return;
                    }
                    Log.e(TAG, "收到ecar拨打电话命令 333333333333333333");
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

}
