package com.jinglingtec.ijiazu.activity;

import android.os.Bundle;
import com.jinglingtec.ijiazu.R;

/**
 * Created by coffee on 14-12-16.
 */
public class AgreementActivity extends BaseActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_service_argeement);

        init();
    }

    public void init()
    {
        setTitleText(R.string.about);
        setHeaderLeftBtn();


    }
}
