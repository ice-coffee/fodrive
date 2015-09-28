package com.jinglingtec.ijiazu.invokeApps.fm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coffee on 15-1-20.
 */
public class FCListActivity extends BaseActivity
{

    private String fmOrMusic = null;
    private ListView fc_list;

    private String appStr = null;

    private String[] apps = null;
    private String[] appNames = null;
    private int[] appImages = null;

    private List<String> appList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fc_list);

        init();

        setHeaderLeftBtn();
        if (FoConstants.MUSIC.equals(fmOrMusic))
        {
            setTitleText(R.string.music);
            apps = FCMessage.musicApps;
            appNames = FCMessage.musicAppsName;
            appImages = FCMessage.musicImage;
        }
        if (FoConstants.FM.equals(fmOrMusic))
        {
            setTitleText(R.string.fm);
            apps = FCMessage.fmApps;
            appNames = FCMessage.fmAppsName;
            appImages = FCMessage.fmImage;
        }

        //        if (null != apps)
        //        {
        //
        //            for (int i = 0; i < apps.length; i++)
        //            {
        //                appList.add(apps[i]);
        //            }
        //        }

        fc_list.setAdapter(new MyFCAdapter());
        fc_list.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                appList.add(apps[position]);

            }
        });
    }

    public void init()
    {
        //返回键
        setHeaderLeftBtn();
        setTitleText(R.string.fm);

        fc_list = (ListView) findViewById(R.id.lv_fc_list);

        Intent intent = getIntent();
        if (null != intent)
        {
            fmOrMusic = intent.getStringExtra(FoConstants.FC);
        }

    }

    public class MyFCAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return apps.length;
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
            ViewHolder holder = null;
            if (null == convertView)
            {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.fm_music_item_activity, null);
                holder.fc_logo = (ImageView) convertView.findViewById(R.id.tv_fc_logo);
                holder.fc_name = (TextView) convertView.findViewById(R.id.tv_fc_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if (null == apps)
            {
                return null;
            }

            holder.fc_logo.setBackgroundResource(appImages[position]);
            holder.fc_name.setText(appNames[position]);
            return convertView;
        }
    }

    class ViewHolder
    {
        ImageView fc_logo;
        TextView fc_name;
    }

    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra(FoConstants.FC, new Gson().toJson(appList, List.class));
        setResult(0, intent);
        finish();
    }

}
