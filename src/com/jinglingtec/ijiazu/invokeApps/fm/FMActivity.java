package com.jinglingtec.ijiazu.invokeApps.fm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoMusicOrFM;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.util.List;

public class FMActivity extends BaseActivity implements View.OnClickListener
{

    private ImageView logo;
    //    private TextView have;
    //    private TextView notHave;

    private ImageView fm_open;

    private ImageView fm_first;
    private ImageView fm_second;
    private ImageView fm_third;

    private List<Boolean> haveList = null;

    private String[] upAppList = {FoConstants.FM_DOWN_LODE};

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

        fm_open = (ImageView) findViewById(R.id.iv_fc_open);

        fm_first = (ImageView) findViewById(R.id.iv_fc_first);
        fm_second = (ImageView) findViewById(R.id.iv_fc_second);
        fm_third = (ImageView) findViewById(R.id.iv_fc_third);

        //        setTitleImage(R.drawable.fm_title);
        //返回键
        setHeaderLeftBtn();
        setTitleText(R.string.fm);
        //        setHeaderRightBtnDrawable(R.drawable.edit_contact_icon, listenerEditFC);


        fm_first.setBackgroundResource(R.drawable.qingting_small);
        fm_second.setBackgroundResource(R.drawable.koudai_small);
        fm_third.setBackgroundResource(R.drawable.kaola_small);

        logo.setOnClickListener(this);
        fm_open.setOnClickListener(this);
        fm_first.setOnClickListener(this);
        fm_second.setOnClickListener(this);
        fm_third.setOnClickListener(this);

    }

    View.OnClickListener listenerEditFC = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(FMActivity.this, FCListActivity.class);
            intent.putExtra(FoConstants.FC, FoConstants.FM);
            startActivityForResult(intent, 0);
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_fc_open:
                runningFM();
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
        //电台app安装检测
        FoMusicOrFM mf = new FoMusicOrFM(getApplicationContext(), FoConstants.FM);

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

        //修改透明度
        setImageAlpha(showWhat);

        for (int i = 0; i < haveList.size(); i++)
        {
            if (haveList.get(i))
            {
                switch (i)
                {
                    case 0:
                        fm_first.setBackgroundResource(R.drawable.qingting_small);
                        break;

                    case 1:
                        fm_second.setBackgroundResource(R.drawable.koudai_small);
                        break;

                    case 2:
                        fm_third.setBackgroundResource(R.drawable.kaola_small);
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
                        fm_first.setBackgroundResource(R.drawable.qingting_small_on);
                        break;

                    case 1:
                        fm_second.setBackgroundResource(R.drawable.koudai_small_on);
                        break;

                    case 2:
                        fm_third.setBackgroundResource(R.drawable.kaola_small_on);
                        break;

                    default:
                        break;
                }
            }
        }

        switch (showWhat)
        {
            case 0:
                if (haveList.get(0))
                {
                    logo.setBackgroundResource(R.drawable.qingting_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.qingting_big_on);
                }
                break;

            case 1:
                if (haveList.get(1))
                {
                    logo.setBackgroundResource(R.drawable.koudai_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.koudai_big_on);
                }
                break;

            case 2:
                if (haveList.get(2))
                {
                    logo.setBackgroundResource(R.drawable.kaola_big);
                }
                else
                {
                    logo.setBackgroundResource(R.drawable.kaola_big_on);
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

        if (null == data)
        {
            return;
        }

        FoUtil.toast(getApplicationContext(), data.getStringExtra(FoConstants.FC));
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
                fm_first.startAnimation(alphaUp);
                fm_second.startAnimation(alphaDown);
                fm_third.startAnimation(alphaDown);
                break;
            case 1:
                fm_first.startAnimation(alphaDown);
                fm_second.startAnimation(alphaUp);
                fm_third.startAnimation(alphaDown);
                break;
            case 2:
                fm_first.startAnimation(alphaDown);
                fm_second.startAnimation(alphaDown);
                fm_third.startAnimation(alphaUp);
                break;
        }
    }

    //运行电台
    public void runningFM()
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

                    launchQingting(getApplicationContext());
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

            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.please_download_fm));

        }
    }
    //点击大图下载电台app

    public void touchDownLoad()
    {
        if (null != haveList)
        {

            if (!haveList.get(showWhat))
            {
                switch (showWhat)
                {
                    case 0:
                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(FoConstants.FM_DOWN_LODE));
                        startActivity(it);
                        break;
                    case 1:
                        FoUtil.toast(getApplicationContext(), R.string.not_koudai);
                        break;
                    case 2:
                        FoUtil.toast(getApplicationContext(), R.string.not_kaola);
                        break;
                }

            }
            else
            {
                FoUtil.toast(getApplicationContext(), R.string.have_down);
            }

        }
    }

    //提醒用户下载DIALOG
    //    public void downLoadFM()
    //    {
    //        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
    //        builder.setIconAttribute(android.R.attr.dialogIcon);
    //
    //        String from = getResources().getString(R.string.app_name);
    //        String message = getResources().getString(R.string.i_please_download_fm);
    //        builder.setMessage(message);
    //        builder.setTitle(from);
    //
    //        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which)
    //            {
    //                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://qingting.fm/app/download_fujia"));
    //                startActivity(it);
    //            }
    //        });
    //
    //        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which)
    //            {
    //                dialog.dismiss();
    //            }
    //        });
    //
    //        builder.create().show();
    //
    //        dialogShow = true;
    //    }

    private void launchQingting(Context ctx)
    {
        // Target app package name
        String PACKAGE_NAME = "fm.qingting.qtradio";
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (intent != null)
        {
            intent.setAction("fm.qingting.qtradio.CAR_PLAY");
            intent.putExtra("client_id", "a1d182f150f9a4fb78c0cd8bc425abddc");
            intent.putExtra("invoke_action", "play");
            ctx.startActivity(intent);
        }

        sendAction(ctx, "fm.qingting.qtradio.CAR_PLAY");
    }

    // send action to qingting fm
    private void sendAction(Context ctx, String action)
    {
        Intent intent = new Intent();
        intent.setAction(action);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.sendBroadcast(intent);
    }

    @Override
    protected void onResume()
    {
        updataView();
        super.onResume();

        String fm_list = FoPreference.getString(FoConstants.FM_LIST);
        if (null != fm_list)
        {
            List list = new Gson().fromJson(fm_list, List.class);
        }
        else
        {
            //            Fo
        }

        //获取保存的选择记录
        String selected = FoPreference.getString(FoConstants.FM_SELECT);

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
        FoPreference.putString(FoConstants.FM_SELECT, showWhat + "");
    }
}
