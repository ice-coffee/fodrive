package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by coffee on 14-10-30.
 */
public class Address implements Serializable
{
    private String address;
    private String name;
    private double latitude;
    private double longitude;

    //    private Address(Parcel parcel){
    //        address = parcel.readString();
    //        name = parcel.readString();
    //        latitude = parcel.readDouble();
    //        longitude = parcel.readDouble();
    //    }

    public String getAddress()
    {
        return address;
    }

    public String getName()
    {
        return name;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

}
