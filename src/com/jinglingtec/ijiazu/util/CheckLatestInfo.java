package com.jinglingtec.ijiazu.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.http.*;

// thread util to manage the unified thread pool
public class CheckLatestInfo
{
    private final String TAG = "CheckLatestInfo";

    private final int CHECK_LATEST_INFO = 0x1035;

    private Context myCtx = null;

    public static void checkLatest(Context ctx)
    {
        CheckLatestInfo checkLatestInfo = new CheckLatestInfo();
        checkLatestInfo.check(ctx);
    }

    // delay checking
    private void check(Context ctx)
    {
        this.myCtx = ctx;

        // this is used to receive the message if the new version is available from the server
        final Handler handlerCheckLatestInfo = new Handler()
        {
            public void handleMessage(Message msg)
            {
                if ((CHECK_LATEST_INFO != msg.what) || (null == msg.obj))
                {
                    return;
                }

                LatestInformation latestInfo = (LatestInformation) msg.obj;
                if ((null != latestInfo) && (null != latestInfo.LatestAppVersion))
                {
                    showNewVersionDlg(latestInfo.LatestAppVersion.DownloadUrl);
                }
            }
        };

        // delay to check update
        handlerCheckLatestInfo.postDelayed(new Runnable()
        {
            public void run()
            {
                // at here, the code run in the main thread
                // so create another new runnable to execute others
                //                ThreadUtil.execute(new Runnable()
                //                {
                //                    public void run()
                //                    {
                //                        Looper.prepare();
                HttpRequestComplete requestComplete = new HttpRequestComplete()
                {
                    @Override
                    public void onComplete(HttpAsyncResponse response)
                    {
                        onCompleteHttp(response, handlerCheckLatestInfo);
                        //                                Looper.myLooper().quit();
                    }
                };

                // build the request data
                RequestOfCheckLatestInfo request = new RequestOfCheckLatestInfo();
                request.PlatformCode = FoConstants.PLTCODE_ANDROID_PHONE;
                request.VersionCode = FoUtil.getCurVersionCode(myCtx);
                // initiate the http request
                FoHttpRequest.doHttpPost(HttpConst.API_CHECK_LATEST_INFO, request, requestComplete);

                //                        Looper.loop();
                //                    }
                //                });
            }
        }, 10 * 1000);
        // delay 10 seconds to do check latest information
    }

    // check latest info, complete
    private void onCompleteHttp(HttpAsyncResponse response, Handler handlerCheckLatestInfo)
    {
        if (!response.isSuccess())
        {
            return;
        }

        try
        {
            // get the http return body
            String retBody = response.getValue();
            Log.d(TAG, retBody);

            // converty the JSON string to CheckLatestFeedback object
            Gson gson = new Gson();
            CheckLatestFeedback feedback = gson.fromJson(retBody, CheckLatestFeedback.class);

            if ((null != feedback) && (null != feedback.LatestInfo))
            {
                if ((null != feedback.LatestInfo.LatestAppVersion) && (feedback.LatestInfo.LatestAppVersion.VersionCode <= FoUtil.getCurVersionCode(this.myCtx)))
                {
                    feedback.LatestInfo.LatestAppVersion = null;
                }

                // send message to UI thread
                Message msg = handlerCheckLatestInfo.obtainMessage();
                msg.what = CHECK_LATEST_INFO;
                msg.obj = feedback.LatestInfo;
                msg.sendToTarget();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //    // check the current installed package version code
    //    private int getCurVersionCode(Context ctx)
    //    {
    //        try
    //        {
    //            // UserInfo ui = GlobalData.userInfo;
    //            PackageManager pm = ctx.getPackageManager();
    //            PackageInfo pi = pm.getPackageInfo(ctx.getApplicationContext().getPackageName(), 0);
    //            return pi.versionCode;
    //        }
    //        catch (Exception ex)
    //        {
    //            ex.printStackTrace();
    //        }
    //        return 0xEFFFFFFF;
    //    }

    // show new version dialog
    private void showNewVersionDlg(final String new_version_url)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myCtx, AlertDialog.THEME_HOLO_DARK);
        builder.setIconAttribute(android.R.attr.dialogIcon);

        final String from = myCtx.getResources().getString(R.string.app_name);
        final String message = myCtx.getResources().getString(R.string.new_version_ticker_text);
        builder.setMessage(message);
        builder.setTitle(from);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(new_version_url));
                myCtx.startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    // here is the data that will post to server to check the latest information
    private class RequestOfCheckLatestInfo extends BaseHttpFeedback
    {
        public int PlatformCode;
        public int VersionCode;
    }


    // here is the data struct that is the feedback OF check latest information
    private class CheckLatestFeedback extends BaseHttpFeedback
    {
        LatestInformation LatestInfo;
    }

    // latest information
    private class LatestInformation extends BaseHttpFeedback
    {
        MobileAppVersion LatestAppVersion;
    }

    // mobile app verion info
    private class MobileAppVersion
    {
        public int SysCategory;
        public int PlatformCode;
        public int VersionCode;
        public int MustUpdate;

        public String VersionName;
        public String DownloadUrl;
        public String Description;
        public String Size;
        public String MD5;
    }
}

