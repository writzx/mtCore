package mtcore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final Logger Log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); static {Log.setLevel(Level.ALL);}

    public static void e(String tag, String message) {
        Log.severe(tag + ": " + message);
    }

    public static void i(String tag, String message) {
        Log.info(tag + ": " + message);
    }

    public static void w(String tag, String message) {
        Log.warning(tag + ": " + message);
    }

    public static void wtf(String tag, String message) {
        Log.config(tag + ": " + message);
    }

    public static void d(String tag, String message) {
        Log.fine(tag + ": " + message);
    }

    public static void v(String tag, String message) {
        Log.finer(tag + ": " + message);
    }

    public static void println(String tag, String message) {
        Log.finest(tag + ": " + message);
    }
}
