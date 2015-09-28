package com.jinglingtec.ijiazu.invokeApps.fokey;

import android.content.Context;
import android.content.Intent;
import com.jinglingtec.ijiazu.activity.IjiazuActivity;
import com.jinglingtec.ijiazu.invokeApps.FokeyAdapter;
import com.jinglingtec.ijiazu.util.FoConstants;

public class FokeyImpl implements FokeyAdapter
{
    public void initialize(Context ctx)
    {

    }

    public void release(Context ctx)
    {

    }

    public void singleClick(Context ctx)
    {
        //        Intent intent = new Intent(ctx, IjiazuActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        intent.putExtra(FoConstants.IS_DISABLE_SPLASHSCREEN, true);
        //        ctx.startActivity(intent);
    }

    public void doubleClick(Context ctx)
    {

    }

    public void longPressed(Context ctx)
    {

    }
}
