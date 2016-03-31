package com.wondereight.sensioair.Helper;

import android.util.Log;

/**
 * Created by Miguel on 02/21/2016.
 */

public class _Debug {
    final static int VERBOSE = 2;
    final static int DEBUG = 3;
    final static int INFO = 4;
    final static int WARN = 5;
    final static int ERROR = 6;
    //final static int WTF = 8;

    boolean mLoggingEnabled = false;
    int mLoggingLevel = VERBOSE;

    public _Debug(){
    }

    public _Debug(boolean enable){
        mLoggingEnabled = enable;
    }

    public boolean isLoggingEnabled() {
        return mLoggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.mLoggingEnabled = loggingEnabled;
    }

    public int getLoggingLevel() {
        return mLoggingLevel;
    }

    public void setLoggingLevel(int loggingLevel) {
        this.mLoggingLevel = loggingLevel;
    }

    public boolean shouldLog(int logLevel) {
        return logLevel >= mLoggingLevel;
    }

    public void log(int logLevel, String tag, String msg) {
        logWithThrowable(logLevel, tag, msg, null);
    }

    public void logWithThrowable(int logLevel, String tag, String msg, Throwable t) {
        if (isLoggingEnabled() && shouldLog(logLevel)) {
            switch (logLevel) {
                case VERBOSE:
                    Log.v(tag, msg, t);
                    break;
                case WARN:
                    Log.w(tag, msg, t);
                    break;
                case ERROR:
                    Log.e(tag, msg, t);
                    break;
                case DEBUG:
                    Log.d(tag, msg, t);
                    break;
                case INFO:
                    Log.i(tag, msg, t);
                    break;
            }
        }
    }

    public void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    public void v(String tag, String msg, Throwable t) {
        logWithThrowable(VERBOSE, tag, msg, t);
    }

    public void d(String tag, String msg) {
        log(DEBUG, tag, msg);
    }

    public void d(String tag, String msg, Throwable t) {
        logWithThrowable(DEBUG, tag, msg, t);
    }

    public void i(String tag, String msg) {
        log(INFO, tag, msg);
    }

    public void i(String tag, String msg, Throwable t) {
        logWithThrowable(INFO, tag, msg, t);
    }

    public void w(String tag, String msg) {
        log(WARN, tag, msg);
    }

    public void w(String tag, String msg, Throwable t) {
        logWithThrowable(WARN, tag, msg, t);
    }

    public void e(String tag, String msg) {
        log(ERROR, tag, msg);
    }

    public void e(String tag, String msg, Throwable t) {
        logWithThrowable(ERROR, tag, msg, t);
    }

}
