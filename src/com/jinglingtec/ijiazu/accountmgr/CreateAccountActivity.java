package com.jinglingtec.ijiazu.accountmgr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.http.FoHttpRequest;
import com.jinglingtec.ijiazu.util.http.HttpAsyncResponse;
import com.jinglingtec.ijiazu.util.http.HttpConst;
import com.jinglingtec.ijiazu.util.http.HttpRequestComplete;

// activity for create a new account
public class CreateAccountActivity extends BaseActivityAccoungMgr
{

    private TextView btnGetSMSConfirmCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        this.setTitleText(R.string.new_acc_registry);
        setHeaderLeftBtn();

        btnGetSMSConfirmCode = (TextView) this.findViewById(R.id.btnGetSMSConfirmCode);

        initialize();
    }

    //
    private void initialize()
    {
        // setup title
        this.setTitleText(R.string.registry);

        loadLocalMobileNum(R.id.mobileNumber);
        setHeaderLeftBtn();
    }

    // show password check box was clicked
    //    public void showPasswordClicked(View v)
    //    {
    //        EditText etPassOne = (EditText) this.findViewById(R.id.password);
    //        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);
    //        CheckBox cb = (CheckBox) this.findViewById(R.id.showPassword);
    //
    //        if (cb.isChecked())
    //        {
    //            etPassOne.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    //            etPassTwo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    //        }
    //        else
    //        {
    //            etPassOne.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    //            etPassTwo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    //        }
    //    }

    // create account button was clicked
    public void createBtnClicked(View v)
    {
        EditText etMobileNumber = (EditText) this.findViewById(R.id.mobileNumber);
        EditText etNickName = (EditText) this.findViewById(R.id.nickname);
        EditText etPassOne = (EditText) this.findViewById(R.id.password);
        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.Mobile = etMobileNumber.getText().toString();
        accountInfo.NickName = etNickName.getText().toString();
        accountInfo.Password = etPassOne.getText().toString();
        String passTwo = etPassTwo.getText().toString();

        int resid = 0;
        if (accountInfo.Mobile.length() != FoConstants.MOBILELEN)
        {
            resid = R.string.invalideMobile;
        }
        else if (accountInfo.NickName.length() < 2)
        {
            resid = R.string.invalideNickName;
        }
        else if ((accountInfo.Password.length() < FoConstants.PASSMINLEN) || (accountInfo.Password.length() > FoConstants.PASSMAXLEN))
        {
            resid = R.string.invalidePassword;
        }
        else if (!accountInfo.Password.equals(passTwo))
        {
            resid = R.string.passwordNotMatch;
        }
        if (resid > 0)
        {
            FoUtil.toast(this, resid);
            return;
        }

        //        FoUtil.toast(getApplicationContext(),"create");

        accountInfo.Password = FoUtil.getMD5(accountInfo.Password);
        createAccount(accountInfo);
    }

    // create account
    private void createAccount(AccountInfo accountInfo)
    {
        final ProgressDialog progDlg = ProgressDialog.show(this, "", getResources().getString(R.string.creatingAccount));

        HttpRequestComplete requestComplete = new HttpRequestComplete()
        {
            @Override
            public void onComplete(HttpAsyncResponse response)
            {
                if (response.isSuccess())
                {
                    String responseValue = response.getValue();
                    createAccountFedback(responseValue);
                }
                progDlg.dismiss();
            }
        };

        FoHttpRequest.doHttpPost(HttpConst.API_ACCOUNT_CREATE, accountInfo, requestComplete);
    }

    // create account feedback
    private void createAccountFedback(String result)
    {
        if (parseHttpFeedbackAccountMgr(result))
        {
            FoUtil.toast(this, R.string.registrySucc);
            Intent intent = new Intent(CreateAccountActivity.this, AccountHomeActivity.class);
            startActivity(intent);
            finish();
        }
        //        else
        //        {
        //            FoUtil.toast(this, R.string.loginfailed);
        //        }
    }

    // button clicked, get sms confirmation code
    public void onClickGetSMSConfirmCode(View v)
    {
        EditText etMobileNumber = (EditText) this.findViewById(R.id.mobileNumber);
        final String mobile = etMobileNumber.getText().toString();
        if ((null != mobile) && (mobile.length() != FoConstants.MOBILELEN))
        {
            FoUtil.toast(this, R.string.invalideMobile);
            return;
        }

        this.verifyMobileNum(mobile);

        getSMSCodeAfterWhile(R.id.btnGetSMSConfirmCode);

        btnGetSMSConfirmCode.setBackgroundResource(R.drawable.button_small_gray);
        TextView btnNextStep = (TextView) this.findViewById(R.id.btnNextStep);
        //        btnNextStep.setBackground(getResources().getDrawable(R.drawable.big_button));
        btnNextStep.setEnabled(true);
    }

    // verify SMS success
    @Override
    protected void onVerifySMSSuccess(String verifyCode)
    {
        smsCodeVerifySuccess();
    }


    // SMS verification code successful, ready to do next step
    private void smsCodeVerifySuccess()
    {
        FoUtil.toast(this, R.string.smsVerifySuccess);

        EditText etMobileNumber = (EditText) this.findViewById(R.id.mobileNumber);
        EditText etNickName = (EditText) this.findViewById(R.id.nickname);
        EditText etPassOne = (EditText) this.findViewById(R.id.password);
        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);
        EditText etConfirmSMSCode = (EditText) this.findViewById(R.id.smsConfirmCode);

        TextView btnCreate = (TextView) this.findViewById(R.id.btnCreate);
        TextView btnNextStep = (TextView) this.findViewById(R.id.btnNextStep);
        //        CheckBox cbShowPassword = (CheckBox) this.findViewById(R.id.showPassword);

        etMobileNumber.setVisibility(View.GONE);
        etConfirmSMSCode.setVisibility(View.GONE);
        btnGetSMSConfirmCode.setVisibility(View.GONE);
        btnNextStep.setVisibility(View.GONE);

        etNickName.setVisibility(View.VISIBLE);
        etPassOne.setVisibility(View.VISIBLE);
        etPassTwo.setVisibility(View.VISIBLE);
        btnCreate.setVisibility(View.VISIBLE);
        //        cbShowPassword.setVisibility(View.VISIBLE);
    }

    // next step button clicked
    public void onClickNextStep(View v)
    {
        EditText etConfirmSMSCode = (EditText) this.findViewById(R.id.smsConfirmCode);
        String code = etConfirmSMSCode.getText().toString();
        if (!verifySMSConfirmCode(code))
        {
            FoUtil.toast(this, R.string.invalidSMSVerifyCode);
            return;
        }

        smsCodeVerifySuccess();
    }


    //    private String TAG = "CreateAccountActivity";
}

