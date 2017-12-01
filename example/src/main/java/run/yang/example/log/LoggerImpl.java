package run.yang.example.log;

import run.yang.lib.heterogeneousjsonlist.log.Logger;

import java.io.PrintStream;
import java.util.Locale;

/**
 * 创建时间: 2017/11/29 14:36 <br>
 * 作者: Yang Tianmei <br>
 * 描述:
 */
public class LoggerImpl implements Logger {
    @Override
    public void log(int logLevel, String msg) {
        PrintStream printStream = getPrintStream(logLevel);
        printStream.println(getLogLevelString(logLevel) + "/" + msg);
    }

    @Override
    public void log(int logLevel, String template, Object... args) {
        log(logLevel, String.format(Locale.US, template, args));
    }

    private static PrintStream getPrintStream(int logLevel) {
        return logLevel == ERROR || logLevel == WARNING ? System.err : System.out;
    }

    private static String getLogLevelString(int logLevel) {
        switch (logLevel) {
            case ERROR:
                return "E";

            case WARNING:
                return "W";

            case DEBUG:
                return "D";

            case VERBOSE:
                return "V";

            default:
                return String.valueOf(logLevel);
        }
    }
}
