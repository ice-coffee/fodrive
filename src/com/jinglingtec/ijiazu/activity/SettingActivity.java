package com.jinglingtec.ijiazu.activity;

import android.accounts.Account;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.accountmgr.AccountHomeActivity;
import com.jinglingtec.ijiazu.accountmgr.AccountInfo;
import com.jinglingtec.ijiazu.accountmgr.AccountManager;
import com.jinglingtec.ijiazu.accountmgr.LoginActivity;
import com.jinglingtec.ijiazu.data.Depot;
import com.jinglingtec.ijiazu.ui.SwitchButton;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.FoVersionCode;

/**
 * Created by coffee on 14-11-20.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener
{
    /**
     * 云登录
     */
    private LinearLayout cloudLogin;

    /**
     * 没有wifi下下载
     */
    private RelativeLayout noWifi;

    /**
     * 蓝颜
     */
    private RelativeLayout blueTooth;
    private SwitchButton ble_control;
    private BluetoothAdapter blueAdapter;

    /**
     * wifi
     */
    private SwitchButton wifi_control;

    /**
     * 反馈
     */
    private LinearLayout feedBack;

    /**
     * 关于
     */
    private LinearLayout fodriveAbout;

    /**
     * 注册翼卡
     */
    private LinearLayout registerEcar;

    /**
     * 监听
     */
    private BroadCastBlue broadCastBlue;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_setting);

        init();

    }

    public void init()
    {

        setTitleText(R.string.setting);
        setHeaderLeftBtn();

        cloudLogin = (LinearLayout) findViewById(R.id.scv_setting_cloud_login);
        noWifi = (RelativeLayout) findViewById(R.id.rl_setting_no_wifi);
        blueTooth = (RelativeLayout) findViewById(R.id.rl_setting_blue_tooth);
        feedBack = (LinearLayout) findViewById(R.id.ll_setting_feed_back);
        fodriveAbout = (LinearLayout) findViewById(R.id.ll_setting_fodrive_about);
        registerEcar = (LinearLayout) findViewById(R.id.ll_setting_ecar_register);

        ble_control = (SwitchButton) findViewById(R.id.sb_set_ble);
        wifi_control = (SwitchButton) findViewById(R.id.sb_set_wifi);

        //获取蓝牙控制
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (FoUtil.isBluetoothEnabled(this))
        {
            ble_control.changeState(true);
        }
        else
        {
            ble_control.changeState(false);
        }

        boolean bool = FoPreference.getBoolean(getResources().getString(R.string.down_file_nowifi), false);
        if (bool)
        {
            wifi_control.changeState(true);
        }
        else
        {
            wifi_control.changeState(false);
        }

        showIjiazuVersion();

        //设置点击事件
        cloudLogin.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        fodriveAbout.setOnClickListener(this);
        ble_control.setOnClickListener(this);
        wifi_control.setOnClickListener(this);
        registerEcar.setOnClickListener(this);

        broadCastBlue = new BroadCastBlue();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadCastBlue, filter);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.scv_setting_cloud_login:
                AccountInfo accountInfo = AccountManager.loadAccountInfo();

                if (null != accountInfo)
                {
                    intent = new Intent(SettingActivity.this, AccountHomeActivity.class);
                }
                else
                {
                    intent = new Intent(SettingActivity.this, LoginActivity.class);
                }

                break;
            case R.id.ll_setting_feed_back:
                intent = new Intent(SettingActivity.this, FeedBackActivity.class);

                break;
            case R.id.ll_setting_fodrive_about:
                intent = new Intent(SettingActivity.this, AboutActivity.class);

                break;
            case R.id.ll_setting_ecar_register:
                intent = new Intent(SettingActivity.this, RegisterEcarActivity.class);

                break;
            case R.id.sb_set_ble:
                bleControl();
                break;
            case R.id.sb_set_wifi:
                wifiControl();
                break;
        }

        if (null != intent)
        {
            startActivity(intent);
        }

    }

    private class BroadCastBlue extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int blueState = blueAdapter.getState();
                switch (blueState)
                {
                    case BluetoothAdapter.STATE_OFF:
                        ble_control.changeState(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        ble_control.changeState(true);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //控制蓝牙的开闭
    private void bleControl()
    {
        if (null == blueAdapter)
        {
            return;
        }


        if (blueAdapter.isEnabled())
        {

            blueAdapter.disable();
            ble_control.changeState(false);

        }
        else
        {

            blueAdapter.enable();
            ble_control.changeState(true);
        }
    }

    private void wifiControl()
    {
        boolean bool = FoPreference.getBoolean(getResources().getString(R.string.down_file_nowifi), false);
        if (bool)
        {
            wifi_control.changeState(false);
            FoPreference.putBoolean(getResources().getString(R.string.down_file_nowifi), false);
        }
        else
        {
            wifi_control.changeState(true);
            FoPreference.putBoolean(getResources().getString(R.string.down_file_nowifi), true);
            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.open_wifi_down));
        }
    }

    public void showIjiazuVersion()
    {
        TextView version = (TextView) findViewById(R.id.tv_setting_ijiazu_version);
        String versionCode = FoVersionCode.getIjiazuVersionName(this);
        if (null != versionCode)
        {
            version.setText(versionCode);
        }
    }

    @Override
    protected void onResume()
    {
        if (blueAdapter.isEnabled())
        {

            ble_control.changeState(true);

        }
        else
        {

            ble_control.changeState(false);

        }
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(broadCastBlue);
        super.onDestroy();
    }
}
