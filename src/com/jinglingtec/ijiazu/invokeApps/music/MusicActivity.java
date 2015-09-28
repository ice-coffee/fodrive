package com.jinglingtec.ijiazu.invokeApps.music;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import cn.kuwo.autosdk.api.KWAPI;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.fm.FCListActivity;
import com.jinglingtec.ijiazu.invokeApps.fm.FMActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoMusicOrFM;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.util.List;

public class MusicActivity extends BaseActivity implements View.OnClickListener
{


    // Kuwo API instance
    private KWAPI mKwapi = null;

    private ImageView logo;
    //    private TextView have;
    //    private TextView notHave;

    private ImageView mv_open;

    private ImageView mv_first;
    private ImageView mv_second;
    private ImageView mv_third;

    private List<Boolean> haveList = null;

    //dialog show ?
    private boolean dialogShow = false;

    //show
    private int showWhat = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_fc);

        logo = (ImageView) findViewById(R.id.iv_fc_logo);
        //        have = (TextView) findViewById(R.id.tv_fc_have);
        //        notHave = (TextView) findViewById(R.id.tv_fc_not);

        mv_open = (ImageView) findViewById(R.id.iv_fc_open);

        mv_first = (ImageView) findViewById(R.id.iv_fc_first);
        mv_second = (ImageView) findViewById(R.id.iv_fc_second);
        mv_third = (ImageView) findViewById(R.id.iv_fc_third);

        //        setTitleImage(R.drawable.music_title);
        //返回键
        setHeaderLeftBtn();
        setTitleText(R.string.music);
        //        setHeaderRightBtnDrawable(R.drawable.edit_contact_icon, listenerEditFC);

        mv_first.setBackgroundResource(R.drawable.kuwo_small);
        mv_second.setBackgroundResource(R.drawable.baidu_small);
        mv_third.setBackgroundResource(R.drawable.duomi_small);

        logo.setOnClickListener(this);
        mv_open.setOnClickListener(this);
        mv_first.setOnClickListener(this);
        mv_second.setOnClickListener(this);
        mv_third.setOnClickListener(this);

    }

    View.OnClickListener listenerEditFC = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(MusicActivity.this, FCListActivity.class);
            intent.putExtra(FoConstants.FC, FoConstants.MUSIC);
            startActivityForResult(intent, 0);
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_fc_open:
                runningMusic();

                break;
            case R.id.iv_fc_first:

                init(0);
                break;
            case R.id.iv_fc_second:

                init(1);
                break;
            case R.id.iv_fc_third:

                init(2);
                break;
            case R.id.iv_fc_logo:
                touchDownLoad();

                break;
        }
    }

    public void updataView()
    {
        //音乐app安装检测
        FoMusicOrFM mf = new FoMusicOrFM(getApplicationContext(), FoConstants.MUSIC);

        haveList = mf.getHaveAppList();
    }

    //更新控件
    public void init(int showId)
    {
        /**
         * true    存在
         * false   不存在
         */

        if (null == haveList)
        {
            return;
        }

        showWhat = showId;

        //初始化酷我音乐
        initKuWo(getApplicationContext());

        //修改透明度
        setImageAlpha(showWhat);

        for (int i = 0; i < haveList.size(); i++)
        {
            if (haveList.get(i))
            {
                switch (i)
                {
                    case 0:
                        mv_first.setBackgroundResource(R.drawable.kuwo_small);
                        break;

                    case 1:
                        mv_second.setBackgroundResource(R.drawable.baidu_small);
                        break;

                    case 2:
                        mv_third.setBackgroundResource(R.drawable.duomi_small);
                        break;

                    default:
                        break;
                }
            }
            else
            {
                switch (i)
                {
                    case 0:
                        mv_first.setBackgroundResource(R.drawable.kuwo_small_no);
                        break;

                    case 1:
                        mv_second.setBackgroundResource(R.drawable.baidu_small_no);
                        break;

                    case 2:
                        mv_third.setBackgroundResource(R.drawable.duomi_small_no);
                        break;

                    default:
                        break;
                }
            }
        }

        switch (showWhat)
        {
            case 0:
                if (haveList.get(showWhat))
                {
                    logo.setBackgroundResource(R.drawable.kuwo_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.kuwo_big_no);
                }
                break;

            case 1:
                if (haveList.get(showWhat))
                {
                    logo.setBackgroundResource(R.drawable.baidu_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.baidu_big_no);
                }
                break;

            case 2:
                if (haveList.get(showWhat))
                {
                    logo.setBackgroundResource(R.drawable.duomi_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.duomi_big_no);
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (null != data)
        {
            //            FoUtil.toast(getApplicationContext(), data.getStringExtra(FoConstants.FC));
        }

    }

    //控制透明度
    public void setImageAlpha(int show)
    {
        AlphaAnimation alphaDown = new AlphaAnimation(1F, 0.5F);
        alphaDown.setDuration(0); // Make animation instant
        alphaDown.setFillAfter(true); // Tell it to persist after the animation

        AlphaAnimation alphaUp = new AlphaAnimation(0.5F, 1F);
        alphaUp.setDuration(0); // Make animation instant
        alphaUp.setFillAfter(true); // Tell it to persist after the animation
        switch (show)
        {
            case 0:
                mv_first.startAnimation(alphaUp);
                mv_second.startAnimation(alphaDown);
                mv_third.startAnimation(alphaDown);
                break;
            case 1:
                mv_first.startAnimation(alphaDown);
                mv_second.startAnimation(alphaUp);
                mv_third.startAnimation(alphaDown);
                break;
            case 2:
                mv_first.startAnimation(alphaDown);
                mv_second.startAnimation(alphaDown);
                mv_third.startAnimation(alphaUp);
                break;
        }
    }

    public void runningMusic()
    {

        if (null == haveList)
        {
            return;
        }

        if (showWhat < 0 || showWhat > 2)
        {
            return;
        }

        if (haveList.get(showWhat))
        {
            switch (showWhat)
            {
                case 0:

                    if (null == mKwapi)
                    {
                        return;
                    }

                    mKwapi.startAPP(getApplicationContext(), true);
                    break;
                case 1:

                    break;
                case 2:

                    break;
                default:
                    break;
            }
        }
        else
        {

            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.please_download_music));

        }

    }

    //触摸地图下载音乐app
    public void touchDownLoad()
    {
        if (null != haveList)
        {

            if (!haveList.get(showWhat))
            {
                switch (showWhat)
                {
                    case 0:
                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(FoConstants.MUSIC_DOWN_LODE));
                        startActivity(it);
                        break;
                    case 1:
                        FoUtil.toast(getApplicationContext(), R.string.not_baidu);
                        break;
                    case 2:
                        FoUtil.toast(getApplicationContext(), R.string.not_duomi);
                        break;
                }

            }
            else
            {
                FoUtil.toast(getApplicationContext(), R.string.have_down);
            }
        }
    }

    public void initKuWo(Context ctx)
    {
        if (null == mKwapi)
        {
            // Kuwo API Key
            final String KUWO_API_KEY = "auto";
            mKwapi = KWAPI.createKWAPI(ctx, KUWO_API_KEY);
        }
    }


    @Override
    protected void onResume()
    {
        updataView();
        super.onResume();

        //获取保存的选择记录
        String selected = FoPreference.getString(FoConstants.MUSIC_SELECT);

        if (null != selected)
        {

            switch (Integer.valueOf(selected))
            {
                case 0:
                    showWhat = 0;

                    break;
                case 1:
                    showWhat = 1;
                    break;
                case 2:
                    showWhat = 2;
                    break;
            }
        }
        else
        {
            showWhat = 0;
        }

        init(showWhat);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //保存用户使用记录
        FoPreference.putString(FoConstants.MUSIC_SELECT, showWhat + "");
    }
}