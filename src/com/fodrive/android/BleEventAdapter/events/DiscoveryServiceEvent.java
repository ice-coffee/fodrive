package com.fodrive.android.BleEventAdapter.events;


public class DiscoveryServiceEvent
{

    //    public static final int GATT_DISCOVERING = 1;

    private int mStatus;

    public DiscoveryServiceEvent(int i)
    {
        setmStatus(i);
    }

    @Override
    public String toString()
    {
        return "DiscoveryServiceEvent{" +
                "mStatus=" + getmStatus() +
                '}';
    }

    public int getmStatus()
    {
        return mStatus;
    }

    public void setmStatus(int mStatus)
    {
        this.mStatus = mStatus;
    }
}
