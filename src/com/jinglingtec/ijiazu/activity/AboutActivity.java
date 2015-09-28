package com.jinglingtec.ijiazu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoVersionCode;

/**
 * Created by coffee on 14-11-6.
 */
public class AboutActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_about);

        init();
    }

    public void init()
    {
        setTitleText(R.string.about);
        setHeaderLeftBtn();

        showIjiazuVersion();
    }

    //联系我们
    public void callOus(View view)
    {

    }

    //版权信息
    public void copyRightInformation(View view)
    {

    }


    public void sendEmail(View view)
    {
        Uri uri = Uri.parse("mailto:service@jinglingtec.com");
        //        String[] email = {"service@jinglingtec.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        //        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人 intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分");
        // 主题
        //        intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.i_account_feedback));
        // 正文
        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

    }

    //服务条款
    public void serviceAgreement(View view)
    {
        Intent intent = new Intent(AboutActivity.this, AgreementActivity.class);
        startActivity(intent);
    }

    //精灵网络地址
    public void jlUrl(View view)
    {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ijiazu.com"));
        startActivity(it);
    }

    public void showIjiazuVersion()
    {
        TextView version = (TextView) findViewById(R.id.tv_about_service_code);
        String versionCode = FoVersionCode.getIjiazuVersionName(this);
        if (null != versionCode)
        {
            version.setText(getResources().getString(R.string.local_version_code) + versionCode);
        }
    }

}
