package com.example.picturewall.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具
 *
 * @author youngbin
 *         2016-08-26
 */
public class FileUtil {

    public static final String TAG = FileUtil.class.getSimpleName();

    /**
     * 删除文件夹或文件之前重命名，防止ebusy
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void delete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) delete(childFile);
            }
        }
        File to = new File(file.getAbsolutePath()
                + System.currentTimeMillis());
        if (!file.renameTo(to)) {
            Log.i(TAG, "delete, file.renameTo failed");
        }
        if (!to.delete()) {
            Log.i(TAG, "delete, to.delete failed");
        }
    }

    /**
     * file size
     */
    public static long getFileSize(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            long length = 0;
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    length += getFileSize(childFile);
                }
            }
            return length;
        }
        return 0;
    }

    /**
     * file size to String
     */
    public static String fileSizeToString(long size) {
        if (size < 1024) {
            return String.format(Locale.getDefault(), "%dB", size);
        } else if (size < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%1.2fKB", (float) size / 1024);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%1.2fMB", (float) size / 1024 / 1024);
        } else {
            return String.format(Locale.getDefault(), "%1.2fGB", (float) size / 1024 / 1024 / 1024);
        }
    }

    /**
     * SD path
     */
    private static String getSDPath() {
        String path = null;
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {// sd卡存在
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }


    /**
     * 获取下载路径
     */
    public static String getDownloadPath(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null) {
            File f = new File(file.getPath() + File.separator + "download");
            if (!f.exists()) {
                f.mkdirs();
            }
            return f.getPath();
        }
        return null;
    }

    /**
     * imageCache path
     */
    public static File getExternalImageCacheFile(Context context) {
        if (context == null) {
            Log.i(TAG, "context not null.");
            return null;
        }
        File file = context.getExternalCacheDir();
        File result = null;
        if (file != null) {
            result = new File(file, "imageCache");
            if (!result.exists()) {
                if (!result.mkdirs())
                    return null;
            }
        }
        return result;
    }

    /**
     * 异常日志文件
     */
    public static File getExternalLogFile(Context context) {
        if (context == null) {
            Log.i(TAG, "context not null.");
            return null;
        }
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
        File result = null;
        if (file != null) {
            result = new File(file, "log" + File.separator + "log.txt");
            File parent = result.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs())
                    return null;
            }
        }
        return result;
    }

    /**
     * 照片保存位置
     */
    public static File getExternalPhotoFile(Context context) {
        if (context == null) {
            Log.i(TAG, "context not null.");
            return null;
        }
        String newFileName = String.format(Locale.getDefault(), "%d.jpg", new Date().getTime());
        File result = null;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null) {
            result = new File(file, newFileName);
        }
        return result;
    }

    /**
     * 照片路径
     */
    public static File getOutputMediaFile() {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "construction");
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Log.i(TAG, "没有写权限");
                    return null;
                }
            }
            String newFileName = String.format(Locale.getDefault(), "%d.jpg", new Date().getTime());
            return new File(f, newFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
