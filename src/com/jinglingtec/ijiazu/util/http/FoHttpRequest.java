package com.jinglingtec.ijiazu.util.http;

import com.google.gson.Gson;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.HttpStatus;

/**
 * Created by kevin on 14-10-24.
 */
public class FoHttpRequest
{
    public static void doHttpPost(final String api, final Object srcObj, HttpRequestComplete requestComplete)
    {
        if ((null == api) || (api.length() < 1))
        {
            return;
        }

        try
        {
            // create a count down latch to sync the HTTP request
            //            final CountDownLatch latch = new CountDownLatch(1);
            // AsyncResponse to hold the data of response of HTTP request
            final HttpAsyncResponse asyncResponse = new HttpAsyncResponse();

            // execute the HTTP request in a dedicate thread
            //            ThreadUtil.execute(new Runnable()
            //            {
            //                @Override
            //                public void run()
            //                {
            //                    Looper.prepare();
            // concat the fodrive domain and the HTTP API
            final String url = HttpConst.FODRIVE_API_DOMAIN + api;
            // build the request param
            final RequestParams params = new RequestParams();
            if ((null != srcObj))
            {
                final Gson gson = new Gson();
                final String requestBody = gson.toJson(srcObj);

                final HTTPSignData signData = new HTTPSignData();
                signData.Signature = FoUtil.getMD5(requestBody);
                final String requestSign = gson.toJson(signData);

                params.put(HttpConst.REQUEST_BODY, requestBody);
                params.put(HttpConst.REQUEST_SIGN, requestSign);
            }

            doPost(url, params, asyncResponse, requestComplete);
            //                    Looper.loop();
            //
            //                    // HTTP request finished
            //                    latch.countDown();
            //                }
            //            });

            //            latch.await(FoConstants.HTTP_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);

            //            if ((null != asyncResponse) && asyncResponse.isSuccess())
            //            {
            //                final String ret = asyncResponse.getValue();
            //                return ret;
            //            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //        return null;
    }

    // really to do http post
    private static void doPost(final String url, final RequestParams params, final HttpAsyncResponse response, final HttpRequestComplete requestComplete)
    {
        if ((null == url) || (null == params) || (null == response))
        {
            //            Looper.myLooper().quit();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] body)
            {
                if ((null != requestComplete) && (HttpStatus.SC_OK == statusCode) && (null != body) && (body.length > 0))
                {
                    // by default, the "responst" object is set to SUCCESS
                    String resBody = new String(body);
                    response.setValue(resBody);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
            {
                if (null != requestComplete)
                {
                    response.setFailed();
                }
            }

            @Override
            public void onFinish()
            {
                if (null != requestComplete)
                {
                    requestComplete.onComplete(response);
                }
            }

        });
    }

    // build http request sign
    //    private static String buildRequestSign()
    //    {
    //        String sign = null;
    //
    //        HTTPSignData signData = new HTTPSignData;
    //        return sign;
    //    }

    // get current version
    //    private void getCurVersion()
    //    {
    //        try
    //        {
    //            // UserInfo ui = GlobalData.userInfo;
    //            PackageManager pm = this.getPackageManager();
    //            PackageInfo pi = pm.getPackageInfo(this.getApplicationContext().getPackageName(), 0);
    //            curVersionName = pi.versionName;
    //            curVersionCode = pi.versionCode;
    //        }
    //        catch (Exception ex)
    //        {
    //
    //        }
    //    }


    // object to hold SIGN data
    private static class HTTPSignData
    {
        public int PlatformCode = FoConstants.PLTCODE_ANDROID_PHONE;
        public int VersionCode = -1;
        public String Signature = null;

        public HTTPSignData()
        {
            VersionCode = 100;
        }
    }


}

