package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.*;
import com.baidu.mapapi.search.sug.SuggestionResult;

/**
 * Created by coffee on 14-12-25.
 */
public class PoiSearch implements OnGetPoiSearchResultListener
{
    private static com.baidu.mapapi.search.poi.PoiSearch mPoiSearch = null;
    private static PoiResult poiResult;

    private static String address = null;

    public PoiSearch(Context context, String str)
    {
        address = str;

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = com.baidu.mapapi.search.poi.PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

    }

    /**
     * 影响搜索按钮点击事件
     */
    public static void searchButtonProcess()
    {

        mPoiSearch.searchInCity((new PoiCitySearchOption()).city("北京").keyword(address).pageNum(0).pageCapacity(20));
    }

    //    public void goToNextPage(View v) {
    //        load_Index++;
    //        searchButtonProcess(null);
    //    }

    public void onGetPoiResult(PoiResult result)
    {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)
        {

            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR)
        {

            poiResult = result;
            return;

        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD)
        {

        }
    }

    //
    public void onGetPoiDetailResult(PoiDetailResult result)
    {
        if (result.error != SearchResult.ERRORNO.NO_ERROR)
        {

        }
        else
        {
            //            poiResult = result;
        }
    }


    //    private class MyPoiOverlay extends PoiOverlay {
    //
    //        public MyPoiOverlay(BaiduMap baiduMap) {
    //            super(baiduMap);
    //        }
    //
    //        @Override
    //        public boolean onPoiClick(int index) {
    //            super.onPoiClick(index);
    //            PoiInfo poi = getPoiResult().getAllPoi().get(index);
    //            // if (poi.hasCaterDetails) {
    //            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
    //                    .poiUid(poi.uid));
    //            // }
    //            return true;
    //        }
    //
    //        @Override
    //        public PoiResult getPoiResult()
    //        {
    //            return poiResult;
    //        }
    //    }

}
