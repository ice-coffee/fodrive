package com.jinglingtec.ijiazu.invokeApps.baidunavi;//package com.jinglingtec.ijiazu.invokeApps.baidunavi;
//
//import android.app.Activity;
//import android.content.Context;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.jinglingtec.ijiazu.R;
//
//public class GPSTest extends Activity
//{
//
//    private Button btnStart;
//    private Button btnStop;
//    private TextView textView;
//    private Location mLocation;
//    private LocationManager mLocationManager;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.gpstest);
//
//        btnStart = (Button) findViewById(R.id.btnStart);
//        btnStop = (Button) findViewById(R.id.btnStop);
//        textView = (TextView) findViewById(R.id.text);
//        btnStart.setOnClickListener(btnClickListener); //开始定位
//        btnStop.setOnClickListener(btnClickListener); //结束定位按钮
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
//            textView.setText("维度:" + mLocation.getLatitude() + "\n经度:" + mLocation.getLongitude());
//        }
//        else
//        {
//            textView.setText("获取不到数据");
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
//                    textView.setText("维度:" + mLocation.getLatitude() + "\n经度:" + mLocation.getLongitude());
//                }
//                else
//                {
//                    textView.setText("获取不到数据");
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
//    //判断当前是否开启GPS
//    private boolean gpsIsOpen()
//    {
//        boolean bRet = true;
//
//        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER))
//        {
//            Toast.makeText(this, "未开启GPS", Toast.LENGTH_SHORT).show();
//            bRet = false;
//        }
//        else
//        {
//            Toast.makeText(this, "GPS已开启", Toast.LENGTH_SHORT).show();
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
//        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
//                textView.setText("维度:" + location.getLatitude() + "\n经度:"
//                        + location.getLongitude());
//            }
//            else
//            {
//                textView.setText("获取不到数据");
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
//}