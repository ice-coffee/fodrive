package com.jinglingtec.ijiazu.util.http;

// import java.io.Serializable;

public class HttpAsyncResponse //implements Serializable
{
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAILED = 0;

    private int code = CODE_SUCCESS;
    private String value = null;
    // private byte[] b = null;

    public boolean isSuccess()
    {
        return (CODE_SUCCESS == code);
    }

    public void setFailed()
    {
        this.code = CODE_FAILED;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String v)
    {
        this.value = v;
    }

    // public byte[] getB()
    // {
    //     return b;
    // }

    // public void setB(byte[] b)
    // {
    //     this.b = b;
    // }

}
