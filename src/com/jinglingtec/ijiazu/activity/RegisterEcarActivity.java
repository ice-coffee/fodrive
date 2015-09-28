package com.jinglingtec.ijiazu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.ecar.csp.http.EcarRegisterThread;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.invokeApps.ecar.EcarAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;


public class RegisterEcarActivity extends BaseActivity
{
    //    // this indicate register ecar service successfully
    //    private final int REG_SUCCESS = 0x95;
    //    // this indicate register ecar service failed
    //    private final int REG_FAILED = 0x58;
    //
    //    // -----------------------------------------------
    //    public Handler handlerRegEcar = new Handler()
    //    {
    //        @Override
    //        public void handleMessage(Message msg)
    //        {
    //            super.handleMessage(msg);
    //            switch (msg.what)
    //            {
    //                case REG_SUCCESS:
    //                    FoPreference.putBoolean(FoConstants.ECAR, true);
    //                    FoUtil.toast(RegisterEcarActivity.this, R.string.register_success);
    //                    finish();
    //                    break;
    //
    //                case REG_FAILED:
    //                    FoUtil.toast(RegisterEcarActivity.this, R.string.write_local_phone);
    //                    break;
    //            }
    //        }
    //    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecar_register);

        init();
        loadLocalMobileNum();
    }

    // initialize this activity
    public void init()
    {
        setHeaderLeftBtn();
        setTitleText(R.string.ecar_service);

        boolean regEcar = FoPreference.getBoolean(FoConstants.ECAR, false);
        if (regEcar)
        {
            FoUtil.toast(this, R.string.have_registered);

            EditText localText = (EditText) findViewById(R.id.et_ecar_local_number);
            TextView reg_ecer = (TextView) findViewById(R.id.tv_register_ecar);

            localText.setVisibility(View.INVISIBLE);
            reg_ecer.setVisibility(View.INVISIBLE);
        }
        else
        {
            FoUtil.toast(this, R.string.please_register_ecar);

            ImageView ecar_logo = (ImageView) findViewById(R.id.iv_ecar_logo);
            ImageView call_ecar = (ImageView) findViewById(R.id.iv_fc_open);

            ecar_logo.setVisibility(View.INVISIBLE);
            call_ecar.setVisibility(View.INVISIBLE);
        }
    }

    public void registerEcar(View view)
    {
        if (!FoUtil.isNetworkConnected(this))
        {
            FoUtil.toast(this, R.string.no_internet);
            return;
        }

        EditText localText = (EditText) findViewById(R.id.et_ecar_local_number);
        String localNumber = localText.getText().toString();
        if ((null == localNumber) || (localNumber.length() < FoConstants.MOBILELEN))
        {
            FoUtil.toast(RegisterEcarActivity.this, R.string.write_local_phone);
        }

        if ((null != localNumber) && (localNumber.length() == FoConstants.MOBILELEN))
        {
            //register ecar
            EcarRegisterThread registerThread = new EcarRegisterThread(getApplicationContext(), localNumber, null, null)
            {
                @Override
                public void onHttpRegisterResponse(final String result)
                {
                    if ((null == result) || (result.length() < 1))
                    {
                        return;
                    }

                    final String regSuccess = "success";
                    if (regSuccess.equals(result))
                    {
                        //success
                        //                        handlerRegEcar.sendEmptyMessage(REG_SUCCESS);
                        FoPreference.putBoolean(FoConstants.ECAR, true);
                        RegisterEcarActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                FoUtil.toast(RegisterEcarActivity.this, R.string.register_success);
                                RegisterEcarActivity.this.finish();
                            }
                        });

                    }
                    else
                    {
                        //false
                        //                        handlerRegEcar.sendEmptyMessage(REG_FAILED);
                        RegisterEcarActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                FoUtil.toast(RegisterEcarActivity.this, result);
                            }
                        });
                    }
                }
            };
            registerThread.start();
        }
        else
        {
            FoUtil.toast(this, R.string.write_local_phone);
        }

    }

    // load local mobile number to EditText
    protected void loadLocalMobileNum()
    {
        String mobile = FoUtil.readLocalMobileNum(this);
        if ((null == mobile) || (FoConstants.MOBILELEN > mobile.length()))
        {
            return;
        }
        if (FoConstants.MOBILELEN < mobile.length())
        {
            mobile = mobile.substring(mobile.length() - FoConstants.MOBILELEN);
        }

        EditText localText = (EditText) findViewById(R.id.et_ecar_local_number);
        if (null != localText)
        {
            localText.setText(mobile);
        }
    }

    public void callEcar(View view)
    {
        EcarAdapter adapter = new EcarAdapter();
        adapter.dialEcar(getApplicationContext());
    }

}
