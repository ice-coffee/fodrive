package com.fodrive.android.BleEventAdapter;

import com.fodrive.squareup.otto.Bus;
import com.fodrive.squareup.otto.ThreadEnforcer;

/**
 * @author leequer
 */
public class BleEventBus extends Bus
{
    private static BleEventBus ourInstance = new BleEventBus(ThreadEnforcer.ANY);

    public static BleEventBus getInstance()
    {
        return ourInstance;
    }

    public BleEventBus(ThreadEnforcer enforcer)
    {
        super(enforcer, "Indy-Ble-LowLevel");
    }
}
