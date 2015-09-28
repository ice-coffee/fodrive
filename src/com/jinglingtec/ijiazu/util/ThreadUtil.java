package com.jinglingtec.ijiazu.util;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// thread util to manage the unified thread pool
public class ThreadUtil
{
    private static ExecutorService threadPool = null;

    // get the thread pool
    public static ExecutorService getPool()
    {
        if (threadPool == null)
        {
            synchronized (ThreadUtil.class)
            {
                if (threadPool == null)
                {
                    final int pool_size = Runtime.getRuntime().availableProcessors() * 2;
                    threadPool = Executors.newFixedThreadPool(pool_size);
                }
            }
        }
        else
        {
            if (threadPool.isShutdown())
            {
                synchronized (ThreadUtil.class)
                {
                    if (threadPool.isShutdown())
                    {
                        final int pool_size = Runtime.getRuntime().availableProcessors() * 2;
                        threadPool = Executors.newFixedThreadPool(pool_size);
                    }
                }
            }
        }

        return threadPool;
    }

    // execute a runnable
    public static void execute(Runnable rable)
    {
        if ((null == threadPool) || threadPool.isShutdown())
        {
            threadPool = null;
            getPool();
        }
        threadPool.execute(rable);
    }

    // submit a runnable to run
    //    public static Future<?> submit(Runnable rable)
    //    {
    //        if ((null == threadPool) || threadPool.isShutdown())
    //        {
    //            threadPool = null;
    //            getPool();
    //        }
    //        return threadPool.submit(rable);
    //    }

    // destroy the thread pool
    public static void destroyPool()
    {
        if (null == threadPool)
        {
            return;
        }

        ExecutorService pool = threadPool;
        threadPool = null;

        try
        {
            pool.shutdown(); // Disable new tasks from being submitted
            int count = 0;

            while (!pool.awaitTermination(60, TimeUnit.SECONDS))
            {
                count++;
                if (count > 10)
                {
                    pool.shutdownNow(); // Cancel currently executing tasks
                    break;
                }
            }

            Log.d("ThreadUtil", "Thread Pool terminated successfully, congratulations !");
        } catch (InterruptedException ie)
        {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}

