package run.yang.lib.heterogeneousjsonlist.log;

/**
 * 创建时间: 2017/11/29 11:57 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public interface Logger {
    int ERROR = 1;
    int WARNING = 2;
    int DEBUG = 3;
    int VERBOSE = 4;

    /**
     * @param logLevel {@link #ERROR}, {@link #WARNING}, {@link #DEBUG}, {@link #VERBOSE}
     * @param msg      log message, maybe null
     */
    void log(int logLevel, String msg);

    /**
     * @param logLevel {@link #ERROR}, {@link #WARNING}, {@link #DEBUG}, {@link #VERBOSE}
     * @param template String template, maybe null. see {@link String#format(String, Object...)}
     * @param args     arguments, see {@link String#format(String, Object...)}
     */
    void log(int logLevel, String template, Object... args);
}
