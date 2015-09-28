package com.jinglingtec.ijiazu.util;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.jinglingtec.ijiazu.IjiazuApp;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// utility class for logger
public class FoUtil
{
    // delete local file
    public static void removeLocalFile(String filename)
    {
        File file = new File(filename);
        if (file.exists())
        {
            file.delete();
        }
    }

    // calculate MD5 value of a input string
    public static String getMD5(String input)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = input.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e)
        {
            return null;
        }
    }

    // verify a file's MD5 value
    public static boolean verifyFileMD5(String filename, String md5)
    {
        String localMD5 = FoUtil.fileMD5(filename);
        return md5.equals(localMD5);
    }

    // calculate MD5 value of a file
    public static String fileMD5(String inputFile)
    {
        int bufferSize = 128 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0)
            {
                ;
            }
            messageDigest = digestInputStream.getMessageDigest();
            byte[] resultByteArray = messageDigest.digest();

            return byteArrayToHex(resultByteArray);
        } catch (Exception e)
        {
            return null;
        } finally
        {
            try
            {
                if (null != digestInputStream)
                {
                    digestInputStream.close();
                }
                if (null != fileInputStream)
                {
                    fileInputStream.close();
                }
            } catch (Exception e)
            {
            }
        }
    }

    // byte array to HEX
    public static String byteArrayToHex(byte[] byteArray)
    {
        if ((null == byteArray) || (byteArray.length < 1))
        {
            return "";
        }
        char[] resultCharArray = new char[byteArray.length * 2];
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        int index = 0;
        for (byte b : byteArray)
        {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        return new String(resultCharArray);
    }

    // byte array to string
    public static String byteArrayToString(byte[] byteArray)
    {
        if ((null == byteArray) || (byteArray.length < 1))
        {
            return "";
        }

        StringBuilder retval = new StringBuilder();
        for (byte by : byteArray)
        {
            retval.append(by);
        }

        return retval.toString();
    }

    /**
     * 判断存储卡是否可以用
     *
     * @return
     */
    public static boolean hasSdcard()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // show toast to user
    public static void toast(Context ctx, int resid)
    {
        Toast toast = Toast.makeText(ctx, resid, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // show toast to user
    public static void toast(Context ctx, String info)
    {
        if ((null != info) && (info.length() > 0))
        {
            Toast toast = Toast.makeText(ctx, info, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    // get current network availablity
    public static boolean isNetworkConnected(Context ctx)
    {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connManager.getActiveNetworkInfo();
        return (null != ni) && ni.isAvailable();
    }

    // get current network type
    public static int getNetworkType(Context ctx)
    {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connManager.getActiveNetworkInfo();
        return (null != ni) ? ni.getType(): -1;
    }

    // check the current installed package version code
    public static int getCurVersionCode(Context ctx)
    {
        try
        {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getApplicationContext().getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0xEFFFFFFF;
    }

    // read local mobile number
    public static String readLocalMobileNum(Context ctx)
    {
        //与手机建立连接
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //获取手机号码
        String mobile = tm.getLine1Number();
        return mobile;
    }

    // check bluetooth state
    public static boolean isBluetoothEnabled(Context ctx)
    {
        if (ctx == null)
        {
            return false;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
        {
            return false;
        }

        final BluetoothAdapter btAdapter = bluetoothManager.getAdapter();
        //        return (btAdapter == null) ? false : btAdapter.isEnabled();
        return ((null != btAdapter) && btAdapter.isEnabled());
    }

    // enable the bluetooth
    public static void enableBluetooth(Context ctx, boolean enable)
    {
        if (ctx == null)
        {
            return;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
        {
            return;
        }

        final BluetoothAdapter mBtAdapter = bluetoothManager.getAdapter();
        if (mBtAdapter == null)
        {
            return;
        }

        if (enable)
        {
            if (!mBtAdapter.isEnabled())
            {
                //如果手机蓝牙没有打开，则自动启动蓝牙
                mBtAdapter.enable();
            }
        }
        else
        {
            if (mBtAdapter.isEnabled())
            {
                mBtAdapter.disable();
            }
        }
    }

    // get the current process name
    public static String getProcessName(Context ctx, int pid)
    {
        if (pid < 0)
        {
            pid = android.os.Process.myPid();
        }
        ActivityManager mActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses())
        {
            if (appProcess.pid == pid)
            {
                return appProcess.processName;
            }
        }
        return null;
    }

    // print proc ID and name
    public static void printProcIDandName(Context ctx, String tagname)
    {
        String processName = null;
        final int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses())
        {
            if (appProcess.pid == pid)
            {
                processName = appProcess.processName;
                break;
            }
        }
        Log.e(tagname, String.format("Process Name：%s  Process ID：%d", processName, pid));
    }

}

