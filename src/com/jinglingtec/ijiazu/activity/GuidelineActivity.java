package com.jinglingtec.ijiazu.activity;

import android.os.Bundle;
import android.view.View;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;

// guideline activity
// it was launched at the first time when this app is first launched
public class GuidelineActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guideline);
    }

    // response the button
    public void startUseApp(View v)
    {
        this.finish();
        FoPreference.putBoolean(FoConstants.IS_FIRST_RUN, false);
    }
}

