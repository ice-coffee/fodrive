package com.jinglingtec.ijiazu.util.http;

// import java.io.Serializable;

public class BaseHttpFeedback
{
    public int ErrCode = -1;
    public String Info = null;

    public boolean isSuccess()
    {
        return (HttpConst.ERR_SUCCESS == this.ErrCode);
    }
}

