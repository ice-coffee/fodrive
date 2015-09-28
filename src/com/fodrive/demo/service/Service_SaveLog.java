package com.fodrive.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <pre>
 *
 * <p>在另一线程开启保存日志的服务，调试用，正式发布取消
 *
 * <p>日志等级*:v , *:d , *:w , *:e , *:f , *:s
 *
 * <p>eg:如果定义*：e等级的日志那么e f s 这三个等级的日志将会打印
 *
 *
 * <p>eg cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
 *
 * cmds = "logcat  | grep \"(" + mPID + ")\"";
 *
 * <p>打印某个进程中的日志信息
 *
 * cmds = "logcat -s way";
 *
 * <p>打印标签过滤信息
 *
 * <p>cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
 *
 * </pre>
 *
 * @author leequer
 */
public class Service_SaveLog extends Service implements Runnable
{
    private static final String TAG = "Service_SaveLog";

    SaveLog.Stub mStub = new SaveLog.Stub()
    {

        @Override
        public void startSaveLog() throws RemoteException
        {
            // TODO Auto-generated method stub
            CLog.i(TAG, "startSaveLog");
            runing = true;
            startSavelogThread();
        }

        @Override
        public void StopSaveLog() throws RemoteException
        {
            // TODO Auto-generated method stub
            CLog.i(TAG, "StopSaveLog");
            runing = false;
            stopSelf();
        }
    };
    private boolean runing = true;

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub

        return mStub;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        runing = true;
        CLog.i(TAG, "onCreate");
        startSavelogThread();

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId)
    {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        CLog.i(TAG, "onstart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO Auto-generated method stub
        CLog.i(TAG, "onstartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        CLog.i(TAG, "onunbind");
        stopService(intent);
        return super.onUnbind(intent);

    }

    private void startSavelogThread()
    {
        Thread _saveLogThread = new Thread(Service_SaveLog.this);
        _saveLogThread.setName("save_log");
        _saveLogThread.start();
    }

    public static String getCurrentTime(String format)
    {
        SimpleDateFormat dateformat1 = new SimpleDateFormat(format);
        String time = dateformat1.format(new Date());
        return time;
    }

    public static void isHavePath(String path)
    {
        File _file = new File(path);
        if (!_file.exists())
        {
            _file.mkdirs();
        }
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        Process _Process = null;
        BufferedReader _BufferedReader = null;
        FileOutputStream _FileOutputStream = null;

        String _path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ijiazu/logs/";

        CLog.i(TAG, _path);
        String _logFileName = getCurrentTime("yyyy-MM-dd-hh-mm-ss") + ".txt";

        try
        {
            isHavePath(_path);
            File _file = new File(_path + _logFileName);

            _Process = Runtime.getRuntime().exec("logcat -v time *:v  ");
            // 保存某个进程的所有日志
            // _Process = Runtime.getRuntime().exec("logcat *:e *:i | grep \"("
            // + _PID + ")\"");
            // 保存指定tag的日志
            // _Process = Runtime.getRuntime().exec(new String
            // []{"logcat",你定义的tag的名字":I *:S"});
            _BufferedReader = new BufferedReader(new InputStreamReader(_Process.getInputStream()));
            _FileOutputStream = new FileOutputStream(_file);
            String _line;
            while ((_line = _BufferedReader.readLine()) != null && runing)
            {
                _line += "\r\n";
                _FileOutputStream.write(_line.getBytes());
            }
            _FileOutputStream.flush();
            _FileOutputStream.close();
            _BufferedReader.close();
        } catch (Exception e)
        {
        }
    }

}
