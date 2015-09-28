//package com.jinglingtec.ijiazu.accountmgr;//package com.jinglingtec.fodrive.accountmgr;
//
//import android.database.ContentObserver;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import com.jinglingtec.ijiazu.R;
//import com.jinglingtec.ijiazu.activity.BaseActivity;
//import com.jinglingtec.ijiazu.util.ThreadUtil;
//
///**
//* Created by coffee on 14-11-13.
//*/
//public class RegisterActivity extends BaseActivity
//{
//
//    private EditText et_name_register;
//    private EditText et_password_register;
//    private EditText et_validation_register;
//    private Button bt_seconds_register;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fodrive_register);
//
//        et_name_register = (EditText) findViewById(R.id.et_name_register);
//        et_password_register = (EditText) findViewById(R.id.et_password_register);
//        et_validation_register = (EditText) findViewById(R.id.et_validation_register);
//        bt_seconds_register = (Button) findViewById(R.id.bt_seconds_register);
//
//        bt_seconds_register.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                ThreadUtil.execute(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        //                        FoSendSMS sms = new FoSendSMS(et_name_register.getText().toString());
//                        //                        sms.sendSMS();
//                    }
//                });
//            }
//        });
//
//        Uri uri = Uri.parse("content://sms");
//        getContentResolver().registerContentObserver(uri, true, new myObserver(new Handler()));
//
//        ShareSDK.initSDK(this);
//    }
//
//    private class myObserver extends ContentObserver
//    {
//        public myObserver(Handler handler)
//        {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange)
//        {
//            super.onChange(selfChange);
//            Uri outMMS = Uri.parse("content://sms");
//            Cursor cursor = getContentResolver().query(outMMS, null, null, null, null);
//            if (cursor != null)
//            {
//                cursor.moveToFirst();
//                String body = cursor.getString(cursor.getColumnIndex("body"));
//                int length = body.length();
//                String code = body.substring(length - 15, length - 11);
//                et_validation_register.setText(code);
//                /*Log.e("sssssssss",c.getString(2));
//                Log.e("sssssssss",c.getString(c.getColumnIndex("body")));
//                Log.e("sssssssss",c.getString(c.getColumnIndex("date")));*/
//                cursor.close();
//
//            }
//        }
//    }
//
//    public void Share(View view)
//    {
//        showShare();
//    }
//
//    private void showShare()
//    {
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getString(R.string.share));
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//        // 启动分享GUI
//        oks.show(this);
//    }
//
//}
