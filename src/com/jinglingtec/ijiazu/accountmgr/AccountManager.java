package com.jinglingtec.ijiazu.accountmgr;

import com.google.gson.Gson;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;

public class AccountManager
{
    // save account information to preference
    public static void saveAccountInfo(AccountInfo accountInfo)
    {
        if (null != accountInfo)
        {
            try
            {
                Gson gson = new Gson();
                String aInfo = gson.toJson(accountInfo);
                if ((null != aInfo) && (aInfo.length() > 0))
                {
                    FoPreference.putString(FoConstants.ACCOUNT_INFO, aInfo);
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

    // load account information from the local preference
    public static AccountInfo loadAccountInfo()
    {
        try
        {
            String aInfo = FoPreference.getString(FoConstants.ACCOUNT_INFO);
            if ((null != aInfo) && (aInfo.length() > 0))
            {
                Gson gson = new Gson();
                AccountInfo accountInfo = gson.fromJson(aInfo, AccountInfo.class);
                return accountInfo;
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
