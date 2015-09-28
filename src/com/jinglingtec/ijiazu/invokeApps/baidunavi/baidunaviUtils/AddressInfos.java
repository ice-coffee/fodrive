package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.util.Log;
import com.google.gson.Gson;

/**
 * 分解s保存的地址信息
 */
public class AddressInfos
{
    private static Address addrs;

    public static Address getPoiAddress(String addressInfo)
    {
        try
        {
            Gson gson = new Gson();
            addrs = gson.fromJson(addressInfo, Address.class);
            return addrs;
        } catch (Exception e)
        {
            Log.e("GsonException", e.getMessage());
        }
        return null;
    }

}
