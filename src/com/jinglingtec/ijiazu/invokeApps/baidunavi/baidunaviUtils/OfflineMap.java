package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

/**
 * Created by coffee on 14-12-19.
 */
public class OfflineMap
{
    public String cityName;
    public int alllodesize;
    public int lodesize;
    public String baifen;
    public int position;

    public int getAlllodesize()
    {
        return alllodesize;
    }

    public String getCityName()
    {
        return cityName;
    }

    public String getBaifen()
    {
        return baifen;
    }

    public void setAlllodesize(int alllodesize)
    {
        this.alllodesize = alllodesize;
    }

    public void setBaifen(String baifen)
    {
        this.baifen = baifen;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getPosition()
    {
        return position;
    }

    public int getLodesize()
    {
        return lodesize;
    }

    public void setLodesize(int lodesize)
    {
        this.lodesize = lodesize;
    }
}
