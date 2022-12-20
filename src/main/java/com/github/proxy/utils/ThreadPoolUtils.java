package com.github.proxy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {

    private static final Logger logger = LogManager.getLogger(ThreadPoolUtils.class);

    private static final int MAX_THREADS = 32;
    private static final int MAX_THREAD_WAIT_TIME = 30;

    public static ThreadPoolExecutor createThreadPool(int numberOfParallelTasks) {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.max(1,Math.min(MAX_THREADS, numberOfParallelTasks)));
    }

    public static boolean shutdownThreadPool(ThreadPoolExecutor threadPool) {
        threadPool.shutdown();
        try {
            return threadPool.awaitTermination(MAX_THREAD_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e);
            return false;
        }
    }
}
