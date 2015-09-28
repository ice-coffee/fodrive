package com.jinglingtec.ijiazu.util;

import android.os.Handler;
import android.util.Log;
import com.jinglingtec.ijiazu.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// utility class for logger
public class FoLogger
{
    // sign to indicate whether this apk is debugable
    public static final boolean isDebuggable = true;//BuildConfig.DEBUG;

    public static int d(String tag, String msg)
    {
        return Log.d(tag, msg);
    }

    public static int i(String tag, String msg)
    {
        return Log.i(tag, msg);
    }

    public static int e(String tag, String msg)
    {
        return Log.e(tag, msg);
    }

    public static void addLog(String log)
    {
        //        initLogList();
        //        logList.add(log);
    }

    private static void initLogList()
    {
        if (null == logList)
        {
            logList = new ArrayList<String>();
            startLoggingSystem();
        }
    }

    private static void startLoggingSystem()
    {
        final int delay = 60 * 1000; // one minute
        final Handler logHandler = new Handler();
        //        final Runnable reconnectRunnable = null;

        final Runnable reconnectRunnable = new Runnable()
        {
            final Runnable self = this;

            @Override
            public void run()
            {
                logHandler.postDelayed(self, delay);

                List<String> logs = logList;
                logList = new ArrayList<String>();
                saveLog(logs);
            }
        };
        logHandler.postDelayed(reconnectRunnable, delay);

    }

    private static void saveLog(List<String> logs)
    {
        try
        {
            String _path = "/sdcard/ijiazu/logs/";
            isHavePath(_path);
            String _logFileName = getCurrentTime("yyyy-MM-dd-hh-mm-ss") + ".txt";
            File _file = new File(_path + _logFileName);

            //            _Process = Runtime.getRuntime().exec("logcat -v time *:v  ");
            // 保存某个进程的所有日志
            // _Process = Runtime.getRuntime().exec("logcat *:e *:i | grep \"("
            // + _PID + ")\"");
            // 保存指定tag的日志
            // _Process = Runtime.getRuntime().exec(new String
            // []{"logcat",你定义的tag的名字":I *:S"});
            //            BufferedReader _BufferedReader = new BufferedReader(new InputStreamReader(
            //                    _Process.getInputStream()));
            FileOutputStream _FileOutputStream = new FileOutputStream(_file);
            String _line;
            //            while ((_line = _BufferedReader.readLine()) != null && runing)
            for (int i = 0; i < logs.size(); i++)
            {
                _line = logs.get(i);
                _line += "\r\n";
                _FileOutputStream.write(_line.getBytes());
            }
            _FileOutputStream.flush();
            _FileOutputStream.close();
        } catch (Exception e)
        {
        }

    }

    public static void isHavePath(String path)
    {
        File _file = new File(path);
        if (!_file.exists())
        {
            _file.mkdirs();
        }
    }

    public static String getCurrentTime(String format)
    {
        SimpleDateFormat dateformat1 = new SimpleDateFormat(format);
        String time = dateformat1.format(new Date());
        return time;
    }

    private static List<String> logList = null;
}

