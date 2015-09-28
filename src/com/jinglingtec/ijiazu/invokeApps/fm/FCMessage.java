package com.jinglingtec.ijiazu.invokeApps.fm;

import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.util.FoConstants;

/**
 * Created by coffee on 15-1-20.
 */
public class FCMessage
{
    //支持音乐列表
    public static String[] musicApps = {FoConstants.KWMUSIC, FoConstants.BDMUSIC, FoConstants.DMMUSIC};
    public static String[] musicAppsName = {FoConstants.KWNAME, FoConstants.BDNAME, FoConstants.DMNAME};
    public static int[] musicImage = {R.drawable.kuwo_mini, R.drawable.baidu_mini, R.drawable.duomi_mini};

    //支持电台列表
    public static String[] fmApps = {FoConstants.QTFM, FoConstants.KLFM, FoConstants.KDFM};
    public static String[] fmAppsName = {FoConstants.QTNAME, FoConstants.KLNAME, FoConstants.KDNAME};
    public static int[] fmImage = {R.drawable.qingting_mini, R.drawable.kaola_mini, R.drawable.koudai_mini};

}
