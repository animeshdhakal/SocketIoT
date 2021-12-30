package animesh.app.server;

import java.io.File;
import java.io.FileWriter;

public class Logger {
    private static FileWriter fw = null;

    public static void init(String path) {
        File targetFile = new File(path, "logs/server.log");
        File parent = targetFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (targetFile.exists()) {
            targetFile.delete();
        }

        try {
            targetFile.createNewFile();

            fw = new FileWriter(targetFile, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String level, String msg) {
        try {
            fw.write(Long.toString(System.currentTimeMillis()));
            fw.write(' ');
            fw.write(level);
            fw.write(" - ");
            fw.write(msg);
            fw.write('\n');
            fw.flush();
        } catch (Exception e) {
        }
    }

    public static void info(String msg) {
        log("INFO", msg);
    }

    public static void error(String msg) {
        log("ERROR", msg);
    }

    public static void debug(String msg) {
        // log("DEBUG", msg);
        // System.out.println(msg);
    }

    public static void warn(String msg) {
        log("WARN", msg);
    }
}
