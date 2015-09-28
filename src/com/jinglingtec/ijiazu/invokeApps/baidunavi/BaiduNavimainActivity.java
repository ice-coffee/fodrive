package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.*;
import com.jinglingtec.ijiazu.util.*;

/**
 * 地图选点定位和地址搜索入口
 */
public class BaiduNavimainActivity extends BaseActivity implements View.OnClickListener
{

    // 搜索成功，隐藏用户等待提示窗，并显示搜索结果
    private final int SEARCH_SUCCESS = 1;
    // 开始搜索，显示用户等待提示窗
    private final int SEARCH_START = 2;
    // 无网络状况下提示用户，搜索失败
    private final int SEARCH_NO_NET = 3;
    // 搜索时间过长提示用户，搜索失败
    private final int SEARCH_FAILED = 4;


    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /**
     * 数据操作入口
     */
    private FoGeoCoder coder = null;
    private FoLocation location = null;

    private FoLocalLocation foLocalLocation = null;

    /**
     * 等待
     */
    private RelativeLayout navi_load;
    private ImageView loadImage;

    /**
     * 收藏地址1、2
     */
    private RelativeLayout rl_address_1;
    private RelativeLayout rl_address_2;

    /**
     * 显示地图选点
     */
    private LinearLayout showSelect;
    /**
     * 搜索页面入口
     */
    private RelativeLayout toSearch;

    /**
     * 搜索关键字
     */
    private TextView keyword;

    /**
     * 家庭、公司地址
     */
    private Address address_home = null;
    private Address address_company = null;
    private Address address_cache = null;

    /**
     * 收藏入口
     */
    private TextView addrHome;
    private TextView addrCompany;

    /**
     * 导航偏好入口
     */
    private RelativeLayout routePlan;

    /**
     * 导航偏好
     */
    private RelativeLayout navi_def;//默认
    private RelativeLayout navi_dist;//拥堵
    private RelativeLayout navi_taf;//收费
    private RelativeLayout navi_time;//时间
    private RelativeLayout navi_toll;//距离

    private ImageView iv_def;//默认
    private ImageView iv_dist;//拥堵
    private ImageView iv_taf;//收费
    private ImageView iv_time;//时间
    private ImageView iv_toll;//距离

    private TextView showAddr;

    private Handler handlerNavi = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            int updateUi = msg.what;

            int loadState = navi_load.getVisibility();

            switch (updateUi)
            {
                case SEARCH_SUCCESS:
                    if (loadState == View.VISIBLE)
                    {
                        navi_load.setVisibility(View.GONE);
                        loadImage.clearAnimation();

                        showWindow();
                    }
                    break;
                case SEARCH_START:
                    if (loadState == View.GONE)
                    {
                        navi_load.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(BaiduNavimainActivity.this, R.anim.loading);
                        loadImage.startAnimation(animation);
                    }
                    break;

                case SEARCH_NO_NET:
                    if (loadState == View.VISIBLE)
                    {
                        navi_load.setVisibility(View.GONE);
                        loadImage.clearAnimation();
                    }

                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.no_internet));
                    break;
                case SEARCH_FAILED:
                    if (loadState == View.VISIBLE)
                    {
                        navi_load.setVisibility(View.GONE);
                        loadImage.clearAnimation();
                    }

                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.no_search_result));
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidunavi_home);

        //导航key验证
        new FoKeyValidation(this);

        //title
        setTitleText(R.string.navigation);
        setTitleTextListener(R.string.navigation, listenerPreference);
        setHeaderRightBtn(R.string.offline_map, listenerOfflineMap);
        setHeaderLeftBtn();

        //地图初始化
        mMapView = (MapView) findViewById(R.id.mapView_main);
        mBaiduMap = mMapView.getMap();

        addrHome = (TextView) findViewById(R.id.tv_save_address_home);
        addrCompany = (TextView) findViewById(R.id.tv_save_address_company);
        rl_address_1 = (RelativeLayout) findViewById(R.id.rl_navi_collection_address_1);
        rl_address_2 = (RelativeLayout) findViewById(R.id.rl_navi_collection_address_2);
        showSelect = (LinearLayout) findViewById(R.id.rl_navi_show_back_address);
        toSearch = (RelativeLayout) findViewById(R.id.rl_navi_goto_search);
        keyword = (TextView) findViewById(R.id.et_navi_write_keyword);
        showAddr = (TextView) findViewById(R.id.et_show_address);

        navi_def = (RelativeLayout) findViewById(R.id.rl_navi_preference_def);
        navi_dist = (RelativeLayout) findViewById(R.id.rl_navi_preference_dist);
        navi_taf = (RelativeLayout) findViewById(R.id.rl_navi_preference_taf);
        navi_time = (RelativeLayout) findViewById(R.id.rl_navi_preference_time);
        navi_toll = (RelativeLayout) findViewById(R.id.rl_navi_preference_toll);

        iv_def = (ImageView) findViewById(R.id.iv_prefer_def);
        iv_dist = (ImageView) findViewById(R.id.iv_prefer_dist);
        iv_taf = (ImageView) findViewById(R.id.iv_prefer_taf);
        iv_time = (ImageView) findViewById(R.id.iv_prefer_time);
        iv_toll = (ImageView) findViewById(R.id.iv_prefer_toll);

        //等待
        navi_load = (RelativeLayout) findViewById(R.id.rl_navi_loading);
        loadImage = (ImageView) findViewById(R.id.iv_navi_loading);

        //设置比例尺控件和缩放控件是否显示
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(false);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //监听地图状态改变
        initOverlay();

        if (null != mBaiduMap)
        {
            //进行定位
            //            location = new FoLocation(getApplicationContext(), this);

            String startAddress = FoPreference.getString(FoConstants.START_ADDRESS);
            //打开地图瞬间显示定位，防止地图移动
            foLocalLocation = new FoLocalLocation(mBaiduMap);
            if (startAddress != null)
            {
                Address addr = AddressInfos.getPoiAddress(startAddress);
                LatLng ll = new LatLng(addr.getLatitude(), addr.getLongitude());
                foLocalLocation.locatePosition(ll);
            }
        }

        //点击事件
        addrHome.setOnClickListener(this);
        addrCompany.setOnClickListener(this);
        rl_address_1.setOnClickListener(this);
        rl_address_2.setOnClickListener(this);
        toSearch.setOnClickListener(this);

        navi_def.setOnClickListener(this);
        navi_dist.setOnClickListener(this);
        navi_taf.setOnClickListener(this);
        navi_time.setOnClickListener(this);
        navi_toll.setOnClickListener(this);

        //选点地址展示
        showAddr.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    showAddr.setText("");
                    //显示软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        //长按事件并拦截
        rl_address_1.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {

                if (null != address_home)
                {
                    showDialog(FoConstants.HOME);
                }
                else
                {
                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.address_no_set));
                }
                return true;
            }
        });

        //长按事件并拦截
        rl_address_2.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {

                if (null != address_company)
                {
                    showDialog(FoConstants.COMPANY);
                }
                else
                {
                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.address_no_set));
                }
                return true;
            }
        });

    }

    public void initOverlay()
    {

        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener()
        {
            public void onMapLongClick(LatLng point)
            {

                //长按显示图标
                ImageView ijiazu = new ImageView(BaiduNavimainActivity.this);
                ijiazu.setBackgroundResource(R.drawable.click_on_select);
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(ijiazu), point, 0, new InfoWindow.OnInfoWindowClickListener()
                {
                    @Override
                    public void onInfoWindowClick()
                    {
                        showWindow();
                    }
                });
                mBaiduMap.showInfoWindow(mInfoWindow);

                //消除address_cache
                address_cache = null;

                if (null == point)
                {
                    return;
                }

                if (!FoNet.getNetState(getApplicationContext()))
                {
                    //提示用户等待
                    handlerNavi.sendEmptyMessage(SEARCH_NO_NET);
                    return;
                }

                //提示用户等待

                handlerNavi.sendEmptyMessage(SEARCH_START);

                coder = new FoGeoCoder(getApplicationContext(), FoConstants.CACHE, point.latitude, point.longitude);
                coder.reverseSearchProcess();

                //10秒后如果还是没有检测到结果那么提示用户
                noSearchRemind();

                //函数回调记录选点位置
                coder.setLocationListener(new FoGeoCoder.GeoCoderSuccess()
                {
                    @Override
                    public void onGetReverseGeoCodeSuccess(Address result)
                    {
                        address_cache = result;
                        handlerNavi.sendEmptyMessage(SEARCH_SUCCESS);
                    }
                });

            }
        });

    }

    // click listener for pick up tel number from contact
    final View.OnClickListener listenerOfflineMap = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            OfflineMap();
        }
    };

    // click listener for pick up tel number from contact
    final View.OnClickListener listenerPreference = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            //            OfflineMap();
            NaviPreference();
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //家
            case R.id.rl_navi_collection_address_1:
                if (null != address_home)
                {

                    new RoutePlan(getApplicationContext(), this, address_home);
                }
                else
                {
                    //提示用户设置
                    TTSPlayerUtil.play(getApplicationContext(), getResources().getString(R.string.please_set_end_address));
                }
                break;
            //公司
            case R.id.rl_navi_collection_address_2:
                if (null != address_company)
                {
                    new RoutePlan(getApplicationContext(), this, address_company);
                }
                else
                {
                    //提示用户设置
                    TTSPlayerUtil.play(getApplicationContext(), getResources().getString(R.string.please_set_end_address));
                }
                break;
            //搜索入口
            case R.id.rl_navi_goto_search:
                Intent intent = new Intent(BaiduNavimainActivity.this, PoiSearchActivity.class);
                intent.putExtra(FoConstants.KEYWORD, keyword.getText().toString());
                startActivity(intent);
                break;
            //收藏1
            case R.id.tv_save_address_home:
                if (haveAddressName())
                {
                    if (null != address_cache)
                    {
                        address_cache.setName(showAddr.getText().toString());
                        SaveAddress saveHome = new SaveAddress();
                        saveHome.save(FoConstants.HOME, address_cache);
                        address_home = address_cache;
                        showAddress();
                        FoUtil.toast(getApplicationContext(), getResources().getString(R.string.collection_successs));
                    }
                }
                else
                {
                    FoUtil.toast(getApplicationContext(), R.string.collected_successs);
                }
                break;
            //收藏2
            case R.id.tv_save_address_company:
                if (haveAddressName())
                {
                    if (null != address_cache)
                    {
                        address_cache.setName(showAddr.getText().toString());
                        SaveAddress saveCompany = new SaveAddress();
                        saveCompany.save(FoConstants.COMPANY, address_cache);
                        address_company = address_cache;
                        showAddress();
                        FoUtil.toast(getApplicationContext(), getResources().getString(R.string.collection_successs));
                    }
                }
                else
                {
                    FoUtil.toast(getApplicationContext(), R.string.collected_successs);
                }
                break;
            case R.id.rl_navi_preference_def:
                saveNaviPreference(R.id.rl_navi_preference_def);
                break;
            case R.id.rl_navi_preference_dist:
                saveNaviPreference(R.id.rl_navi_preference_dist);
                break;
            case R.id.rl_navi_preference_taf:
                saveNaviPreference(R.id.rl_navi_preference_taf);
                break;
            case R.id.rl_navi_preference_time:
                saveNaviPreference(R.id.rl_navi_preference_time);
                break;
            case R.id.rl_navi_preference_toll:
                saveNaviPreference(R.id.rl_navi_preference_toll);
                break;
            default:
                break;
        }
    }


    //没有搜索结果提示用户
    public void noSearchRemind()
    {
        ThreadUtil.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(10000);
                } catch (Exception e)
                {

                }

                if (navi_load.getVisibility() == View.VISIBLE)
                {
                    handlerNavi.sendEmptyMessage(SEARCH_FAILED);
                }
            }
        });
    }

    public void saveNaviPreference(int id)
    {
        NaviPreference();
        switch (id)
        {
            case R.id.rl_navi_preference_def:
                FoPreference.putInt(FoConstants.MODE, BaiduNaviUtil.COMMED);
                showNaviPreference();
                break;
            case R.id.rl_navi_preference_dist:
                FoPreference.putInt(FoConstants.MODE, BaiduNaviUtil.DIST);
                showNaviPreference();
                break;
            case R.id.rl_navi_preference_taf:
                FoPreference.putInt(FoConstants.MODE, BaiduNaviUtil.TRAFFIC);
                showNaviPreference();
                break;
            case R.id.rl_navi_preference_time:
                FoPreference.putInt(FoConstants.MODE, BaiduNaviUtil.TIME);
                showNaviPreference();
                break;
            case R.id.rl_navi_preference_toll:
                FoPreference.putInt(FoConstants.MODE, BaiduNaviUtil.TOLL);
                showNaviPreference();
                break;
            default:
                break;
        }

    }

    public void showNaviPreference()
    {

        clearAllPreferIm();
        //        showNaviLike();
        switch (FoPreference.getInt(FoConstants.MODE))
        {

            case BaiduNaviUtil.COMMED:

                iv_def.setBackgroundResource(R.drawable.navigation_strategy_choose);
                break;

            case BaiduNaviUtil.TRAFFIC:
                iv_taf.setBackgroundResource(R.drawable.navigation_strategy_choose);
                break;

            case BaiduNaviUtil.TOLL:
                iv_toll.setBackgroundResource(R.drawable.navigation_strategy_choose);
                break;

            case BaiduNaviUtil.TIME:
                iv_time.setBackgroundResource(R.drawable.navigation_strategy_choose);
                break;

            case BaiduNaviUtil.DIST:
                iv_dist.setBackgroundResource(R.drawable.navigation_strategy_choose);
                break;

        }
    }

    //    public void showNaviLike()
    //    {
    //        TextView tv_prefer = (TextView) findViewById(R.id.baidunavi_header_bar).findViewById(R.id.tv_navi_preference);
    //        switch (FoPreference.getInt(FoConstants.MODE))
    //        {
    //
    //            case FoConstants.COMMED:
    //
    //                tv_prefer.setText(getResources().getString(R.string.like_recommend));
    //                break;
    //
    //            case FoConstants.TRAFFIC:
    //                tv_prefer.setText(getResources().getString(R.string.like_avoid_taffic));
    //                break;
    //
    //            case FoConstants.TOLL:
    //                tv_prefer.setText(getResources().getString(R.string.like_min_toll));
    //                break;
    //
    //            case FoConstants.TIME:
    //                tv_prefer.setText(getResources().getString(R.string.like_min_time));
    //                break;
    //
    //            case FoConstants.DIST:
    //                tv_prefer.setText(getResources().getString(R.string.like_min_dist));
    //                break;
    //
    //        }
    //    }

    public void clearAllPreferIm()
    {
        iv_def.setBackgroundResource(R.drawable.navigation_strategy_not_choose);
        iv_taf.setBackgroundResource(R.drawable.navigation_strategy_not_choose);
        iv_toll.setBackgroundResource(R.drawable.navigation_strategy_not_choose);
        iv_time.setBackgroundResource(R.drawable.navigation_strategy_not_choose);
        iv_dist.setBackgroundResource(R.drawable.navigation_strategy_not_choose);
    }

    /**
     * 判断是否已经收藏
     *
     * @return false 没有 ， true 有
     */
    public boolean haveAddressName()
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

    /**
     * 显示收藏条目
     */
    public void showWindow()
    {
        if (null != address_cache)
        {
            showSelect.setVisibility(View.VISIBLE);
            showAddr.setText(address_cache.getAddress());
        }
        else
        {
            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.not_search_result));
        }
    }

    public void dismissWindow()
    {
        showSelect.setVisibility(View.GONE);
        showAddr.setText("");
    }


    /**
     * 删除提示
     *
     * @param mark
     */
    public void showDialog(final String mark)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.request_delete)).setCancelable(false).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FoPreference.getPreference().edit().remove(mark).commit();
                if (mark == FoConstants.HOME)
                {
                    address_home = null;
                }
                else
                {
                    address_company = null;
                }
                //更新地址显示
                showAddress();
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.show();

    }

    /**
     * 回到当前位置
     *
     * @param view
     */
    public void GoToLocalLocation(View view)
    {
        String startAddress = FoPreference.getString(FoConstants.START_ADDRESS);

        if (startAddress != null)
        {
            Address addr = AddressInfos.getPoiAddress(startAddress);
            LatLng ll = new LatLng(addr.getLatitude(), addr.getLongitude());
            foLocalLocation.locatePosition(ll);
        }
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

    /**
     * 离线地图入口
     */
    public void OfflineMap()
    {

        Intent intent = new Intent(BaiduNavimainActivity.this, OfflineMapActivity.class);
        startActivity(intent);

    }

    public void NaviPreference()
    {
        routePlan = (RelativeLayout) findViewById(R.id.baiduNavi_route_plan_preference);
        ImageView pre_arrow = (ImageView) findViewById(R.id.general_header_titlebar).findViewById(R.id.iv_preference_arrow);
        if (View.GONE == routePlan.getVisibility())
        {
            routePlan.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.preference_down);
            animation.setFillAfter(true);
            pre_arrow.startAnimation(animation);

            showNaviPreference();
        }
        else
        {
            routePlan.setVisibility(View.GONE);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.preference_up);
            animation.setFillAfter(true);
            pre_arrow.startAnimation(animation);
        }
    }

    //    public void changePreferenceState()
    //    {
    //
    //        routePlan = (RelativeLayout) findViewById(R.id.baiduNavi_route_plan_preference);
    //        ImageView pre_arrow = (ImageView) findViewById(R.id.general_header_titlebar).findViewById(R.id.iv_preference_arrow);
    //
    //        if (View.GONE == routePlan.getVisibility())
    //        {
    //            routePlan.setVisibility(View.VISIBLE);
    //
    //            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.preference_down);
    //            animation.setFillAfter(true);
    //            pre_arrow.startAnimation(animation);
    //
    //            showNaviPreference();
    //        }
    //        else
    //        {
    //            routePlan.setVisibility(View.GONE);
    //
    //            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.preference_up);
    //            animation.setFillAfter(true);
    //            pre_arrow.startAnimation(animation);
    //        }
    //
    //    }

    /**
     * 显示地址家、公司
     */
    public void showAddress()
    {
        ImageView address1_iv = (ImageView) findViewById(R.id.iv_navi_address1);
        TextView address1_tv = (TextView) findViewById(R.id.tv_navi_address1);
        if (null != address_home)
        {
            address1_iv.setBackground(getResources().getDrawable(R.drawable.collection_site_1_select));
            if (null != address_home.getName())
            {
                address1_tv.setText(address_home.getName());
            }
        }
        else
        {
            address1_iv.setBackground(getResources().getDrawable(R.drawable.collection_site_1_unselect));
            address1_tv.setText(getResources().getString(R.string.collection_address));
        }

        ImageView address2_iv = (ImageView) findViewById(R.id.iv_navi_address2);
        TextView address2_tv = (TextView) findViewById(R.id.tv_navi_address2);
        if (null != address_company)
        {
            address2_iv.setBackground(getResources().getDrawable(R.drawable.collection_site_2_select));
            if (null != address_company.getName())
            {
                address2_tv.setText(address_company.getName());
            }
        }
        else
        {
            address2_iv.setBackground(getResources().getDrawable(R.drawable.collection_site_2_unselect));
            address2_tv.setText(getResources().getString(R.string.collection_address));
        }
    }

    /**
     * 获得家庭、公司地址
     */
    public void getHomeAndCompany()
    {
        address_home = AddressInfos.getPoiAddress(FoPreference.getString(FoConstants.HOME));
        address_company = AddressInfos.getPoiAddress(FoPreference.getString(FoConstants.COMPANY));

        //设置Textiew显示地址
        showAddress();
    }

    public void naviBack(View view)
    {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        showSelect.setVisibility(View.GONE);
        return super.onTouchEvent(event);
    }

    @Override
    public void onPause()
    {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        mMapView.onResume();
        //        showNaviLike();
        //获得家庭、公司地址
        getHomeAndCompany();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        //退出导航功能时停止定位
        if (null != location)
        {
            location.destroyLocation();
        }

        super.onDestroy();
    }

}
