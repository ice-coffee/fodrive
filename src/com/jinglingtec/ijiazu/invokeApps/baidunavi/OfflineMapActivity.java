package com.jinglingtec.ijiazu.invokeApps.baidunavi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils.OfflineMap;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coffee on 14-11-26.
 */
public class OfflineMapActivity extends BaseActivity implements MKOfflineMapListener
{

    private MKOfflineMap mOffline = null;

    private ArrayList<MKOLSearchRecord> allCityList;
    private ArrayList<MKOLSearchRecord> hotCityList;
    private ArrayList<MKOLSearchRecord> bigCityList;

    private Map hotCityPosition;
    private Map bigCityPosition;
    private Map allCityPosition;

    /**
     * 已下载的地图ID
     */
    private ArrayList<Integer> localCityIdList = null;

    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;

    private ListView hotmap;


    private int allTheSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidunavi_offline_map);

        mOffline = new MKOfflineMap();
        mOffline.init(this);

        hotmap = (ListView) findViewById(R.id.lv_hot_map);

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();

        if (localMapList == null)
        {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }

        //已经下载的地图ID
        localCityIdList = new ArrayList<Integer>();
        if (null != localMapList)
        {
            for (MKOLUpdateElement item : localMapList)
            {
                if (item != null)
                {
                    localCityIdList.add(item.cityID);
                }
            }
        }

        setTitleText(R.string.offline_map);
        setHeaderLeftBtn();

        //所有
        allCityList = mOffline.getOfflineCityList();
        hotCityList = new ArrayList<MKOLSearchRecord>();
        bigCityList = new ArrayList<MKOLSearchRecord>();

        hotCityPosition = new HashMap();
        bigCityPosition = new HashMap();
        allCityPosition = new HashMap();
        for (int i = 0; i < allCityList.size(); i++)
        {
            MKOLSearchRecord searchRecord = allCityList.get(i);

            if (searchRecord.cityType == 1)
            {
                bigCityList.add(searchRecord);
                bigCityPosition.put(searchRecord.cityID, i);
            }
            else
            {
                hotCityList.add(searchRecord);
                hotCityPosition.put(searchRecord.cityID, i);
            }
        }

        for (int i = 0; i < bigCityList.size(); i++)
        {
            ArrayList<MKOLSearchRecord> searchRecord = bigCityList.get(i).childCities;
            int position = (Integer) bigCityPosition.get(bigCityList.get(i).cityID);
            for (int j = 0; j < searchRecord.size(); j++)
            {
                MKOLSearchRecord record = searchRecord.get(j);
                allCityPosition.put(record.cityID, position);
            }
        }

        /**
         *cityType: 0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表
         */

        hotmap.setAdapter(new OfflineCityMapAdapter());

        hotmap.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if ((position < 0) || (position >= allCityList.size()))
                {
                    return;
                }
                if (null != allCityList)
                {

                    MKOLSearchRecord searchRecord = allCityList.get(position);

                    int loadCityId = searchRecord.cityID;

                    start(loadCityId);

                }

            }
        });

    }

    @Override
    public void onGetOfflineMapState(int type, int state)
    {
        switch (type)
        {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
            {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null)
                {
                    updateView(update);
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
        }

    }

    private void updateView(MKOLUpdateElement update)
    {
        int position = 0;
        MKOLSearchRecord searchRecord = null;

        //判断是否是省级单位
        if (hotCityPosition.containsKey(update.cityID))
        {
            position = (Integer) hotCityPosition.get(update.cityID);
        }
        else
        {
            position = (Integer) allCityPosition.get(update.cityID);
            searchRecord = allCityList.get(position);
        }

        int visiblePos = hotmap.getFirstVisiblePosition();
        int offset = position - visiblePos;
        if (offset < 0)
        {
            return;
        }
        View view = hotmap.getChildAt(offset);
        if (null == view)
        {
            return;
        }

        OfflineMapViewHolder holder = (OfflineMapViewHolder) view.getTag();
        if (null == holder)
        {
            return;
        }

        //下载出现问题的情况下删除原先错误的城市地图数据，重新下载
        switch (update.status)
        {
            case MKOLUpdateElement.eOLDSIOError:
                remove(update.cityID);
                start(update.cityID);
                return;
            case MKOLUpdateElement.eOLDSMd5Error:
                break;
            case MKOLUpdateElement.eOLDSMissData:
                remove(update.cityID);
                start(update.cityID);
                return;
            case MKOLUpdateElement.eOLDSNetError:
                break;
            case MKOLUpdateElement.eOLDSWifiError:
                break;
        }

        //如果是热门城市按照ratio进行控件更新
        if (hotCityPosition.containsKey(update.cityID))
        {
            int clrDownloading = getResources().getColor(R.color.load_color);
            holder.citySize.setTextColor(clrDownloading);
            holder.loadding.setText("正在下载:" + update.ratio + "%");
            holder.loadding.setTextColor(clrDownloading);

            if (update.ratio == 100)
            {
                int clrDownloaded = getResources().getColor(R.color.collection_color);
                holder.citySize.setTextColor(clrDownloaded);
                holder.loadding.setText("已下载");
                holder.loadding.setTextColor(clrDownloaded);
            }

            holder.ivcityMap.setBackgroundResource(R.drawable.delete_button);
            return;
        }

        String bf = null;
        OfflineMap map = new OfflineMap();

        //本地保存下载信息
        String localmapinfo = FoPreference.getString(searchRecord.cityName);

        if (null != localmapinfo)
        {
            map = new Gson().fromJson(localmapinfo, OfflineMap.class);
        }
        else
        {
            map.setCityName(update.cityName);
            map.setAlllodesize(0);
        }

        //已下载地图数据加本次下载数据和省级地图总数据的百分比
        bf = getPercent((update.size + map.getAlllodesize()), searchRecord.size);

        if (Double.valueOf(bf) < 0)
        {
            return;
        }

        ArrayList<MKOLSearchRecord> records = searchRecord.childCities;
        if (records.get(records.size() - 1).cityID == update.cityID)
        {
            if (update.ratio == 100)
            {
                updateOfflineMapViewHolder(holder, 100);

                map.setBaifen("100");
                FoPreference.putString(searchRecord.cityName, new Gson().toJson(map));

                return;
            }
        }

        if (Double.valueOf(bf) >= 100)
        {

            if (update.ratio == 100)
            {
                map.setBaifen("100");
                FoPreference.putString(searchRecord.cityName, new Gson().toJson(map));
            }

        }
        else
        {
            if (update.ratio == 100)
            {
                map.setAlllodesize(map.getAlllodesize() + update.size);
            }

            int clrDownloading = getResources().getColor(R.color.load_color);
            holder.citySize.setTextColor(clrDownloading);
            holder.loadding.setText("正在下载:" + bf + "%");
            holder.loadding.setTextColor(clrDownloading);

            map.setBaifen(bf);
            FoPreference.putString(searchRecord.cityName, new Gson().toJson(map));
        }

        holder.ivcityMap.setBackgroundResource(R.drawable.delete_button);
    }

    // update a specific view holder
    private void updateOfflineMapViewHolder(OfflineMapViewHolder holder, int downloadRatio)
    {
        if (downloadRatio < 100)
        {
            // during downloading
            int clrDownloading = getResources().getColor(R.color.load_color);
            holder.citySize.setTextColor(clrDownloading);
            holder.loadding.setText("正在下载:" + downloadRatio + "%");
            holder.loadding.setTextColor(clrDownloading);
        }
        else
        {
            // download finished
            int clrDownloaded = getResources().getColor(R.color.collection_color);
            holder.citySize.setTextColor(clrDownloaded);
            holder.loadding.setText("已下载");
            holder.loadding.setTextColor(clrDownloaded);
        }
        holder.ivcityMap.setBackgroundResource(R.drawable.delete_button);
    }

    // stop a specific view holder
    private void stopOfflineMapViewHolder(OfflineMapViewHolder holder, int downloadRatio)
    {
        if (downloadRatio < 100)
        {
            // during downloading
            int clrDownloading = getResources().getColor(R.color.load_color);
            holder.citySize.setTextColor(clrDownloading);
            holder.loadding.setText("已暂停:" + downloadRatio + "%");
            holder.loadding.setTextColor(clrDownloading);
            holder.ivcityMap.setBackgroundResource(R.drawable.download_button);
        }
        else
        {
            // download finished
            int clrDownloaded = getResources().getColor(R.color.collection_color);
            holder.citySize.setTextColor(clrDownloaded);
            holder.loadding.setText("已下载");
            holder.loadding.setTextColor(clrDownloaded);
            holder.ivcityMap.setBackgroundResource(R.drawable.delete_button);
        }

    }

    public String getPercent(int num1, int num2)
    {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result;
    }

    public String formatDataSize(int size)
    {
        String ret = "";
        if (size < (1024 * 1024))
        {
            ret = String.format("%dK", size / 1024);
        }
        else
        {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    /**
     * 开始下载
     */
    public void start(int cityid)
    {
        mOffline.start(cityid);
    }

    /**
     * 暂停下载
     */
    public void stop(int cityid)
    {
        mOffline.pause(cityid);
    }

    /**
     * 删除离线地图
     */
    public void remove(int cityid)
    {
        mOffline.remove(cityid);
    }

    /**
     * 删除离线地图
     */
    private void removeCity(final int cityId, final View itemViewOfList)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setIconAttribute(android.R.attr.dialogIcon);

        final String from = getResources().getString(R.string.app_name);
        final String message = getResources().getString(R.string.confirm_remove_city_map);
        builder.setMessage(message);
        builder.setTitle(from);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mOffline.remove(cityId);
                localCityIdList.remove(new Integer(cityId));
                OfflineMapViewHolder holder = (OfflineMapViewHolder) itemViewOfList.getTag();
                updateViewHolder2Default(holder, getPositionByCityID(cityId));
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    // get element index in the all offlineMap
    private int getPositionByCityID(int cityID)
    {
        int index = -1;
        for (int i = this.allCityList.size() - 1; i > -1; i--)
        {
            MKOLSearchRecord item = this.allCityList.get(i);
            if ((null != item) && (cityID == item.cityID))
            {
                return i;
            }
        }
        return index;
    }


    // update a specific view holder to default
    private void updateViewHolder2Default(OfflineMapViewHolder holder, int position)
    {
        MKOLSearchRecord searchRecord = allCityList.get(position);
        if ((null == searchRecord) || (null == holder))
        {
            return;
        }

        holder.cityName.setText(searchRecord.cityName);
        double size = searchRecord.size;
        String strCitySize = String.format("%.2f", size / (1024 * 1024)) + "M";
        holder.citySize.setText(strCitySize);
        holder.citySize.setTextColor(getResources().getColor(R.color.black));
        holder.loadding.setText("");
        holder.ivcityMap.setBackgroundResource(R.drawable.download_button);
    }

    //查看是否正停止下载
    public boolean suspended(MKOLSearchRecord searchRecord)
    {
        if (null == searchRecord)
        {
            return false;
        }
        ArrayList<MKOLSearchRecord> cityList = searchRecord.childCities;
        for (int j = 0; j < cityList.size(); j++)
        {
            MKOLSearchRecord city = cityList.get(j);
            MKOLUpdateElement updateCity = mOffline.getUpdateInfo(city.cityID);
            if (MKOLUpdateElement.SUSPENDED == updateCity.status)
            {
                return true;
            }
        }

        return false;
    }

    // offlien map adapter
    private class OfflineCityMapAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return allCityList.size();
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            if ((position < 0) || (position >= allCityList.size()))
            {
                return convertView;
            }

            final MKOLSearchRecord searchRecord = allCityList.get(position);
            if (null == searchRecord)
            {
                return convertView;
            }

            final OfflineMapViewHolder holder;
            if (null == convertView)
            {
                holder = new OfflineMapViewHolder();

                convertView = View.inflate(OfflineMapActivity.this, R.layout.offline_map_item, null);

                holder.cityName = (TextView) convertView.findViewById(R.id.tv_ol_city_name);
                holder.ivcityMap = (ImageView) convertView.findViewById(R.id.iv_ol_control);

                holder.citySize = (TextView) convertView.findViewById(R.id.tv_ol_size);
                holder.loadding = (TextView) convertView.findViewById(R.id.tv_ol_loadding);

                convertView.setTag(holder);
            }
            else
            {
                holder = (OfflineMapViewHolder) convertView.getTag();
            }

            holder.cityName.setText(searchRecord.cityName);
            double size = searchRecord.size;
            String strCitySize = String.format("%.2f", size / (1024 * 1024)) + "M";
            holder.citySize.setText(strCitySize);

            if (localCityIdList.contains(searchRecord.cityID))
            {
                MKOLUpdateElement element = mOffline.getUpdateInfo(searchRecord.cityID);
                if (null != element)
                {
                    if (MKOLUpdateElement.SUSPENDED == element.status)
                    {
                        stopOfflineMapViewHolder(holder, element.ratio);
                    }
                    else
                    {
                        updateOfflineMapViewHolder(holder, element.ratio);
                    }
                }
            }
            else
            {
                String downStr = FoPreference.getString(searchRecord.cityName);

                if (null != downStr)
                {
                    OfflineMap offlineMap = new Gson().fromJson(downStr, OfflineMap.class);

                    if (suspended(searchRecord))
                    {
                        stopOfflineMapViewHolder(holder, Integer.valueOf(offlineMap.getBaifen()));
                    }
                    else
                    {
                        updateOfflineMapViewHolder(holder, Integer.valueOf(offlineMap.getBaifen()));
                    }

                }
                else
                {
                    holder.citySize.setTextColor(getResources().getColor(R.color.black));
                    holder.loadding.setText("");
                    holder.ivcityMap.setBackgroundResource(R.drawable.download_button);
                }
            }

            final View view = convertView;

            holder.ivcityMap.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (searchRecord.cityType != 1)
                    {
                        MKOLUpdateElement element = mOffline.getUpdateInfo(searchRecord.cityID);
                        if ((null != element) && (element.ratio > 99))
                        {
                            // download finished, remove it
                            removeCity(searchRecord.cityID, view);
                            return;
                        }

                        if ((null != element) && (MKOLUpdateElement.DOWNLOADING == element.status))
                        {
                            mOffline.pause(searchRecord.cityID);
                            return;
                        }

                    }
                    else
                    {
                        String downStr = FoPreference.getString(searchRecord.cityName);

                        if (null != downStr)
                        {
                            OfflineMap offlineMap = new Gson().fromJson(downStr, OfflineMap.class);

                            if (Integer.valueOf(offlineMap.getBaifen()) == 100)
                            {
                                removeCity(searchRecord.cityID, view);
                                SharedPreferences.Editor editor = FoPreference.getPreference().edit();
                                editor.remove(searchRecord.cityName);
                                editor.commit();
                            }
                        }
                    }
                }
            });

            return convertView;

        }
    }

    //
    private class OfflineMapViewHolder
    {
        TextView cityName;
        ImageView ivcityMap;

        TextView citySize;
        TextView loadding;
    }

    @Override
    protected void onDestroy()
    {
        mOffline.destroy();
        super.onDestroy();
    }
}
