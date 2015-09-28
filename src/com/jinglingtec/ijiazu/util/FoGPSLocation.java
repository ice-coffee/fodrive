package com.jinglingtec.ijiazu.util;//package com.jinglingtec.ijiazu.util;
//
//import android.content.Context;
//import android.content.Intent;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//import com.google.gson.Gson;
//import com.jinglingtec.ijiazu.R;
//import com.jinglingtec.ijiazu.invokeApps.baidunavi.RoutePlanActivity;
//import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
//
///**
// * Created by coffee on 15-1-23.
// */
//public class FoGPSLocation
//{
//    private Context context;
//
//    private Location mLocation;
//    private LocationManager mLocationManager;
//
//    public FoGPSLocation(Context context, Address endAddress)
//    {
//        this.context = context;
//
//        if (!gpsIsOpen())
//        {
//            return;
//        }
//
//        mLocation = getLocation();
//
//        if (mLocation != null)
//        {
//            ecarStartNavi(endAddress);
//        }
//    }
//
//    //gpsIsOpen是自己写的查看当前GPS是否开启
//    //getLocation 是自己写的一个获取定位信息的方法
//    //mLocationManager.removeUpdates()是停止当前的GPS位置监听
//    public Button.OnClickListener btnClickListener = new Button.OnClickListener()
//    {
//        public void onClick(View v)
//        {
//            Button btn = (Button) v;
//            if (btn.getId() == R.id.btnStart)
//            {
//                if (!gpsIsOpen())
//                {
//                    return;
//                }
//
//                mLocation = getLocation();
//
//                if (mLocation != null)
//                {
//                }
//                else
//                {
//                }
//            }
//            else if (btn.getId() == R.id.btnStop)
//            {
//                mLocationManager.removeUpdates(locationListener);
//            }
//
//        }
//    };
//
//    public void ecarStartNavi(Address endAddress)
//    {
//
//        FoPreference.putString(FoConstants.NAVI_TO, FoConstants.NAVI_HOME);
//        Intent intent = new Intent(context, RoutePlanActivity.class);
//        intent.putExtra(FoConstants.END_ADDRESS, new Gson().toJson(endAddress));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }
//
//    //判断当前是否开启GPS
//    private boolean gpsIsOpen()
//    {
//        boolean bRet = true;
//
//        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER))
//        {
//            bRet = false;
//        }
//        else
//        {
//
//        }
//
//        return bRet;
//    }
//
//    //该方法获取当前的经纬度， 第一次获取总是null
//    //   后面从LocationListener获取已改变的位置
//    //mLocationManager.requestLocationUpdates()是开启一个LocationListener等待位置变化
//    private Location getLocation()
//    {
//        //获取位置管理服务
//        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        //查找服务信息
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
//        criteria.setAltitudeRequired(false); //海拔信息：不需要
//        criteria.setBearingRequired(false); //方位信息: 不需要
//        criteria.setCostAllowed(false);  //是否允许付费
//        criteria.setPowerRequirement(Criteria.POWER_LOW); //耗电量: 低功耗
//
//        String provider = mLocationManager.getBestProvider(criteria, true); //获取GPS信息
//
//        Location location = mLocationManager.getLastKnownLocation(provider);
//
//        mLocationManager.requestLocationUpdates(provider, 2000, 5, locationListener);
//
//        return location;
//    }
//
//    //此方法是等待GPS位置改变后得到新的经纬度
//    private final LocationListener locationListener = new LocationListener()
//    {
//        public void onLocationChanged(Location location)
//        {
//            // TODO Auto-generated method stub
//            if (location != null)
//            {
//
//            }
//            else
//            {
//
//            }
//        }
//
//        public void onProviderDisabled(String provider)
//        {
//            // TODO Auto-generated method stub
//        }
//
//        public void onProviderEnabled(String provider)
//        {
//            // TODO Auto-generated method stub
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras)
//        {
//            // TODO Auto-generated method stub
//
//        }
//    };
//
//}
