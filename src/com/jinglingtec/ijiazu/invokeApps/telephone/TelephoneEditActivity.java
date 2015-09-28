package com.jinglingtec.ijiazu.invokeApps.telephone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoUtil;

/**
 * Created by coffee on 14-12-2.
 */
public class TelephoneEditActivity extends BaseActivity
{
    private final int PICKUP_CONTACT = 0x1021;

    private EditText et_phoneName;
    private EditText et_phoneNumber;

    //    private int MAX_ITEMS = 1;
    //    private ContactsItem[] defaultContact = null;//new ContactsItem[MAX_ITEMS];

    /**
     * 是否是硬件点击进来的
     */
    private boolean isSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "11111111");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "22222222222");
        setContentView(R.layout.telephone_edit_activity);
        Log.d(TAG, "3333333333333");
        init();
        Log.d(TAG, "44444444444444");
    }

    // initialize this activity
    public void init()
    {
        et_phoneName = (EditText) this.findViewById(R.id.et_col_phone_name);
        et_phoneNumber = (EditText) this.findViewById(R.id.et_col_phone_number);

        setHeaderLeftBtn();
        setTitleText(R.string.write_telephone);

        setHeaderRightBtnDrawable(R.drawable.phone_book_icon, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // launch the system contacts application
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICKUP_CONTACT);
            }
        });

        Intent intent = getIntent();
        if (null == intent)
        {
            return;
        }

        String name = intent.getStringExtra(FoConstants.TELEPHONE_NAME);
        String number = intent.getStringExtra(FoConstants.TELEPHONE_NUMBER);
        isSetting = intent.getBooleanExtra(FoConstants.TELEPHONE_SETTING, false);

        if ((null != name) && (name.length() > 0))
        {
            et_phoneName.setText(name);
        }

        if ((null != number) && (number.length() > 0))
        {
            et_phoneNumber.setText(number);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((RESULT_OK != resultCode) || (PICKUP_CONTACT != requestCode) || (null == data))
        {
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Uri contactData = data.getData();

        Cursor cursor = managedQuery(contactData, null, null, null, null);
        if ((null == cursor) || !cursor.moveToFirst())
        {
            return;
        }
        String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        if (null == phone)
        {
            return;
        }

        String usernumber = null;
        final int idxNumber = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        while (phone.moveToNext())
        {
            usernumber = phone.getString(idxNumber);
            if ((usernumber != null) && (usernumber.length() > 1))
            {
                break;
            }
        }

        et_phoneName.setText(username);
        et_phoneNumber.setText(usernumber);
    }

    private void onContactChanged(String name, String number)
    {
        final int MAX_ITEMS = 1;

        et_phoneName.setText(name);
        et_phoneNumber.setText(number);

        ContactsItem[] defaultContact = new ContactsItem[MAX_ITEMS];
        defaultContact[0] = new ContactsItem();

        defaultContact[0].Name = name;
        defaultContact[0].Number = number;

        PhoneLocalStorage.saveContactItems(defaultContact);
        FoUtil.toast(this, R.string.set_phone_success);
    }

    // save contacts item to cache
    //    private void saveContactsItem2Cache()
    //    {
    //        PhoneLocalStorage.saveContactItems(this.defaultContact);
    //        FoUtil.toast(this, R.string.set_phone_success);
    //    }

    public void editTelephone(View view)
    {
        String name = et_phoneName.getText().toString();
        String number = et_phoneNumber.getText().toString();

        if ((name == null) || (name.length() < 1))
        {
            FoUtil.toast(this, R.string.invalideNickName);
            return;
        }
        if (number == null)
        {
            FoUtil.toast(this, R.string.invalideMobile);
            return;
        }

        if (isSetting)
        {
            onContactChanged(name, number);
            finish();
        }
        else
        {
            Intent data = new Intent();
            data.putExtra(FoConstants.TELEPHONE_NAME, name);
            data.putExtra(FoConstants.TELEPHONE_NUMBER, number);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
