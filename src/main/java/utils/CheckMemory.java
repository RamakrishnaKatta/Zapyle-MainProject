package utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by haseeb on 26/2/16.
 */
public class CheckMemory {
    public static Boolean CheckInternalMemory(long mem){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long megavailable = availableBlocks * blockSize;
        return megavailable > mem;
    }

    public static Boolean CheckExternalMemory(long mem){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;
        ////System.out.println("Megs :"+megAvailable);

        return megAvailable > mem;
    }

}
