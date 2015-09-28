package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.model.RoutePlanModel;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.BNavigatorActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoSettingOrDown;
import com.jinglingtec.ijiazu.util.TTSPlayerUtil;

/**
 * Created by coffee on 14-11-24.
 */
public class RoutePlan
{
    private Context context;
    private Activity act;
    private Address addressEnd;
    private Address addressStart;
    private RoutePlanModel mRoutePlanModel = null;

    public RoutePlan(Context context, Activity act, Address addressEnd)
    {
        this.context = context;
        this.act = act;

        //获取起点、终点信息
        this.addressEnd = addressEnd;
        addressStart = AddressInfos.getPoiAddress(FoPreference.getString(FoConstants.START_ADDRESS));

        //算距
        calculateDistance();
    }

    private void calculateDistance()
    {
        LatLng slat = new LatLng(addressStart.getLatitude(), addressStart.getLongitude());
        LatLng elat = new LatLng(addressEnd.getLatitude(), addressEnd.getLongitude());
        double distance = DistanceUtil.getDistance(slat, elat);
        if (distance < 100)
        {
            TTSPlayerUtil.play(context, context.getResources().getString(R.string.distance_near));
            return;
        }
        else
        {
            launchNavigator2();
        }
    }

    /**
     * 指定导航起终点启动GPS导航.起终点可为多种类型坐标系的地理坐标。
     * 前置条件：导航引擎初始化成功
     */
    private void launchNavigator2()
    {

        if (addressStart == null || addressEnd == null)
        {

            //提示用户设置
            FoSettingOrDown.remindUsers(context, FoConstants.NAVI);
            return;
        }

        //设置算路方式
        int mode = FoPreference.getInt(FoConstants.MODE);

        if (mode == 0)
        {
            mode = BaiduNaviUtil.COMMED;
        }
        //这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
        BNaviPoint startPoint = new BNaviPoint(addressStart.getLongitude(), addressStart.getLatitude(), addressStart.getName(), BNaviPoint.CoordinateType.BD09_MC);
        BNaviPoint endPoint = null;

        if (null == addressEnd.getName())
        {
            endPoint = new BNaviPoint(addressEnd.getLongitude(), addressEnd.getLatitude(), addressEnd.getAddress(), BNaviPoint.CoordinateType.BD09_MC);
        }
        else
        {
            endPoint = new BNaviPoint(addressEnd.getLongitude(), addressEnd.getLatitude(), addressEnd.getName(), BNaviPoint.CoordinateType.BD09_MC);
        }
        //        List<BNaviPoint> pointList = new ArrayList<BNaviPoint>();
        //        pointList.add(startPoint);
        //        pointList.add(endPoint);
        //
        //
        //        BaiduNaviManager.getInstance().launchNavigator(act,
        //                pointList,
        //                mode,
        //                true,
        //                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY,
        //                new BaiduNaviManager.OnStartNavigationListener()
        //                {                //跳转监听
        //
        //                    @Override
        //                    public void onJumpToNavigator(Bundle configParams)
        //                    {
        //                        FoPreference.putString(FoConstants.END_ADDRESS, addressEnd.getName());
        //                        Intent intent = new Intent(act, BNavigatorActivity.class);
        //                        intent.putExtras(configParams);
        //                        intent.putExtra(FoConstants.END_ADDRESS, addressEnd.getName());
        //                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //                        context.startActivity(intent);
        //                    }
        //
        //                    @Override
        //                    public void onJumpToDownloader()
        //                    {
        //                    }
        //                });

        if (null == startPoint || null == endPoint)
        {
            return;
        }

        BaiduNaviManager.getInstance().launchNavigator(act, startPoint,                                      //起点（可指定坐标系）
                endPoint,                                        //终点（可指定坐标系）
                mode,                                            //算路方式
                true,                                            //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new BaiduNaviManager.OnStartNavigationListener()
                {                //跳转监听

                    @Override
                    public void onJumpToNavigator(Bundle configParams)
                    {
                        //                        BNTTSPlayer.playTTSText(addressEnd.getName(), 1);
                        //                        TTSPlayerUtil.play(context,addressEnd.getName());
                        if (null != addressEnd.getName())
                        {
                            FoPreference.putString(FoConstants.NAVI_NAME, addressEnd.getName());
                        }
                        else
                        {
                            FoPreference.putString(FoConstants.NAVI_NAME, addressEnd.getAddress());
                        }

                        Intent intent = new Intent(act, BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        act.finish();
                    }

                    @Override
                    public void onJumpToDownloader()
                    {
                    }
                });
    }

}
