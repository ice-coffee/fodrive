package com.jinglingtec.ijiazu.invokeApps;

import android.content.Context;

public interface MusicAdapter
{
    void initialize(Context ctx);

    void release(Context ctx);

    void singleClick(Context ctx);

    void doubleClick(Context ctx);

    void longPressed(Context ctx);
}
