package com.jinglingtec.ijiazu.invokeApps;

import android.content.Context;

public interface NavigatorAdapter
{

    void singleClick(Context ctx);

    void doubleClick(Context ctx);

    void longPressed(Context ctx);

    void release();
}

