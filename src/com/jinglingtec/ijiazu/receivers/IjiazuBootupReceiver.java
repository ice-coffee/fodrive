package com.jinglingtec.ijiazu.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jinglingtec.ijiazu.services.IjiazuService;
import com.jinglingtec.ijiazu.util.FoLogger;

public class IjiazuBootupReceiver extends BroadcastReceiver
{
    public void onReceive(Context ctx, Intent arg1)
    {
        FoLogger.addLog("IjiazuBootupReceiver onReceive");
        Intent intent = new Intent(ctx, IjiazuService.class);
        ctx.startService(intent);
    }
}
