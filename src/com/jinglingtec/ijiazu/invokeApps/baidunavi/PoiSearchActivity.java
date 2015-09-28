package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.*;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.Address;
import com.jinglingtec.ijiazu.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coffee on 15-1-18.
 */
public class PoiSearchActivity extends BaseActivity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener
{

    // 搜索成功，隐藏用户等待提示窗，并显示搜索结果
    private final int SEARCH_SUCCESS = 1;
    // 开始搜索，显示用户等待提示窗
    private final int SEARCH_START = 2;
    // 无网络状况下提示用户，搜索失败
    private final int SEARCH_NO_NET = 3;
    // 搜索时间过长提示用户，搜索失败
    private final int SEARCH_FAILED = 4;

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorld = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;

    /**
     * 搜索结果
     */
    private List<SearchResult> searchResultList = null;

    /**
     * 搜索建议
     */
    private ListView suggestList = null;

    /**
     * 搜索历史记录
     */
    private ListView historyList = null;
    private List<String> searchHistoryList = null;
    private MyHistoryShowAdapter historyShowAdapter = null;

    /**
     * 搜索建议列表
     */
    private List<String> suggestStrList = null;
    private MySuggestShowAdapter suggestShowAdapter = null;

    //提醒用户动画
    private RelativeLayout search_load;
    private ImageView loadImage;

    private TextView search;

    /**
     * 外地搜索城市列表
     */
    private List<String> strInfo;

    private Handler handlerSearch = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            int updateUi = msg.what;

            int loadState = search_load.getVisibility();
            switch (updateUi)
            {
                case SEARCH_SUCCESS:
                    search_load.setVisibility(View.GONE);
                    loadImage.clearAnimation();
                    break;
                case SEARCH_START:
                    search_load.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(PoiSearchActivity.this, R.anim.loading);
                    loadImage.startAnimation(animation);
                    break;

                case SEARCH_NO_NET:
                    if (View.VISIBLE == loadState)
                    {
                        search_load.setVisibility(View.GONE);
                        loadImage.clearAnimation();
                    }

                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.no_internet));
                    break;
                case SEARCH_FAILED:
                    if (View.VISIBLE == loadState)
                    {
                        search_load.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_baidunavi_poisearch);

        setTitleText(R.string.search);
        setHeaderLeftBtn();

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        keyWorld = (AutoCompleteTextView) findViewById(R.id.et_navi_collection_keyword);
        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        keyWorld.setAdapter(sugAdapter);

        search_load = (RelativeLayout) findViewById(R.id.rl_search_loading);
        search = (TextView) findViewById(R.id.tv_search_start);

        historyList = (ListView) findViewById(R.id.lv_search_history);
        suggestList = (ListView) findViewById(R.id.lv_search_suggest);

        loadImage = (ImageView) findViewById(R.id.iv_search_loading);

        //初始化适配器
        searchHistoryList = new ArrayList<String>();
        historyShowAdapter = new MyHistoryShowAdapter();

        //搜索建议
        suggestStrList = new ArrayList<String>();
        suggestShowAdapter = new MySuggestShowAdapter();

        initClick();

    }

    public void initClick()
    {
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorld.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void afterTextChanged(Editable arg0)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
            {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
            {
                suggestStrList.removeAll(suggestStrList);

                String search_keyword = keyWorld.getText().toString();
                if (search_keyword.length() <= 0)
                {
                    changeState(0);
                    return;
                }

                //改变状态
                changeState(1);

                String city = FoPreference.getString(FoConstants.CITY);

                if (null == city)
                {
                    city = FoConstants.DEFAULT_CITY;
                }
                if (keyWorld != null)
                {
                    /**
                     * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                     */
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(search_keyword.toString()).city(city));
                }
            }
        });

        /**
         *搜索事件
         */
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String keyword_cache = keyWorld.getText().toString();
                if (null != keyword_cache && keyword_cache.length() > 1)
                {
                    //改变搜索
                    searchButtonProcess(keyWorld.getText().toString());
                }
                else
                {
                    FoUtil.toast(getApplicationContext(), getResources().getString(R.string.no_keyword));
                }
            }
        });

        suggestList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (null != suggestStrList)
                {
                    //改变搜索
                    searchButtonProcess(suggestStrList.get(position));
                }
            }
        });

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (null != searchHistoryList)
                {
                    //改变搜索
                    searchButtonProcess(searchHistoryList.get(position));
                }
            }
        });

    }

    public void changeState(int state)
    {
        switch (state)
        {
            case 0:
                historyList.setVisibility(View.VISIBLE);
                suggestList.setVisibility(View.GONE);
                break;

            case 1:
                historyList.setVisibility(View.GONE);
                suggestList.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 影响搜索按钮点击事件
     */
    public void searchButtonProcess(String keyword)
    {
        if (null == mPoiSearch)
        {
            // 初始化搜索模块，注册搜索事件监听
            mPoiSearch = PoiSearch.newInstance();
            mPoiSearch.setOnGetPoiSearchResultListener(this);
        }

        String city = FoPreference.getString(FoConstants.CITY);

        if (null == city)
        {
            city = FoConstants.DEFAULT_CITY;
        }
        if (keyword != null)
        {
            searchAddressByCity(city, keyword);
        }
    }

    public void searchAddressByCity(String city, String keyword)
    {
        load_Index = 0;

        if (!FoNet.getNetState(getApplicationContext()))
        {
            //提示用户等待
            handlerSearch.sendEmptyMessage(SEARCH_NO_NET);
            return;
        }

        //提示用户等待
        handlerSearch.sendEmptyMessage(SEARCH_START);

        boolean searchFailure = mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(keyword).pageNum(load_Index));
        if (!searchFailure)
        {
            handlerSearch.sendEmptyMessage(SEARCH_SUCCESS);
            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.search_failure));
        }
        else
        {

            //10秒后如果还是没有检测到结果那么提示用户
            noSearchRemind();
            //保存搜索记录
            if (null == searchHistoryList)
            {
                return;
            }

            //避免重复记录
            if (searchHistoryList.contains(keyword))
            {
                searchHistoryList.remove(keyword);
            }

            searchHistoryList.add(0, keyword);

            String historyGson = new Gson().toJson(searchHistoryList);

            FoPreference.putString(FoConstants.SEARCH_HISTORY, historyGson);
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

                if (search_load.getVisibility() == View.VISIBLE)
                {
                    handlerSearch.sendEmptyMessage(SEARCH_FAILED);
                }
            }
        });
    }

    public void onGetPoiResult(PoiResult result)
    {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)
        {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR)
        {

            List<PoiInfo> poi_list = result.getAllPoi();
            List<Address> poi_add_list = new ArrayList<Address>();
            for (int i = 0; i < poi_list.size(); i++)
            {
                PoiInfo poiInfo = poi_list.get(i);
                Address addr = new Address();
                addr.setName(poiInfo.name);
                addr.setAddress(poiInfo.address);
                addr.setLatitude(poiInfo.location.latitude);
                addr.setLongitude(poiInfo.location.longitude);

                poi_add_list.add(addr);
            }

            handlerSearch.sendEmptyMessage(SEARCH_SUCCESS);

            Intent intent = new Intent(PoiSearchActivity.this, PoiSearchResultActivity.class);
            intent.putExtra(FoConstants.SEARCH_RESULT, poi_add_list.toArray());
            startActivity(intent);

            finish();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD)
        {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            strInfo = new ArrayList<String>();
            for (CityInfo cityInfo : result.getSuggestCityList())
            {
                strInfo.add(cityInfo.city);
            }

            handlerSearch.sendEmptyMessage(SEARCH_SUCCESS);

            showCityListDialog();
        }

    }

    //显示外城市列表
    public void showCityListDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setContentView(R.layout.activity_search_citylist);

        if (null == strInfo)
        {
            return;
        }

        ListView cityList = (ListView) dialog.findViewById(R.id.lv_search_citylist);
        cityList.setAdapter(new MyCityListAdapter());

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String keyword = keyWorld.getText().toString();
                String city = strInfo.get(position);
                if (null != keyword && null != city)
                {
                    searchAddressByCity(city, keyword);
                }

                dialog.dismiss();
            }
        });

    }

    private class MyHistoryShowAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return searchHistoryList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            HistoryViewHolder holder;
            if (null == convertView)
            {
                holder = new HistoryViewHolder();

                convertView = View.inflate(getApplicationContext(), R.layout.poisearch_suggest_item_activity, null);

                holder.historyName = (TextView) convertView.findViewById(R.id.tv_search_itme_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (HistoryViewHolder) convertView.getTag();
            }

            if (null == searchHistoryList)
            {
                return null;
            }

            String searchHistory = searchHistoryList.get(position);

            holder.historyName.setText(searchHistory);

            return convertView;
        }
    }

    class HistoryViewHolder
    {
        TextView historyName;
    }

    private class MySuggestShowAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return suggestStrList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            SuggestViewHolder holder;
            if (null == convertView)
            {
                holder = new SuggestViewHolder();

                convertView = View.inflate(getApplicationContext(), R.layout.poisearch_suggest_item_activity, null);

                holder.suggestName = (TextView) convertView.findViewById(R.id.tv_search_itme_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (SuggestViewHolder) convertView.getTag();
            }

            if (null == suggestStrList)
            {
                return null;
            }

            String suggestName = suggestStrList.get(position);

            holder.suggestName.setText(suggestName);

            return convertView;
        }
    }

    class SuggestViewHolder
    {
        TextView suggestName;
    }

    //外城市搜索adapter
    private class MyCityListAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return strInfo.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            CityViewHolder holder;
            if (null == convertView)
            {
                holder = new CityViewHolder();

                convertView = View.inflate(getApplicationContext(), R.layout.search_city_item, null);

                holder.cityItem = (TextView) convertView.findViewById(R.id.tv_search_city_item);

                convertView.setTag(holder);
            }
            else
            {
                holder = (CityViewHolder) convertView.getTag();
            }

            if (null == strInfo)
            {
                return null;
            }

            holder.cityItem.setText(strInfo.get(position));
            return convertView;
        }
    }

    class CityViewHolder
    {
        TextView cityItem;
    }

    public void onGetPoiDetailResult(PoiDetailResult result)
    {
        if (result.error != SearchResult.ERRORNO.NO_ERROR)
        {
            //没找到项目
            FoUtil.toast(getApplicationContext(), getResources().getString(R.string.sorry_not_find));
        }
        else
        {
            //找到需要到项目
            FoUtil.toast(getApplicationContext(), result.getName() + ": " + result.getAddress());
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res)
    {
        if (res == null || res.getAllSuggestions() == null)
        {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions())
        {
            if (info.key != null)
            {
                suggestStrList.add(info.key);
            }
        }

        if (null != suggestShowAdapter)
        {
            suggestShowAdapter = new MySuggestShowAdapter();
        }

        if (null != suggestStrList)
        {
            suggestList.setAdapter(suggestShowAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause()
    {
        super.onPause();


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String searchHistory = FoPreference.getString(FoConstants.SEARCH_HISTORY);
        if (null != searchHistory)
        {
            Log.e("searchhistory", searchHistory);
            searchHistoryList = new Gson().fromJson(searchHistory, ArrayList.class);
        }

        if (null != searchHistoryList)
        {
            historyList.setAdapter(historyShowAdapter);
        }
    }

    @Override
    protected void onDestroy()
    {
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

}
