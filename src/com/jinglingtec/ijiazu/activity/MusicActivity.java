package com.jinglingtec.ijiazu.activity;//package com.jinglingtec.fodrive.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//import cn.kuwo.autosdk.api.KWAPI;
//import cn.kuwo.autosdk.api.PlayState;
//import com.jinglingtec.fodrive.R;
//import com.jinglingtec.fodrive.util.FoConstants;
//import com.jinglingtec.fodrive.util.FoMusicOrFM;
//
//import java.util.List;
//import java.util.Map;
//
//public class MusicActivity extends Activity
//{
//
//    // Kuwo API instance
//    private KWAPI mKwapi = null;
//    // is kuwo music playing?
//    // Note: it's not accuracy
//    private boolean isPlaying = false;
//
//
//    private TextView bt_start;
//    private TextView bt_pause;
//    private TextView bt_next;
//    private TextView bt_pre;
//
//    private TextView music_one;
//    private TextView music_two;
//    private TextView music_three;
//
//    private List<Map<String, Integer>> appList;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fodrive_fc);
//
//        bt_start = (TextView) findViewById(R.id.tv_mv_start);
//        bt_pause = (TextView) findViewById(R.id.tv_mv_pause);
//        bt_next = (TextView) findViewById(R.id.tv_mv_next);
//        bt_pre = (TextView) findViewById(R.id.tv_mv_pre);
//
//        music_one = (TextView) findViewById(R.id.tv_music_one);
//        music_two = (TextView) findViewById(R.id.tv_music_two);
//        music_three = (TextView) findViewById(R.id.tv_music_three);
//
//        //酷我音乐初始化
//        initialize(getApplicationContext());
//
//        //音乐app安装检测
//        FoMusicOrFM mf = new FoMusicOrFM(getApplicationContext(), FoConstants.MUSIC);
////        appList = mf.getApp();
//
//        if (appList.get(0).containsKey(music_one.getText()))
//        {
//            music_one.setTextColor(getResources().getColor(R.color.red));
//
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
//        }
//
//
//        bt_pause.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mKwapi.setPlayState(getApplicationContext(), PlayState.STATE_PAUSE);
//            }
//        });
//        bt_start.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//
//                if (!isPlaying)
//                {
//                    mKwapi.startAPP(getApplicationContext(), true);
//                    isPlaying = true;
//                }
//            }
//        });
//        bt_next.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mKwapi.setPlayState(getApplicationContext(), PlayState.STATE_NEXT);
//            }
//        });
//    }
//
//    public void initialize(Context ctx)
//    {
//        if (null == mKwapi)
//        {
//            // Kuwo API Key
//            final String KUWO_API_KEY = "auto";
//            mKwapi = KWAPI.createKWAPI(ctx, KUWO_API_KEY);
//        }
//    }
//
//    public void singleClick(Context ctx)
//    {
//        if (null == mKwapi)
//        {
//            initialize(ctx);
//            mKwapi.startAPP(ctx, true);
//        }
//        if (null != mKwapi)
//        {
//            mKwapi.startAPP(ctx, true);
//            if (!isPlaying)
//            {
//                // not playing right now, start to play
////                mKwapi.setPlayState(ctx, PlayState.STATE_PLAY);
//                isPlaying = true;
//            }
//            else
//            {
//                // it's in playing state, so play next
//                mKwapi.setPlayState(ctx, PlayState.STATE_NEXT);
//            }
//        }
//    }
//
//    public void doubleClick(Context ctx)
//    {
//        // switch to next channel
//        // Kuwo not supports this actions, so keep blank here
//
////        if (null == mKwapi)
////        {
////            initialize(ctx);
////        }
////        if (null != mKwapi)
////        {
////            mKwapi.startAPP(ctx, true);
////            mKwapi.setPlayState(ctx, PlayState.STATE_NEXT);
////        }
//    }
//
//    public void longPressed(Context ctx)
//    {
//        if (null != mKwapi)
//        {
//            mKwapi.setPlayState(ctx, PlayState.STATE_PAUSE);
//            isPlaying = false;
//        }
//    }
//
//    public void release(Context ctx)
//    {
//        if (null != mKwapi)
//        {
//            mKwapi.exitAPP(ctx);
//            mKwapi = null;
//            isPlaying = false;
//        }
//    }
//
//}
