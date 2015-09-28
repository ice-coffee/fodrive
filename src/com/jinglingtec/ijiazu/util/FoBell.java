package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import com.jinglingtec.ijiazu.R;

import java.util.HashMap;

/**
 * Created by coffee on 15-1-23.
 */
public class FoBell
{

    private Context context;

    private SoundPool sp = null;//声明一个SoundPool的引用
    private HashMap<Integer, Integer> hm;//声明一个HashMap来存放声音资源
    private int currentStreamId;//当前播放的StreamId

    private Boolean isFinishedLoad = false;//查看音乐文件是否加载完毕
    private Boolean isPausePlay = false;//是否暂停播放

    private AudioManager am = null;
    private float currentStreamVolume = 7.0f;

    public FoBell(Context context, int key)
    {
        this.context = context;

        //初始化SoundPool
        initSoundPool(key);
    }

    public void initSoundPool(final int key)
    {
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);//创建SoundPool对象
        hm = new HashMap<Integer, Integer>();//创建HashMap对象

        //加载声音文件
        switch (key)
        {
            case 1:
                hm.put(1, sp.load(context, R.raw.pressbar7, 0));//加载单声声音文件

                break;

            case 2:
                hm.put(2, sp.load(context, R.raw.pressbar7, 0));//加载单声声音文件
                break;

            case 3:
                hm.put(3, sp.load(context, R.raw.pressbar7, 0));//加载单声声音文件
                break;
        }

        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                // TODO Auto-generated method stub
                isFinishedLoad = true;
                playSound(key);
            }
        });
    }

    public void playSound(int key)
    {

        Log.e("fobellplaysound", "111111");
        if (!isPausePlay)
        {
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (null == am)
            {
                return;
            }

            int currentStreamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxStreamVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float setVolume = (float) currentStreamVolume / maxStreamVolume;

            if (isFinishedLoad)
            {
                currentStreamId = sp.play(hm.get(key), setVolume, setVolume, 1, 0, 1.0f);
            }
        }
        else
        {
            isPausePlay = false;
            sp.resume(currentStreamId);
        }
    }

    public void destoryPool()
    {

        am.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0);
        sp.unload(currentStreamId);
        sp.release();
    }

}
