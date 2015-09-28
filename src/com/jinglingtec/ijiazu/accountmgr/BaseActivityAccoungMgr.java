package com.jinglingtec.ijiazu.accountmgr;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.data.Depot;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.SMS.FoSendSMS;
import com.jinglingtec.ijiazu.util.SMS.GenerateValidation;
import com.jinglingtec.ijiazu.util.ThreadUtil;

public class BaseActivityAccoungMgr extends BaseActivity
{
    // verify mobile number
    public void verifyMobileNum(final String mobile)
    {
        // build sms content
        smsVerifyCode = GenerateValidation.getValidation(SMS_CODE_LEN);
        smsVerifyInfo = smsVerifyCode;

        // fo test purpose
        ThreadUtil.execute(new Runnable()
        {
            @Override
            public void run()
            {
                FoSendSMS sms = new FoSendSMS(mobile, smsVerifyInfo);
                sms.sendSMS();
            }
        });

        if (null == localSMSObserver)
        {
            Uri uri = Uri.parse("content://sms");
            localSMSObserver = new LocalSMSObserver(new Handler());
            this.getContentResolver().registerContentObserver(uri, true, localSMSObserver);
        }
    }

    // local SMS observer to monitor local SMS
    private class LocalSMSObserver extends ContentObserver
    {
        public LocalSMSObserver(Handler handler)
        {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange)
        {
            super.onChange(selfChange);
            Uri outMMS = Uri.parse("content://sms");
            Cursor cursor = getContentResolver().query(outMMS, null, null, null, null);
            if (cursor != null)
            {
                if (cursor.moveToFirst())
                {
                    String smsbody = cursor.getString(cursor.getColumnIndex("body"));
                    //                    int length = body.length();
                    //                    String code = body.substring(length - 15, length - 11);
                    //                    et_validation_register.setText(code);
                    //                    Log.e("sssssssss",c.getString(2));
                    //                    Log.e("sssssssss",c.getString(c.getColumnIndex("body")));
                    //                    Log.e("sssssssss",c.getString(c.getColumnIndex("date")));

                    onReceivedNewSMS(smsbody);
                }
                cursor.close();
            }
        }
    }

    // on received new sms
    private void onReceivedNewSMS(String smsbody)
    {
        //        if ((null != smsVerifyInfo) && smsVerifyInfo.equals(smsbody))
        if ((null == this.smsVerifyCode) || (SMS_CODE_LEN != this.smsVerifyCode.length()) ||
                (null == smsbody) || (SMS_CODE_LEN > smsbody.length()))
        {
            return;
        }

        onVerifySMSSuccess(this.smsVerifyCode);
        thisActivityDestroyed = true;
        unRegisterSMSObserve();
    }

    // verify SMS success, this is override by derived class
    protected void onVerifySMSSuccess(String verifyCode)
    {
    }

    // un-register SMS observe
    private void unRegisterSMSObserve()
    {
        if (null != localSMSObserver)
        {
            this.getContentResolver().unregisterContentObserver(localSMSObserver);
            localSMSObserver = null;
        }
    }

    // verify SMS confirm code
    protected boolean verifySMSConfirmCode(String code)
    {
        return ((null != this.smsVerifyCode) && this.smsVerifyCode.equals(code));
    }


    // display text that can get SMS code after 120 seconds
    protected void getSMSCodeAfterWhile(final int btnResID)
    {
        canGetSMSCodeAgain = false;
        final int delayTime = 1000; // 1 second

        final TextView btnGetSMSConfirmCode = (TextView) findViewById(btnResID);
        btnGetSMSConfirmCode.setEnabled(false);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable()
        {
            private int totalTimes = 120;
            private Runnable self = this;
            final String textWait = getResources().getString(R.string.getSMSConfirmCodeAgain);

            @Override
            public void run()
            {
                if (thisActivityDestroyed)
                {
                    self = null; // release object
                    return;
                }
                totalTimes--;
                if (canGetSMSCodeAgain || (totalTimes < 1))
                {
                    btnGetSMSConfirmCode.setText(R.string.getSMSConfirmCode);
                    btnGetSMSConfirmCode.setBackgroundResource(R.drawable.button_small_gray);
                    btnGetSMSConfirmCode.setEnabled(true);
                    self = null; // release object
                }
                else
                {
                    String text = "(" + String.valueOf(totalTimes) + ")" + textWait;
                    btnGetSMSConfirmCode.setText(text);
                    handler.postDelayed(self, delayTime);
                }
            }
        };

        handler.postDelayed(runnable, delayTime);
    }

    // parse http response that manage account
    // 1, create account
    // 2, login
    protected boolean parseHttpFeedbackAccountMgr(String httpResponse)
    {
        if ((null == httpResponse) || (httpResponse.length() < 1))
        {
            return false;
        }
        Log.d(TAG, httpResponse);

        try
        {
            Gson gson = new Gson();
            HttpFeedbackManageAccount feedback = gson.fromJson(httpResponse, HttpFeedbackManageAccount.class);

            if (null == feedback)
            {
                return false;
            }
            if (!feedback.isSuccess())
            {
                if (null != feedback.Info)
                {
                    FoUtil.toast(this, feedback.Info);
                }
                return false;
            }
            if (null != feedback.Account)
            {
                Depot.account = feedback.Account;
                AccountManager.saveAccountInfo(feedback.Account);
                return true;
            }
        } catch (Exception ex)
        {
            //            Log.d(TAG, "login exception 1");
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onDestroy()
    {
        thisActivityDestroyed = true;
        unRegisterSMSObserve();
        super.onDestroy();
    }


    // load local mobile number to EditText
    protected void loadLocalMobileNum(int resID_editText)
    {
        String mobile = FoUtil.readLocalMobileNum(this);
        if ((null == mobile) || (FoConstants.MOBILELEN > mobile.length()))
        {
            return;
        }
        if (FoConstants.MOBILELEN < mobile.length())
        {
            mobile = mobile.substring(mobile.length() - FoConstants.MOBILELEN);
        }

        EditText et = (EditText) this.findViewById(resID_editText);
        if (null != et)
        {
            et.setText(mobile);
        }
    }

    // SMS verify code length
    private final int SMS_CODE_LEN = 6;
    // SMS content of verify
    private String smsVerifyInfo = null;
    private String smsVerifyCode = null;
    // local SMS observer to monitor system SMS message
    private LocalSMSObserver localSMSObserver = null;
    private boolean canGetSMSCodeAgain = false;
    private boolean thisActivityDestroyed = false;

    private String TAG = "BaseActivityAccoungMgr";
}
