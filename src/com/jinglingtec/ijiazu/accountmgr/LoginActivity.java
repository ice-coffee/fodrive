package com.jinglingtec.ijiazu.accountmgr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.http.FoHttpRequest;
import com.jinglingtec.ijiazu.util.http.HttpAsyncResponse;
import com.jinglingtec.ijiazu.util.http.HttpConst;
import com.jinglingtec.ijiazu.util.http.HttpRequestComplete;

/**
 * http://api.jinglingtec.com/static/createaccount.html
 * 用户管理页
 */
public class LoginActivity extends BaseActivityAccoungMgr
{
    protected void onCreate(Bundle sis)
    {
        super.onCreate(sis);
        setContentView(R.layout.login_activity);
        this.setTitleText(R.string.i_cloud);
        //        setTitleImage(R.drawable.cloud_icon);
        setHeaderLeftBtn();

        this.loadLocalMobileNum(R.id.loginMobile);
    }

    // login button was clicked
    public void loginBtnClicked(View v)
    {
        if (this.inLogin)
        {
            return;
        }

        EditText etname = (EditText) this.findViewById(R.id.loginMobile);
        EditText etpass = (EditText) this.findViewById(R.id.password);
        String mobile = etname.getText().toString();
        String pass = etpass.getText().toString();

        if ((mobile == null) || (pass == null) || (mobile.length() != FoConstants.MOBILELEN) ||
                (pass.length() < FoConstants.PASSMINLEN) || (pass.length() > FoConstants.PASSMAXLEN))
        {
            FoUtil.toast(this, R.string.loginfailed);
            return;
        }

        this.inLogin = true;

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.Mobile = mobile;
        accountInfo.Password = FoUtil.getMD5(pass);
        login(accountInfo);
    }

    // create account
    private void login(AccountInfo accountInfo)
    {
        final ProgressDialog progDlg = ProgressDialog.show(this, "", getResources().getString(R.string.duringLogin));

        HttpRequestComplete requestComplete = new HttpRequestComplete()
        {
            @Override
            public void onComplete(HttpAsyncResponse response)
            {
                if (response.isSuccess())
                {
                    String responseValue = response.getValue();
                    loginFedback(responseValue);
                }
                progDlg.dismiss();
                inLogin = false;
            }
        };

        FoHttpRequest.doHttpPost(HttpConst.API_ACCOUNT_LOGIN, accountInfo, requestComplete);
    }

    // login feedback
    private void loginFedback(String result)
    {
        if (parseHttpFeedbackAccountMgr(result))
        {
            FoUtil.toast(this, R.string.loginSucc);
            Intent intent = new Intent(LoginActivity.this, AccountHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // login button was clicked
    public void regBtnClicked(View v)
    {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        //        startActivityForResult(intent, RET_CREATEACCOUNT);
        startActivity(intent);
        this.finish();
    }

    // forgot password button clicked
    public void forgotPasswordBtnClicked(View v)
    {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        this.finish();
    }

    //    // login with sina weibo
    //    public void sinaWeiboBtnClicked(View v)
    //    {
    //        Platform platform_SinaWeibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
    //        platform_SinaWeibo.setPlatformActionListener(this);
    //        if(platform_SinaWeibo.isValid()){
    //            String userId = platform_SinaWeibo.getDb().getUserId();
    //            String userName = platform_SinaWeibo.getDb().getUserName();
    ////            socialLogin(userName, userId);
    //            FoUtil.toast(LoginActivity.this,"SinaWeiboSuccess");
    //        }else{
    //            //弹出失败窗口
    //            platform_SinaWeibo.showUser(null);
    //        }
    //
    //    }
    //
    //    // login with WeChat
    //    public void wechatBtnClicked(View v)
    //    {
    //        Platform platformWechat = ShareSDK.getPlatform(this, Wechat.NAME);
    //        platformWechat.setPlatformActionListener(this);
    //        //判断授权是否成功
    //        if(platformWechat.isValid()){
    //            String userId = platformWechat.getDb().getUserId();
    //            String userName = platformWechat.getDb().getUserName();
    ////            socialLogin(userName, userId);
    //            FoUtil.toast(LoginActivity.this,"WechatSuccess");
    //        }else{
    //            //弹出失败窗口
    //            platformWechat.showUser(null);
    //        }
    //    }
    //
    //    // login with QQ
    //    public void qqBtnClicked(View v)
    //    {
    //        Platform platformQZone = ShareSDK.getPlatform(this, QZone.NAME);
    //        platformQZone.setPlatformActionListener(this);
    //        //判断授权是否成功
    //        if(platformQZone.isValid()){
    //            String userId = platformQZone.getDb().getUserId();
    //            String userName = platformQZone.getDb().getUserName();
    ////            socialLogin(userName, userId);
    //            FoUtil.toast(LoginActivity.this,"QZoneSuccess");
    //        }else{
    //            //弹出失败窗口
    //            platformQZone.showUser(null);
    //        }
    //    }


    // launched activity(create new account activity) finished and returned
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    //    {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        if (resultCode != RESULT_OK)
    //        {
    //            return;
    //        }
    //
    //        switch (requestCode)
    //        {
    //            case RET_CREATEACCOUNT:
    //                Intent intent = new Intent();
    ////            intent.putExtra(FoConstants.ERRCODE, FoConstants.ERR_SUCC);
    //                setResult(RESULT_OK, intent);
    //                this.finish();
    //                break;
    //            default:
    //                break;
    //        }
    //    }

    //    private final int RET_CREATEACCOUNT = 0x83749;

    // true indicates in login process
    private boolean inLogin = false;

    private String TAG = "LoginActivity";
}
