package com.jinglingtec.ijiazu.invokeApps.telephone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.telephony.ITelephony;
import com.jinglingtec.ijiazu.IjiazuApp;
import com.jinglingtec.ijiazu.util.*;

import java.lang.reflect.Method;

public class PhoneCallOperator implements PhoneCall
{
    private static final String TAG = "PhoneCallOperator";

    private TelephonyManager mTelephonyManager = null;
    private int mPhoneCallState = TelephonyManager.CALL_STATE_IDLE;
    private VoiceVolumeBackup volumeBackup = null;
    private boolean isBleConnected = false;

    @Override
    public void singleClicked(Context ctx)
    {
        switch (getPhoneCallState())
        {
            case TelephonyManager.CALL_STATE_IDLE:
                dialSingleClick(ctx);
                //                unMute(ctx);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                answerCall(ctx);
                //                unMute(ctx);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // do nothing
                break;
            default:
                break;
        }
    }

    @Override
    public void doubleClicked(Context ctx)
    {
        switch (getPhoneCallState())
        {
            case TelephonyManager.CALL_STATE_IDLE:
                //                dialDoubleClick(ctx);
                dialLastNumber(ctx);
                //                unMute(ctx);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // fall through
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
            default:
                break;
        }
    }

    @Override
    public void longPressed(Context ctx)
    {
        switch (getPhoneCallState())
        {
            case TelephonyManager.CALL_STATE_IDLE:
                // do nothing
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // fall through
            case TelephonyManager.CALL_STATE_OFFHOOK:
                endCall(ctx);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isIdleState()
    {
        return (TelephonyManager.CALL_STATE_IDLE == mPhoneCallState);
    }

    @Override
    public void onBleStateChanged(boolean newState)
    {
        this.isBleConnected = newState;
    }

    // dial the first contact item, single click
    private void dialSingleClick(Context ctx)
    {
        final ContactsItem[] contactsItems = PhoneLocalStorage.loadContactItems();
        if ((null == contactsItems) || (contactsItems.length < 1))
        {
            //提示用户设置
            FoSettingOrDown.remindUsers(ctx, FoConstants.TELEPHONE);
            return;
        }

        dial(ctx, contactsItems[0].Number);


        //获取设置的快捷联系人item
        //        int item = FoPreference.getInt(FoConstants.TELEPHONE_SELECT);
        //        Log.e(TAG, "dial single click:" + item + " contacts length:" + contactsItems.length);
        //        if (item < contactsItems.length)
        //        {
        //            dial(ctx, contactsItems[item].Number);
        //        }
        //        TTSPlayerUtil.playOutCall(ctx, contactsItems[0].Number, contactsItems[0].Name);
    }

    // dial the second contact item, double click
    //    private void dialDoubleClick(Context ctx)
    //    {
    //        final ContactsItem[] contactsItems = PhoneLocalStorage.loadContactItems();
    //        if ((null == contactsItems) || (contactsItems.length < 2))
    //        {
    //            return;
    //        }
    //
    //        dial(ctx, contactsItems[1].number);
    //    }

    // dial last call/incoming number
    private void dialLastNumber(Context ctx)
    {
        final String lastNum = this.getLastCallNumber(ctx);
        Log.d(TAG, "最后一次电话号码：" + lastNum);
        if (null != lastNum)
        {
            //            Log.d(TAG, "dial to 最后一次电话号码：" + lastNum);
            dial(ctx, lastNum);
            //            DialTheNumber.TelephoneCall(ctx,lastNum);
            //            TTSPlayerUtil.playOutCall(ctx, lastNum, null);
        }
    }

    // dial the third contact item, double click
    //    private void dialLongPressed(Context ctx)
    //    {
    //        final ContactsItem[] contactsItems = PhoneLocalStorage.loadContactItems();
    //        if ((null == contactsItems) || (contactsItems.length < 3))
    //        {
    //            return;
    //        }
    //
    //        dial(ctx, contactsItems[2].number);
    //    }

    // dial a specific number
    private void dial(Context ctx, String number)
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    // answer call
    private void answerCall(Context ctx)
    {
        if (TelephonyManager.CALL_STATE_RINGING != getPhoneCallState())
        {
            return;
        }

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);

        //按下耳机控制键
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
        mediaButtonIntent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
        ctx.sendOrderedBroadcast(mediaButtonIntent, "android.permission.CALL_PRIVILEGED");
        //抬起耳机控制键
        keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        mediaButtonIntent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
        ctx.sendOrderedBroadcast(mediaButtonIntent, "android.permission.CALL_PRIVILEGED");

        //        unMute(ctx);
    }

    // end call
    private void endCall(Context ctx)
    {
        Log.i(TAG, "endCall: state=" + getPhoneCallState() + ", SDK_INT=" + Build.VERSION.SDK_INT);

        /*当前电话为通话状态，或响铃状态，则拒绝来电*/
        if ((TelephonyManager.CALL_STATE_OFFHOOK == getPhoneCallState()) || (TelephonyManager.CALL_STATE_RINGING == getPhoneCallState()))
        {
            //再挂断电话
            //初始化iTelephony
            Class<TelephonyManager> clazzTelphony = TelephonyManager.class;
            ITelephony iTelephony;

            //如下分支代码段在4.2.2（华为G6）和4.1.1（小米2S）测试OK，但是在4.4.4（小米2S，MIUI6）异常。
            //getITelephonyMethod.invoke方法比较怪异，只有小米2s的MIUI6需要参数，小米4和上述早期版本一致，且SDK版本都是19。
            try
            {
                // 获取所有public/private/protected/默认
                // 方法的函数，如果只需要获取public方法，则可以调用getMethod.
                Method getITelephonyMethod = clazzTelphony.getDeclaredMethod("getITelephony", (Class[]) null);
                // 将要执行的方法对象设置是否进行访问检查，也就是说对于public/private/protected/默认
                // 我们是否能够访问。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false
                // 则指示反射的对象应该实施 Java 语言访问检查。
                getITelephonyMethod.setAccessible(true);
                iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null);
                iTelephony.endCall();
            } catch (Exception e1)
            {
                Log.i(TAG, "endCall Error e1 ... ");
                //获取所有方法，不需要给出方法的参数，规避异常。
                Method[] getITelephonyMethods = clazzTelphony.getDeclaredMethods();
                for (Method m : getITelephonyMethods)
                {
                    //Log.i(Tag, "Method: " + m.getName());
                    if (m.getName().equals("getITelephony"))
                    {
                        Log.i(TAG, "Method: " + m.getName());
                        m.setAccessible(true);
                        try
                        {
                            //用上面分支的方法调用invoke接口，异常，提示需要一个参数，而null代表没有参数；
                            //把第2个参数改为为mTelephonyManager，异常，提示需要一个int型参数；
                            //把第2个参数改为1，异常，提示空指针；
                            //把第2个参数改为0，OK。
                            iTelephony = (ITelephony) m.invoke(mTelephonyManager, 0);
                            iTelephony.endCall();
                        } catch (Exception e2)
                        {
                            Log.i(TAG, "endCall Error e2... ");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void release()
    {

    }

    @Override
    public void initialize(Context ctx)
    {
        final PhoneCallListener mPhoneCallListener = new PhoneCallListener();
        mTelephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mTelephonyManager.listen(mPhoneCallListener, PhoneCallListener.LISTEN_CALL_STATE);
    }


    private int getPhoneCallState()
    {
        return mPhoneCallState;
    }

    //    private void unMute(Context ctx)
    //    {
    //        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    //        audioManager.setMode(AudioManager.MODE_IN_CALL);
    //        audioManager.setSpeakerphoneOn(true);
    ////        audioManager.setWiredHeadsetOn(true);
    //
    //        audioManager.setStreamVolume(
    //                AudioManager.STREAM_VOICE_CALL,
    //                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
    //                AudioManager.STREAM_VOICE_CALL);
    //    }

    // get last call number, dial or incoming
    private String getLastCallNumber(Context ctx)
    {
        final ContentResolver cresolver = ctx.getContentResolver();
        if (null == cresolver)
        {
            return null;
        }
        final Cursor cursor = cresolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        //        Log.e("cursor", cursor.getCount() + "");
        if ((null != cursor) && cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        }

        return null;
    }

    // phone call listener to monitor phone state
    public class PhoneCallListener extends PhoneStateListener
    {
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            //记录当前电话状态，从而确定电话键单双击的动作
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲
                    //                    Log.d(TAG, "1111111111111111111");
                    mPhoneCallState = TelephonyManager.CALL_STATE_IDLE;
                    //                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    if (null != volumeBackup)
                    {
                        // why check this condition ?
                        // when the application start firstly, it call here
                        // check this condition to avoid useless invoke
                        setMuteModel(false);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机
                    //                    Log.d(TAG, "222222222222222222222222222");
                    mPhoneCallState = TelephonyManager.CALL_STATE_OFFHOOK;
                    //                    FoUtil.setMuteModel(true);
                    setMuteOn();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:// 响铃
                    //                    Log.d(TAG, "33333333333333333333333333");
                    mPhoneCallState = TelephonyManager.CALL_STATE_RINGING;
                    //                    FoUtil.setMuteModel(true);
                    setMuteOn();
                    break;
                default:
                    mPhoneCallState = TelephonyManager.CALL_STATE_IDLE;
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    // delay one second to set mute model on
    private void setMuteOn()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                setMuteModel(true);
            }
        }, 1 * 1000);
    }


    // set speaker model
    public void setMuteModel(boolean open)
    {
        Context mContext = IjiazuApp.getContext();
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (open)
        {
            if (false == isBleConnected)
            {
                return;
            }

            volumeBackup = new VoiceVolumeBackup();
            volumeBackup.backup(audioManager);

            //            int maxv = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            //            Log.e(Tag, "max  volume:" + maxv);
            //
            //            currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //            Log.e(TAG, "unmute music volume:" + currentMusicVolume);
            //
            //            currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            //            Log.e(Tag, "unmute voice volume:" + currentMusicVolume);

            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
        }
        else
        {
            if (null != volumeBackup)
            {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                volumeBackup.restore(audioManager);
            }
            volumeBackup = null;
        }
    }

    // class for backup/restore the various voice volumes
    private class VoiceVolumeBackup
    {
        public int alarm;
        public int dtmf;
        public int music;
        public int notification;
        public int ring;
        public int system;
        public int voicecall;

        // backup various volumes
        public void backup(AudioManager audioManager)
        {
            alarm = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            dtmf = audioManager.getStreamVolume(AudioManager.STREAM_DTMF);
            music = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            notification = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            ring = audioManager.getStreamVolume(AudioManager.STREAM_RING);
            system = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            voicecall = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        }

        // restore various volumes
        public void restore(AudioManager audioManager)
        {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarm, AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(AudioManager.STREAM_DTMF, dtmf, AudioManager.STREAM_DTMF);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, music, AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notification, AudioManager.STREAM_NOTIFICATION);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, ring, AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, system, AudioManager.STREAM_SYSTEM);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voicecall, AudioManager.STREAM_VOICE_CALL);
        }
    }
}
