package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import com.google.gson.Gson;
import com.jinglingtec.ijiazu.util.FoPreference;

/**
 * 保存地址
 */
public class SaveAddress
{

    public void save(String mode, Address addr)
    {
        Gson gson = new Gson();
        FoPreference.putString(mode, gson.toJson(addr));

    }

}
