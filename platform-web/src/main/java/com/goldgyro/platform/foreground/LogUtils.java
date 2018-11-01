package com.goldgyro.platform.foreground;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
	private static final String FILEPATH = "C:\\callback.log";
	public static void writeStringToFile(String content) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        try {
            File file = new File(FILEPATH);
            if(!file.exists()){
                file = new File("/home/log/callback.log");
            }
            PrintStream ps = new PrintStream(new FileOutputStream(file,true));
            ps.println(df.format(now)+":\t\t"+content);
            ps.flush();
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
