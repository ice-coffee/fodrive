package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 地图回到当前位置
 */
public class FoLocalLocation
{
    private BaiduMap mBaiduMap;

    public FoLocalLocation(BaiduMap mBaiduMap)
    {
        if (null == mBaiduMap)
        {
            return;
        }
        this.mBaiduMap = mBaiduMap;

    }

    //地图定位
    public void locatePosition(LatLng ll)
    {

        //设置地图缩放级别
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        this.mBaiduMap.setMapStatus(msu);

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

        this.mBaiduMap.animateMapStatus(u);

        //定位数据
        MyLocationData locData = new MyLocationData.Builder().latitude(ll.latitude).longitude(ll.longitude).build();

        mBaiduMap.setMyLocationData(locData);
    }

    //搜索定位
    public void locateSearchPosition(LatLng ll)
    {

        //设置地图缩放级别
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);
        this.mBaiduMap.setMapStatus(msu);

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

        this.mBaiduMap.animateMapStatus(u);
    }

}
