package com.jinglingtec.ijiazu.invokeApps.telephone;

import android.content.Context;

public interface PhoneCall
{
    void singleClicked(Context ctx);

    void doubleClicked(Context ctx);

    void longPressed(Context ctx);

    void initialize(Context ctx);

    void release();

    boolean isIdleState();

    void onBleStateChanged(boolean newState);
}
