package com.jinglingtec.ijiazu.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import java.util.Map;

/**
 * 获取联系人数据
 */
public class FoContacts
{
    private Context context;
    /**
     * 获取库Phon表字段*
     */
    private static final String[] PHONES_PROJECTION = new String[]{Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

    /**
     * 联系人显示名称*
     */
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码*
     */
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 返回字段*
     */
    private Map<String, String> contacts = null;

    private String TAG = "catchcontacts";

    public FoContacts(Context context)
    {
        this.context = context;
    }

    public String getPhoneContacts(String phone)
    {

        if (null == context || null == phone)
        {
            return null;
        }

        ContentResolver resolver = context.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                {
                    continue;
                }

                String strn1 = phoneNumber.replace(" ", "").replace("-", "");
                String strn2 = phone.replace(" ", "").replace("-", "");


                if (strn1.equals(strn2))
                {
                    //得到联系人名称
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    return contactName;
                }
            }
            phoneCursor.close();
        }
        return null;
    }
}
