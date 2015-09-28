package com.jinglingtec.ijiazu.invokeApps.telephone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;
import com.jinglingtec.ijiazu.util.TTSPlayerUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 14-10-14.
 */
public class TelephoneActivity extends BaseActivity implements View.OnClickListener
{
    // private final String TAG = "TelephoneActivity";
    // for invoke system activity to pick up contact
    private final int PICKUP_CONTACT = 0x1021;
    private final int EDIT_TELEPHONE = 0;
    private final int EDIT_RESULT = 2;

    // 联系人
    private TextView tele_name;
    // 联系人电话
    private TextView phone_number;
    // 联系人头像
    private ImageView handPhoto;

    private TextView textF;
    private TextView textS;
    private TextView textT;

    //快捷联系人表
    private List<ImageView> hisHandArray = null;
    private List<TextView> textArray = null;

    //是否是编辑操作
    private boolean isEdit = false;

    // 当前选择联系人
    private int currentPhone = 0;

    // 头像Bitmap
    private Bitmap head;
    // sd路径
    private static String path = FoConstants.CONTACT_PATH;
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = FoConstants.CONTACT_IMAGE_FILE_NAME;

    private int MAX_ITEMS = 3;
    private ContactsItem[] defaultContact = null;// new ContactsItem[MAX_ITEMS];

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telephone_activity);

        setTitleText(R.string.telephone);
        setHeaderLeftBtn();

        initialize();

        loadCachedContactsItem();

    }

    // initialize
    private void initialize()
    {
        //列表初始化
        hisHandArray = new ArrayList<ImageView>();
        textArray = new ArrayList<TextView>();

        phone_number = (TextView) findViewById(R.id.tv_tele_phone_number);
        tele_name = (TextView) findViewById(R.id.tv_tele_name);
        handPhoto = (ImageView) findViewById(R.id.iv_tele_hand);
        ImageView hisHandF = (ImageView) findViewById(R.id.iv_phone_hisF);
        ImageView hisHandS = (ImageView) findViewById(R.id.iv_phone_hisS);
        ImageView hisHandT = (ImageView) findViewById(R.id.iv_phone_hisT);

        textF = (TextView) findViewById(R.id.tv_phone_textF);
        textS = (TextView) findViewById(R.id.tv_phone_textS);
        textT = (TextView) findViewById(R.id.tv_phone_textT);
        ImageView iv_call = (ImageView) findViewById(R.id.iv_phone_call);

        //添加数据
        hisHandArray.add(hisHandF);
        hisHandArray.add(hisHandS);
        hisHandArray.add(hisHandT);

        textArray.add(textF);
        textArray.add(textS);
        textArray.add(textT);

        handPhoto.setImageResource(R.drawable.head_portrait_big);
        hisHandF.setImageResource(R.drawable.head_portrait_small);
        hisHandS.setImageResource(R.drawable.head_portrait_small);
        hisHandT.setImageResource(R.drawable.head_portrait_small);

        handPhoto.setOnClickListener(this);
        hisHandF.setOnClickListener(this);
        hisHandS.setOnClickListener(this);
        hisHandT.setOnClickListener(this);
        iv_call.setOnClickListener(this);
        textF.setOnClickListener(this);
        textS.setOnClickListener(this);
        textT.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_tele_hand:
                localPhone();
                break;

            case R.id.iv_phone_hisF:

                showBigHand(0);
                break;

            case R.id.iv_phone_hisS:
                showBigHand(1);
                break;

            case R.id.iv_phone_hisT:
                showBigHand(2);
                break;

            case R.id.tv_phone_textF:
                showBigHand(0);
                break;

            case R.id.tv_phone_textS:
                showBigHand(1);
                break;

            case R.id.tv_phone_textT:
                showBigHand(2);
                break;

            case R.id.iv_phone_call:
                TelephoneCall();
                break;
        }
    }

    // load cached contacts item
    private void loadCachedContactsItem()
    {
        textF.setText("");
        textS.setText("");
        textT.setText("");
        this.defaultContact = PhoneLocalStorage.loadContactItems();

        //如果历史通话记录为空，直接返回。
        if (null == this.defaultContact)
        {
            return;
        }

        //如果数组长度为0，没有内容，不需处理，直接返回
        if (this.defaultContact.length == 0)
        {
            return;
        }

        //
        for (int i = defaultContact.length - 1; i >= 0; i--)
        {
            ContactsItem contactItem = this.defaultContact[i];

            if (null == contactItem)
            {
                if (null != hisHandArray)
                {
                    hisHandArray.get(i).setImageResource(R.drawable.head_portrait_small);
                }

            }
            else
            {

                Bitmap historyHand = getContactPhoto(this.defaultContact[i].Number);
                if (null == historyHand)
                {
                    if (null != hisHandArray)
                    {
                        hisHandArray.get(i).setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.gary_small)));
                    }

                    if (null != textArray)
                    {
                        textArray.get(i).setText(getTheChar(contactItem.Name, i));
                    }

                }
                else
                {
                    if (null != hisHandArray)
                    {
                        hisHandArray.get(i).setImageDrawable(new BitmapDrawable(getResources(), historyHand));
                    }

                }
            }

        }
        phone_number.setText(this.defaultContact[0].Number);
        tele_name.setText(this.defaultContact[0].Name);


    }

    //获取联系人姓名的单个字符
    public String getTheChar(String name, int item)
    {
        if (null == defaultContact || null == name)
        {
            return null;
        }

        List<String> allNameChar = new ArrayList<String>();

        int j = textArray.size() - 1;
        //获取所有显示在电话页的字符
        for (int i = item; i < textArray.size() - 1; i++)
        {
            TextView textView = textArray.get(j--);
            if (null != textView)
            {
                allNameChar.add(textView.getText().toString());
            }

        }

        //进行比对
        for (int i = 0; i < name.length(); i++)
        {
            if (!allNameChar.contains(name.substring(i, i + 1)))
            {
                return name.substring(i, i + 1);
            }
        }

        return name.substring(name.length() - 1, name.length());

    }

    // 控制透明度
    public void setImageAlpha(int show)
    {
        AlphaAnimation alphaDown = new AlphaAnimation(1F, 0.5F);
        alphaDown.setDuration(0); // Make animation instant
        alphaDown.setFillAfter(true); // Tell it to persist after the animation

        AlphaAnimation alphaUp = new AlphaAnimation(0.5F, 1F);
        alphaUp.setDuration(0); // Make animation instant
        alphaUp.setFillAfter(true); // Tell it to persist after the animation

        //设置图片的透明度
        for (int i = 0; i < hisHandArray.size(); i++)
        {
            if (i == show)
            {
                hisHandArray.get(i).startAnimation(alphaUp);
            }
            else
            {
                hisHandArray.get(i).startAnimation(alphaDown);
            }

        }

        //设置文字的透明度
        for (int j = 0; j < textArray.size(); j++)
        {
            if (j == show)
            {
                textArray.get(j).startAnimation(alphaUp);
            }
            else
            {
                textArray.get(j).startAnimation(alphaDown);
            }
        }

    }

    // 展示选择结果
    public void showBigHand(int item)
    {

        if (null == defaultContact)
        {
            return;
        }

        if (item < 0 || item >= defaultContact.length)
        {
            return;
        }

        ContactsItem conItem = defaultContact[item];

        if (null == conItem)
        {
            return;
        }

        // 放到这里，如果选择的联系人是空，那么不保存这次选择
        currentPhone = item;
        saveContactItem();

        Bitmap historyHand = getContactPhoto(conItem.Number);
        if (null == historyHand || null == handPhoto)
        {
            handPhoto.setImageResource(R.drawable.head_portrait_small);
        }
        else
        {
            handPhoto.setImageDrawable(new BitmapDrawable(getResources(), historyHand));
        }

        setImageAlpha(item);

        if (null == phone_number || null == tele_name)
        {
            return;
        }
        phone_number.setText(conItem.Number);
        tele_name.setText(conItem.Name);
    }

    // save contacts item to cache
    private void saveContactsItem2Cache()
    {
        PhoneLocalStorage.saveContactItems(this.defaultContact);
        FoUtil.toast(getApplicationContext(), getResources().getString(R.string.set_phone_success));
        loadCachedContactsItem();
    }

    // the contact changed
    private void onContactChanged(String name, String number)
    {

        if (null == name || null == number)
        {
            return;
        }

        tele_name.setText(name);
        phone_number.setText(number);

        ContactsItem conItem = null;

        if (this.defaultContact == null)
        {
            this.defaultContact = new ContactsItem[MAX_ITEMS];
        }

        int item = 0;
        boolean boolSave = false;

        //数组的不可扩展性
        if (defaultContact.length < 3)
        {
            ContactsItem[] contacts = new ContactsItem[defaultContact.length + 1];
            for (int i = 0; i < contacts.length; i++)
            {
                if (i < contacts.length - 1)
                {
                    contacts[i] = defaultContact[i];
                }
                else
                {
                    contacts[i] = new ContactsItem();
                }
            }

            defaultContact = contacts;

        }

        // 是否重复
        for (int i = 0; i < defaultContact.length; i++)
        {
            if (null != defaultContact[i])
            {
                if (number.equals(defaultContact[i].Number))
                {
                    item = i;
                    boolSave = true;
                }
            }
        }

        if (boolSave)
        {
            conItem = defaultContact[item];
            conItem.Name = name;
            for (int j = item - 1; j >= 0; j--)
            {
                if (null != defaultContact[j])
                {
                    this.defaultContact[j + 1] = this.defaultContact[j];
                }
            }
            defaultContact[0] = conItem;
        }
        else
        {
            for (int i = defaultContact.length - 2; i >= 0; i--)
            {

                if (null != defaultContact[i])
                {
                    this.defaultContact[i + 1] = this.defaultContact[i];
                }
                else
                {

                }
            }

            conItem = new ContactsItem();

            if (null != conItem)
            {

                conItem.Name = name;
                conItem.Number = number;
                this.defaultContact[0] = conItem;
            }
        }

        //每次编辑、添加联系人后就指向第一个历史联系人
        currentPhone = 0;
        showBigHand(currentPhone);

        saveContactsItem2Cache();
    }

    // click listener for pick up tel number from contact
    final View.OnClickListener listenerPickupNumber = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            if (null == defaultContact)
            {
                localPhone();
            }
            else
            {

                isEdit = true;
                Intent intent = new Intent(TelephoneActivity.this, TelephoneEditActivity.class);

                intent.putExtra(FoConstants.TELEPHONE_NAME, defaultContact[currentPhone].Name);
                intent.putExtra(FoConstants.TELEPHONE_NUMBER, defaultContact[currentPhone].Number);

                startActivityForResult(intent, EDIT_TELEPHONE);
            }
        }
    };

    // 调用系统电话簿
    public void localPhone()
    {

        isEdit = true;
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, PICKUP_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (null == data)
        {
            return;
        }

        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        String contactId = null;
        String userName = null;
        String userNumber = null;

        // 电话簿返回值
        if (PICKUP_CONTACT == requestCode)
        {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            // @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            if ((null == cursor) || !cursor.moveToFirst())
            {
                return;
            }

            userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            if (null == phone)
            {
                return;
            }
            final int idxNumber = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (phone.moveToNext())
            {
                userNumber = phone.getString(idxNumber);
                if ((userNumber != null) && (userNumber.length() > 1))
                {
                    break;
                }
            }

        }

        // 编辑返回值
        if (EDIT_TELEPHONE == requestCode)
        {
            userName = data.getStringExtra(FoConstants.TELEPHONE_NAME);
            userNumber = data.getStringExtra(FoConstants.TELEPHONE_NUMBER);
        }


        onContactChanged(userName, userNumber);

    }

    /**
     * 获取手机联系人头像
     */
    private Bitmap getContactPhoto(String number)
    {

        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
        Cursor cursorCantacts = getContentResolver().query(uriNumber2Contacts, null, null, null, null);

        // 若游标不为0则说明有头像,游标指向第一条记录,么有头像设置默认头像
        if (cursorCantacts.getCount() > 0)
        {
            cursorCantacts.moveToFirst();
            Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (null == bitmap || null == handPhoto)
            {
                return null;
            }

            return bitmap;

        }
        else
        {
            return null;

        }

    }

    // CallPhone
    public void TelephoneCall()
    {

        if (null == defaultContact)
        {
            return;
        }

        if (null == defaultContact[currentPhone])
        {
            return;
        }

        String num = defaultContact[currentPhone].Number;
        String name = defaultContact[currentPhone].Name;

        if (null == num)
        {
            return;
        }

        TTSPlayerUtil.playOutCall(getApplicationContext(), num, name);
    }

    //保存联系人快捷选择
    public void saveContactItem()
    {
        if (currentPhone < 0 || currentPhone > 2)
        {
            return;
        }

        FoPreference.putInt(FoConstants.TELEPHONE_SELECT, currentPhone);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (null == defaultContact)
        {

            setHeaderRightBtnDrawable(R.drawable.phone_book_icon, listenerPickupNumber);
            return;
        }
        else
        {
            setHeaderRightBtnDrawable(R.drawable.edit_contact_icon, listenerPickupNumber);
        }

        //防止以下代码和onactivityresult中的代码冲突
        if (isEdit)
        {
            return;
        }

        // activity隐藏,保存的选择记录
        int selected = FoPreference.getInt(FoConstants.TELEPHONE_SELECT);

        if (selected >= 0 && selected <= 2)
        {
            showBigHand(selected);
        }
        else
        {
            showBigHand(0);
        }

    }

}