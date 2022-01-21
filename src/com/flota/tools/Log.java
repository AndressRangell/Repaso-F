package com.flota.tools;

/**
 * API for sending log output.
 *
 * <p>Generally, use the Log.v() Log.d() Log.i() Log.w() and Log.e()
 * methods.
 *
 * <p>The order in terms of verbosity, from least to most is
 * ERROR, WARN, INFO, DEBUG, VERBOSE.  Verbose should never be compiled
 * into an application except during development.  Debug logs are compiled
 * in but stripped at runtime.  Error, warning and info logs are always kept.
 *
 * <p><b>Tip:</b> A good convention is to declare a <code>TAG</code> constant
 * in your class:
 *
 * <pre>private static final String TAG = "MyActivity";</pre>
 * <p>
 * and use that in subsequent calls to the log methods.
 * </p>
 *
 * <p><b>Tip:</b> Don't forget that when you make a call like
 * <pre>Log.v(TAG, "index=" + i);</pre>
 * that when you're building the string to pass into Log.d, the compiler uses a
 * StringBuilder and at least three allocations occur: the StringBuilder
 * itself, the buffer, and the String object.  Realistically, there is also
 * another buffer allocation and copy, and even more pressure on the gc.
 * That means that if your log message is filtered out, you might be doing
 * significant work and incurring significant overhead.
 */
@SuppressWarnings("unused")
public class Log {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    private static final String TAG_NULL = "TAG Null";
    private static final String MESSAGE_NULL = "MESSAGE Null";
    private static final String THROWABLE_NULL = "throwable null";

    private Log() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;

        return android.util.Log.v(logTag, logMsg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.v(logTag, logMsg, logTr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;

        return android.util.Log.d(logTag, logMsg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.d(logTag, logMsg, logTr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {

        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;

        return android.util.Log.i(logTag, logMsg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.i(logTag, logMsg, logTr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;


        return android.util.Log.w(logTag, logMsg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        String logMsg = (msg != null) ? msg : MESSAGE_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.w(logTag, logMsg, logTr);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param tr  An exception to log
     */
    public static int w(String tag, Throwable tr) {
        String logTag = (tag != null) ? tag : TAG_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.i(logTag, "", logTr);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return android.util.Log.e(tag != null ? tag : TAG_NULL, msg != null ? msg : MESSAGE_NULL);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        String logTag = tag != null ? tag : TAG_NULL;
        String logMsg = msg != null ? msg : MESSAGE_NULL;
        Throwable logTr = tr != null ? tr : new Throwable(THROWABLE_NULL);

        return android.util.Log.e(logTag, logMsg, logTr);
    }
}
