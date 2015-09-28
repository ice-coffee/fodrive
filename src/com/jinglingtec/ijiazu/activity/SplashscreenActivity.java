package com.jinglingtec.ijiazu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.jinglingtec.ijiazu.R;

// splash screen for each time when app is launched
public class SplashscreenActivity extends BaseActivity
{
    private ImageView rolling;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        init();
        delayClose();
    }

    public void init()
    {
        rolling = (ImageView) findViewById(R.id.iv_splash_roll);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading);
        rolling.startAnimation(animation);
    }

    // delay close
    private void delayClose()
    {
        final Handler handlerCheckLatestInfo = new Handler();

        // delay to check update
        handlerCheckLatestInfo.postDelayed(new Runnable()
        {
            public void run()
            {

                SplashscreenActivity.this.finish();
            }
        }, 1 * 1000);
        // delay 5 seconds to do check latest information
    }
}
