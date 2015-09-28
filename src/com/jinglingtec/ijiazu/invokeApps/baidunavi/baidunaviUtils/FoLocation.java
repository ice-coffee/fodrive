package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.app.Activity;
import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;

/**
 * Created by coffee on 14-11-14.
 */
public class FoLocation
{

    private Context context;

    // 定位相关
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    // 是否首次定位
    boolean isFirstLoc = true;

    public FoLocation(Context context, Activity act)
    {

        this.context = context;

        // 定位初始化
        mLocClient = new LocationClient(act);
        mLocClient.registerLocationListener(myListener);
        //该类用来设置定位SDK的定位方式
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型 国测局经纬度坐标系 coor=gcj02； 返回百度墨卡托坐标系 coor=bd09； 返回百度经纬度坐标系 coor=bd09ll
        option.setCoorType("bd09ll");
        //设置定位时间间隔 大于等于1000（ms）时进行实时定位 不然采用一次定位模式
        option.setScanSpan(500);
        mLocClient.setLocOption(option);
        //开始定位
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location)
        {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
            {
                return;
            }

            //            //定位数据
            //            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
            //                    // 此处设置开发者获取到的方向信息，顺时针0-360
            //                    .direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            //
            //            mBaiduMap.setMyLocationData(locData);
            //
            //            //第一次定位当前位置居中
            //            if (isFirstLoc)
            //            {
            //                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            //                FoLocalLocation foLocalLocation = new FoLocalLocation(mBaiduMap);
            //                if (null == foLocalLocation)
            //                {
            //                    return;
            //                }
            //                foLocalLocation.locatePosition(ll);
            //                isFirstLoc = false;
            //            }

            String city = location.getProvince();

            if (null != city)
            {
                FoPreference.putString(FoConstants.CITY, city);
            }
            //记录您当前到位置，但是这个位置应该会改变到所以还需要思考
            FoGeoCoder coder = new FoGeoCoder(context, FoConstants.START_ADDRESS, location.getLatitude(), location.getLongitude());
            coder.reverseSearchProcess();

        }

    }

    public void destroyLocation()
    {
        // 退出时销毁定位
        mLocClient.stop();
    }

}
