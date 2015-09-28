package com.jinglingtec.ijiazu.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.accountmgr.FeedBackInfo;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.FoVersionCode;
import com.jinglingtec.ijiazu.util.http.FoHttpRequest;
import com.jinglingtec.ijiazu.util.http.HttpAsyncResponse;
import com.jinglingtec.ijiazu.util.http.HttpConst;
import com.jinglingtec.ijiazu.util.http.HttpRequestComplete;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户反馈
 */
public class FeedBackActivity extends BaseActivity
{
    //提交人信息
    private EditText et_exmail;
    //提交反馈内容
    private EditText et_content;

    private String TAG = "feedbackuser";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_user_feedback);

        init();
    }

    public void init()
    {
        setTitleText(R.string.feed_back);
        setHeaderLeftBtn();

        et_exmail = (EditText) findViewById(R.id.et_fb_exmail);
        et_content = (EditText) findViewById(R.id.et_fb_content);

    }

    // 网络反馈操作
    private void feedBackContent(FeedBackInfo feedBackInfo)
    {
        final ProgressDialog progDlg = ProgressDialog.show(this, "", getResources().getString(R.string.feedding_back));

        HttpRequestComplete requestComplete = new HttpRequestComplete()
        {
            @Override
            public void onComplete(HttpAsyncResponse response)
            {
                if (response.isSuccess())
                {
                    Log.e(TAG, "SUCCESS");
                    FoUtil.toast(getApplicationContext(), R.string.feed_back_success);
                }
                else
                {
                    Log.e(TAG, "FALSE");
                    FoUtil.toast(getApplicationContext(), R.string.feed_back_false);
                }

                progDlg.dismiss();
                finish();
            }
        };

        FoHttpRequest.doHttpPost(HttpConst.API_USER_FEEDBACK, feedBackInfo, requestComplete);
    }

    /**
     * 提交反馈信息
     *
     * @param view
     */
    public void submitContent(View view)
    {
        if (isEmail() || isNumeric())
        {
            String content = et_content.getText().toString();
            if (null == content)
            {
                FoUtil.toast(getApplicationContext(), R.string.null_content);
                return;
            }

            if (content.length() < 5)
            {
                FoUtil.toast(getApplicationContext(), R.string.content_less);
                return;
            }

            //版本信息
            int versionCode = FoVersionCode.getIjiazuVersionCode(this);
            String versionName = FoVersionCode.getIjiazuVersionName(this);

            FeedBackInfo feedBackInfo = new FeedBackInfo();
            feedBackInfo.Contact = et_exmail.getText().toString();
            feedBackInfo.Content = content;
            feedBackInfo.VersionCode = versionCode;
            feedBackInfo.VersionName = "v" + versionName;
            feedBackInfo.PlatformCode = 25;


            feedBackContent(feedBackInfo);
        }
        else
        {
            FoUtil.toast(getApplicationContext(), R.string.style_false);
        }

    }

    /**
     * 邮箱格式验证
     *
     * @return
     */
    public boolean isEmail()
    {
        String email = et_exmail.getText().toString();
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 电话格式验证
     *
     * @return
     */
    public boolean isNumeric()
    {
        String num = et_exmail.getText().toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(num);
        if (isNum.matches())
        {
            if (num.length() == FoConstants.MOBILELEN)
            {
                return true;
            }
        }
        return false;
    }

}
