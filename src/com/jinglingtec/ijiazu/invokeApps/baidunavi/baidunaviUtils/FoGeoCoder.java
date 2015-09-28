package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.content.Context;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;

/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class FoGeoCoder implements OnGetGeoCoderResultListener
{
    private Context context;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private String address = null;
    private String name = null;
    private double lat;//纬度
    private double lon;//经度
    private String mark = null;

    //    public FoGeoCoder(String mark,String name, String address)
    //    {
    //        this.mark = mark;
    //        this.name = name;
    //        this.address = address;
    //        // 初始化搜索模块，注册事件监听
    //        mSearch = GeoCoder.newInstance();
    //        mSearch.setOnGetGeoCodeResultListener(this);
    //    }

    public FoGeoCoder()
    {

    }

    /**
     * 用于保存地址，开始地址、家、公司
     */
    public FoGeoCoder(Context context, String mark, double lat, double lon)
    {
        this.context = context;
        this.mark = mark;
        this.lat = lat;
        this.lon = lon;
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    /**
     * 翼卡
     */
    public FoGeoCoder(Context context, double lat, double lon)
    {
        this.context = context;
        this.lat = lat;
        this.lon = lon;
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    //    /**
    //     * 发起搜索
    //     */
    //    public void SearchProcess()
    //    {
    //
    //        // Geo搜索
    //        mSearch.geocode(new GeoCodeOption().city("北京").address(address));
    //
    //    }

    public void reverseSearchProcess()
    {

        LatLng ptCenter = new LatLng(lat, lon);
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result)
    {
        //        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
        //        {
        //            return;
        //        }
        //
        //        Address addr = new Address();
        //        addr.setAddress(result.getAddress());
        //        addr.setLatitude(result.getLocation().latitude);
        //        addr.setLongitude(result.getLocation().longitude);
        //
        //        Log.e("homeorcompanyaddress",new Gson().toJson(addr));
        //        //去保存
        //        SaveAddress.save(mark, addr);
    }

    @Override
    //反Geo地理编码
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result)
    {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
        {
            return;
        }

        if (null != result)
        {
            Address addressSave = new Address();
            addressSave.setName(result.getAddress());
            addressSave.setAddress(result.getAddress());
            addressSave.setLatitude(result.getLocation().latitude);
            addressSave.setLongitude(result.getLocation().longitude);

            SaveAddress saveAddress = new SaveAddress();
            saveAddress.save(mark, addressSave);

            if (null != geoCoderResult)
            {
                //                Toast.makeText(context,"getcoder",Toast.LENGTH_SHORT).show();
                geoCoderResult.onGetReverseGeoCodeSuccess(addressSave);
            }


        }

    }

    //回调函数
    public void setLocationListener(GeoCoderSuccess result)
    {
        geoCoderResult = result;
    }

    private GeoCoderSuccess geoCoderResult = null;


    public interface GeoCoderSuccess
    {
        public void onGetReverseGeoCodeSuccess(Address result);
    }

}

