package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.AddressInfos;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.FoLocalLocation;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.SaveAddress;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * poi搜索功能
 * 搜索监听、建议搜索监听
 */
public class PoiSearchResultActivity extends BaseActivity
{
    /**
     * 搜索相关
     */
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;
    private FoLocalLocation localLocation = null;

    private BitmapDescriptor bitMapSelect = null;
    private BitmapDescriptor bitMapNoSelect = null;

    /**
     * 搜索结果列表
     */
    private List<Address> all_poi_list = null;
    private List<BitmapDescriptor> bitList = null;
    private List<Marker> markerList = null;

    /**
     * 展示点
     */
    private LinearLayout ll_point;

    /**
     * 上一个指示点的位置
     */
    private int lastPointIndex = 0;


    /**
     * 搜索结果列表相关
     */
    private ViewPager viewPagerInfo;
    private List<PoiInfo> poiInfos = null;
    private MyPagerAdapter myAdapter = null;

    /**
     * 搜索结果左右滑动
     */
    private ImageView iv_next;
    private ImageView iv_per;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidunavi_poisearch_result);

        init();

        //获取传递list数据
        Intent intent = getIntent();

        all_poi_list = new ArrayList<Address>();
        Object[] cobjs = (Object[]) intent.getSerializableExtra(FoConstants.SEARCH_RESULT);
        for (int i = 0; i < cobjs.length; i++)
        {
            Address address = (Address) cobjs[i];
            all_poi_list.add(address);
        }

        bitMapSelect = BitmapDescriptorFactory.fromResource(R.drawable.click_on_select);
        bitMapNoSelect = BitmapDescriptorFactory.fromResource(R.drawable.click_on_not_select);

        if (all_poi_list != null)
        {
            if (null != viewPagerInfo && null != myAdapter)
            {

                //设置覆盖物
                showSearchOverlay();

                viewPagerInfo.setAdapter(myAdapter);
            }

            //画点
            for (int i = 0; i < all_poi_list.size(); i++)
            {
                //创建指示点
                ImageView point = new ImageView(this);
                point.setBackgroundResource(R.drawable.point_bg);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, -2);
                param.leftMargin = 15;
                param.rightMargin = 15;

                point.setLayoutParams(param);

                if (i == 0)
                {
                    point.setEnabled(true);
                }
                else
                {
                    point.setEnabled(false);
                }

                ll_point.addView(point);

            }

        }

    }

    public void init()
    {
        setTitleText(R.string.search);
        setHeaderLeftBtn();

        //map
        mMapView = (MapView) findViewById(R.id.search_map);
        mBaiduMap = mMapView.getMap();

        //开始搜索前地图显示当前用户地点

        getMyLocaction(null);

        bitList = new ArrayList<BitmapDescriptor>();
        markerList = new ArrayList<Marker>();

        //搜索结果滑动
        iv_next = (ImageView) findViewById(R.id.iv_poi_next);
        iv_per = (ImageView) findViewById(R.id.iv_poi_pervious);
        ll_point = (LinearLayout) findViewById(R.id.point_group);


        //设置比例尺控件和缩放控件是否显示
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(false);

        //viewpager
        viewPagerInfo = (ViewPager) findViewById(R.id.vp_navi_search);

        //viewpagerAdapter
        myAdapter = new MyPagerAdapter();

        /**
         * viewpager滑动事件
         */
        viewPagerInfo.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

                Marker markerPoint = markerList.get(position);
                Marker markerLastPoint = markerList.get(lastPointIndex);

                if (null == bitMapNoSelect)
                {
                    bitMapNoSelect = BitmapDescriptorFactory.fromResource(R.drawable.click_on_not_select);
                }
                else
                {
                    markerLastPoint.setIcon(bitMapNoSelect);
                }

                if (null == bitMapSelect)
                {
                    bitMapSelect = BitmapDescriptorFactory.fromResource(R.drawable.click_on_select);
                }
                else
                {
                    markerPoint.setIcon(bitMapSelect);
                }

                //将上一个指示点改为normal的状态
                ll_point.getChildAt(lastPointIndex).setEnabled(false);
                lastPointIndex = position;
                //将当前的指示点改为 focused
                ll_point.getChildAt(position).setEnabled(true);

                if (null != all_poi_list)
                {
                    getMyLocaction(all_poi_list.get(position));
                }


            }

            @Override
            public void onPageSelected(int position)
            {
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });

    }

    /**
     * 展示搜索结果
     */
    public void showSearchOverlay()
    {
        if (all_poi_list != null)
        {

            if (null != bitList)
            {
                bitList.clear();
            }
            else
            {
                bitList = new ArrayList<BitmapDescriptor>();
            }

            if (null != markerList)
            {
                markerList.clear();
            }
            else
            {
                markerList = new ArrayList<Marker>();
            }

            for (int i = 0; i < all_poi_list.size(); i++)
            {
                Address overAddr = all_poi_list.get(i);

                LatLng ll = new LatLng(overAddr.getLatitude(), overAddr.getLongitude());
                BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.click_on_not_select);

                bitList.add(bd);

                if (null != ll && null != bd)
                {
                    OverlayOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(9).draggable(true);

                    if (null != oo)
                    {
                        Marker marker = (Marker) mBaiduMap.addOverlay(oo);
                        markerList.add(marker);
                    }
                }
            }
        }
    }

    public void nextPage(View view)
    {
        int pointPosition = lastPointIndex + 1;
        if (pointPosition > 10 || pointPosition < 0)
        {
            return;
        }
        viewPagerInfo.setCurrentItem(pointPosition);
    }

    public void previousPage(View view)
    {
        int pointPosition = lastPointIndex - 1;
        if (pointPosition > 10 || pointPosition < 0)
        {
            return;
        }
        viewPagerInfo.setCurrentItem(pointPosition);
    }

    /**
     * 放大map
     *
     * @param view
     */
    public void amplificationMap(View view)
    {
        float zoom = mBaiduMap.getMapStatus().zoom + 1.0f;

        float minZoom = mBaiduMap.getMinZoomLevel();
        float maxZoom = mBaiduMap.getMaxZoomLevel();
        ImageView iv_big = (ImageView) findViewById(R.id.iv_map_big);
        ImageView iv_small = (ImageView) findViewById(R.id.iv_map_small);

        if (maxZoom == zoom)
        {
            iv_big.setBackgroundResource(R.drawable.amplification_press);
        }
        if (minZoom != zoom)
        {
            iv_small.setBackgroundResource(R.drawable.selector_narrow);
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(zoom);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 缩小map
     *
     * @param view
     */
    public void narrowMap(View view)
    {
        float zoom = mBaiduMap.getMapStatus().zoom - 1.0f;

        float minZoom = mBaiduMap.getMinZoomLevel();
        float maxZoom = mBaiduMap.getMaxZoomLevel();
        ImageView iv_big = (ImageView) findViewById(R.id.iv_map_big);
        ImageView iv_small = (ImageView) findViewById(R.id.iv_map_small);

        if (minZoom == zoom)
        {
            iv_small.setBackgroundResource(R.drawable.narrow_press);
        }
        if (maxZoom != zoom)
        {
            iv_big.setBackgroundResource(R.drawable.selector_amlification);
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(zoom);
        mBaiduMap.setMapStatus(msu);
    }

    class MyPagerAdapter extends PagerAdapter
    {

        @Override
        /**
         * 返回页面的个数
         */
        public int getCount()
        {
            return all_poi_list.size();
        }

        @Override
        /**
         * 获得指定位置上的view
         * container 就是viewPager自身
         * position 是指定的位置
         */
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = View.inflate(PoiSearchResultActivity.this, R.layout.search_result_list_item, null);

            TextView search_address = (TextView) view.findViewById(R.id.et_search_address);
            TextView search_name = (TextView) view.findViewById(R.id.et_search_name);
            TextView save_home = (TextView) view.findViewById(R.id.tv_save_search_home);
            TextView save_company = (TextView) view.findViewById(R.id.tv_save_search_company);
            TextView address_number = (TextView) view.findViewById(R.id.tv_address_number);

            if (null == all_poi_list)
            {
                return null;
            }

            final Address address = all_poi_list.get(position);

            if (null == address)
            {
                return null;
            }

            search_address.setText(address.getAddress());
            search_name.setText(address.getName());
            address_number.setText((position + 1) + "");

            save_home.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saveAddressInfo(FoConstants.HOME, address);
                }
            });

            save_company.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    saveAddressInfo(FoConstants.COMPANY, address);
                }
            });

            container.addView(view);

            return view;
        }


        @Override
        /**
         * 判断指定的的view和object是否有关联关系
         * view 某一位置上的显示的页面
         * object 某一位置上返回的object 就是instantiateItem返回的object
         */
        public boolean isViewFromObject(View view, Object object)
        {

            return view == object;
        }

        @Override
        /**
         * 销毁指定位置上的view
         *
         * object 就是instantiateItem 返回的object
         */
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

    }

    /**
     * 判断是否已经收藏
     *
     * @return false 没有 ， true 有
     */
    public boolean haveAddressName(Address address_cache)
    {
        boolean isHome = false;
        boolean isCompany = false;
        if (null != address_cache)
        {
            Address addrHome = new Gson().fromJson(FoPreference.getString(FoConstants.HOME), Address.class);
            Address addrCompany = new Gson().fromJson(FoPreference.getString(FoConstants.COMPANY), Address.class);

            if (null != addrHome)
            {
                if (address_cache.getLatitude() != addrHome.getLatitude() || address_cache.getLongitude() != addrHome.getLongitude())
                {

                    isHome = true;
                }
            }
            else
            {
                isHome = true;
            }

            if (null != addrCompany)
            {
                if (address_cache.getLatitude() != addrCompany.getLatitude() || address_cache.getLongitude() != addrCompany.getLongitude())
                {
                    isCompany = true;
                }
            }
            else
            {
                isCompany = true;
            }
        }

        if (isHome && isCompany)
        {
            return true;
        }

        return false;
    }

    public void saveAddressInfo(String saveKey, Address address)
    {
        if (haveAddressName(address))
        {
            if (null == address)
            {
                return;
            }

            SaveAddress saveCompany = new SaveAddress();
            saveCompany.save(saveKey, address);
            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.collection_successs));
        }
        else
        {
            FoUtil.toast(getApplicationContext(), R.string.collected_successs);
        }
    }

    public void getMyLocaction(Address address)
    {
        localLocation = new FoLocalLocation(mBaiduMap);

        if (null != address)
        {
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());

            //设置地图缩放级别
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(mBaiduMap.getMapStatus().zoom);
            this.mBaiduMap.setMapStatus(msu);

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

            this.mBaiduMap.animateMapStatus(u);

            return;
        }

        String startAddress = FoPreference.getString(FoConstants.START_ADDRESS);

        if (startAddress != null)
        {
            Address addr = AddressInfos.getPoiAddress(startAddress);
            LatLng ll = new LatLng(addr.getLatitude(), addr.getLongitude());

            localLocation.locateSearchPosition(ll);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {

        mMapView.onDestroy();
        //回收bitmap资源
        for (int i = 0; i < bitList.size(); i++)
        {
            bitList.get(i).recycle();
        }
        bitMapNoSelect.recycle();
        bitMapSelect.recycle();
        super.onDestroy();
    }


}
