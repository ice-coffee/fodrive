package com.jinglingtec.ijiazu.accountmgr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;
import com.jinglingtec.ijiazu.activity.BaseActivity;
import com.jinglingtec.ijiazu.data.Depot;
import com.jinglingtec.ijiazu.ui.RoundImageView;
import com.jinglingtec.ijiazu.util.FoConstants;
import com.jinglingtec.ijiazu.util.FoPreference;
import com.jinglingtec.ijiazu.util.FoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by coffee on 14-11-6.
 */
public class AccountHomeActivity extends BaseActivity
{
    private Bitmap head;//头像Bitmap
    private static String path = FoConstants.PATH;//sd路径

    private String aldum;
    private String takephoto;

    private String[] items;


    /*头像名称*/
    private static final String IMAGE_FILE_NAME = FoConstants.IMAGE_FILE_NAME;
    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    //修改用户资料
    private RoundImageView iv_self_photo;
    private TextView landName;
    private TextView accountState;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fodrive_account);

        this.setTitleText(R.string.i_cloud);
        setHeaderLeftBtn();

        iv_self_photo = (RoundImageView) findViewById(R.id.iv_self_photo);
        landName = (TextView) findViewById(R.id.tv_landname);
        accountState = (TextView) findViewById(R.id.tv_account_state);

        Bitmap bt = BitmapFactory.decodeFile(path + IMAGE_FILE_NAME);//从Sd中找头像，转换成Bitmap
        if (bt != null)
        {
            @SuppressWarnings("deprecation") Drawable drawable = new BitmapDrawable(bt);//转换成drawable
            iv_self_photo.setImageDrawable(drawable);
        }
        else
        {
            iv_self_photo.setBackgroundResource(R.drawable.head_portrait_big);
        }


        setAccountInfo();

        iv_self_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog();
            }
        });

        aldum = getResources().getString(R.string.choose_album);
        takephoto = getResources().getString(R.string.taking_pictures);
        items = new String[]{aldum, takephoto};
    }

    /**
     * 退出登录
     *
     * @param view
     */
    public void systemOut(View view)
    {
        //        FoUtil.toast(getApplicationContext(), getResources().getString(R.string.system_out));
        //退出登录，并将用户信息赋值为空
        Depot.account = null;

        FoPreference.removeString(FoConstants.ACCOUNT_INFO);

        finish();

    }

    /**
     * 用户信息
     */
    public void setAccountInfo()
    {
        //获取用户信息，判断是否存在
        AccountInfo accountInfo = AccountManager.loadAccountInfo();
        if (null != accountInfo)
        {
            landName.setText(accountInfo.Mobile);
            accountState.setText(accountInfo.NickName);
        }
    }

    /**
     * 显示选择对话框
     */
    private void showDialog()
    {

        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.set_hand_picture))//设置头像
                .setItems(items, new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            //本地图库
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType(FoConstants.HAND_PHONE_TYPE); // 设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                            //照相
                            case 1:

                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (FoUtil.hasSdcard())
                                {

                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                }

                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()//点击取消
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).show();

    }

    /**
     * 返回处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        //此处不可直接使用data！=null判断，因为resultCode == CAMERA_REQUEST_CODE 时data==null
        switch (requestCode)
        {
            case IMAGE_REQUEST_CODE:
                if (data != null)
                {
                    startPhotoZoom(data.getData());
                }
                break;
            case CAMERA_REQUEST_CODE:
                //检测sd卡
                if (FoUtil.hasSdcard())
                {
                    File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                }
                else
                {
                    FoUtil.toast(this, getResources().getString(R.string.not_find_SDCard));
                }

                break;
            case RESULT_REQUEST_CODE:
                if (data != null)
                {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null)
                    {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);//保存在SD卡中
                        iv_self_photo.setImageDrawable(new BitmapDrawable(head));//显示
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri)
    {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, FoConstants.HAND_PHONE_TYPE);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    //    private void getImageToView(Intent data)
    //    {
    //        Bundle extras = data.getExtras();
    //        if (extras != null)
    //        {
    //            Bitmap photo = extras.getParcelable("data");
    //            Drawable drawable = new BitmapDrawable(photo);
    //            iv_self_photo.setImageDrawable(drawable);
    //        }
    //
    //
    //    }

    //保存图片
    private void setPicToView(Bitmap mBitmap)
    {
        if (null == mBitmap)
        {
            return;
        }
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + IMAGE_FILE_NAME;//图片名字
        try
        {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

}
