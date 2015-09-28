package com.jinglingtec.ijiazu.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity
{
    public static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, this.getClass().getName() + " onCreate started");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, this.getClass().getName() + " onStart started");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, this.getClass().getName() + " onResume started");
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.d(TAG, this.getClass().getName() + " onPause finished");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        MobclickAgent.onPause(this);
        Log.d(TAG, this.getClass().getName() + " onStop finished");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //取消广播
        //        unregisterReceiver(remindUser);
        Log.d(TAG, this.getClass().getName() + " onDestroy finished");
    }

    // set activity title text
    protected void setTitleText(int resID)
    {
        final TextView title = (TextView) this.findViewById(R.id.tv_header_title);
        if (null != title)
        {
            title.setText(resID);
        }
    }

    protected void setTitleTextListener(int resID, View.OnClickListener listener)
    {
        final TextView title = (TextView) this.findViewById(R.id.tv_header_title);
        if (null != title)
        {
            // title.setText(getResources().getString(resID));
            title.setText(resID);
            if (null != listener)
            {
                title.setOnClickListener(listener);
            }
        }

        ImageView pre_arrow = (ImageView) this.findViewById(R.id.iv_preference_arrow);
        if (null != pre_arrow)
        {
            pre_arrow.setBackgroundResource(R.drawable.drop_down_arrow);
        }
    }

    // set activity title image
    //    protected void setTitleImage(int resID)
    //    {
    //        final ImageView title = (ImageView) this.findViewById(R.id.iv_header_title);
    //        if (null != title)
    //        {
    //            title.setBackground(getResources().getDrawable(resID));
    //        }
    //    }

    // set general header, left button
    protected void setHeaderLeftBtn()
    {
        ImageView btn = (ImageView) this.findViewById(R.id.general_header_left_btn);
        if (null == btn)
        {
            return;
        }

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    // set general header, right button
    protected void setHeaderRightBtn(int resID, View.OnClickListener listener)
    {
        TextView btn = (TextView) this.findViewById(R.id.general_header_right_btn);
        if (null == btn)
        {
            return;
        }

        btn.setText(getResources().getString(resID));
        if (null != listener)
        {
            btn.setOnClickListener(listener);
        }
    }

    // set general header, right button
    protected void setHeaderRightBtnDrawable(int resID, View.OnClickListener listener)
    {
        ImageView btn = (ImageView) this.findViewById(R.id.general_header_right_btn_iv);
        if (null == btn)
        {
            return;
        }

        //        btn.setImageDrawable(getResources().getDrawable(resID));
        btn.setImageResource(resID);
        if (null != listener)
        {
            btn.setOnClickListener(listener);
        }
    }

}
