package com.jinglingtec.ijiazu.accountmgr;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;

public class ForgotPasswordActivity extends BaseActivityAccoungMgr
{
    private TextView btnGetSMSConfirmCode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.setTitleText(R.string.passwordback);
        setHeaderLeftBtn();

        btnGetSMSConfirmCode = (TextView) this.findViewById(R.id.btnGetSMSConfirmCode);
        loadLocalMobileNum(R.id.mobileNumber);
    }

    // show password check box was clicked
    public void showPasswordClicked(View v)
    {
        EditText etPassOne = (EditText) this.findViewById(R.id.password);
        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);
        CheckBox cb = (CheckBox) this.findViewById(R.id.showPassword);

        if (cb.isChecked())
        {
            etPassOne.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPassTwo.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        else
        {
            etPassOne.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassTwo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
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
        btnNextStep.setEnabled(true);
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


    // SMS verification code successful, ready to do next step
    private void smsCodeVerifySuccess()
    {
        FoUtil.toast(this, R.string.smsVerifySuccess);

        EditText etMobileNumber = (EditText) this.findViewById(R.id.mobileNumber);
        EditText etPassOne = (EditText) this.findViewById(R.id.password);
        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);
        EditText etConfirmSMSCode = (EditText) this.findViewById(R.id.smsConfirmCode);
        TextView btnGetSMSConfirmCode = (TextView) this.findViewById(R.id.btnGetSMSConfirmCode);
        TextView btnComplete = (TextView) this.findViewById(R.id.btnComplete);
        TextView btnNextStep = (TextView) this.findViewById(R.id.btnNextStep);
        CheckBox cbShowPassword = (CheckBox) this.findViewById(R.id.showPassword);

        etMobileNumber.setVisibility(View.GONE);
        etConfirmSMSCode.setVisibility(View.GONE);
        btnGetSMSConfirmCode.setVisibility(View.GONE);
        btnNextStep.setVisibility(View.GONE);

        etPassOne.setVisibility(View.VISIBLE);
        etPassTwo.setVisibility(View.VISIBLE);
        btnComplete.setVisibility(View.VISIBLE);
        //        cbShowPassword.setVisibility(View.VISIBLE);
    }

    // complete button clicked
    public void completeBtnClicked(View v)
    {
        EditText etMobileNumber = (EditText) this.findViewById(R.id.mobileNumber);
        EditText etPassOne = (EditText) this.findViewById(R.id.password);
        EditText etPassTwo = (EditText) this.findViewById(R.id.confirmPass);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.Mobile = etMobileNumber.getText().toString();
        accountInfo.Password = etPassOne.getText().toString();
        String passTwo = etPassTwo.getText().toString();

        int resid = 0;
        if (accountInfo.Mobile.length() != FoConstants.MOBILELEN)
        {
            resid = R.string.invalideMobile;
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

        accountInfo.Password = FoUtil.getMD5(accountInfo.Password);
        //        createAccount(accountInfo);
    }

    //    private String TAG = "ForgotPasswordActivity";
}
