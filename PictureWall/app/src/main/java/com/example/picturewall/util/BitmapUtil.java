package com.example.picturewall.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片工具类
 *
 * @author youngbin
 *         2016-08-26
 */
public class BitmapUtil {

    public static final String TAG = BitmapUtil.class.getSimpleName();

    /**
     * convert Bitmap to byte array
     *
     * @param b
     * @return
     */
    public static byte[] bitmapToByte(Bitmap b) {
        if (b == null) {
            return null;
        }
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * convert Drawable to Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable)d).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * convert Drawable to byte array
     *
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d) {
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * convert byte array to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b) {
        return bitmapToDrawable(byteToBitmap(b));
    }




    /**
     * 读取图片并压缩
     *
     * @param path 文件路径
     */
    public static Bitmap decodeBitmapFromPath(String path, int width, int height) {
        if (TextUtils.isEmpty(path) || width < 0 || height < 0) {
            Log.i(TAG, "decodeBitmapFromPath, params is error.");
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = compressBitmapSize(options, width, height);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取图片并压缩
     *
     * @param bytes 字节
     */
    public static Bitmap decodeBitmapFromBytes(byte[] bytes, int width, int height) {
        if (bytes == null || width < 0 || height < 0) {
            Log.i(TAG, "decodeBitmapFromPath, params is error.");
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inSampleSize = compressBitmapSize(options, width, height);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩比例
     */
    private static int compressBitmapSize(BitmapFactory.Options options, int width, int height) {
        if (options == null || width < 0 || height < 0) {
            Log.i(TAG, "compressBitmapSize, params is error.");
            return 1;
        }
        int w = options.outWidth;
        int h = options.outHeight;
        Log.i(TAG, "w=" + w + ",h=" + h + ",width=" + width + ",height=" + height + " " + options.outMimeType);

        int size = 1;
        if (w > width || h > height) {
            int wScale = Math.round((float) w / (float) width);
            int hScale = Math.round((float) h / (float) height);
            size = wScale > hScale ? wScale : hScale;
            Log.i(TAG, "wScale=" + wScale + ",hScale=" + hScale + ",size=" + size);
        }
        return size;
    }

    /**
     * 保存图片到文件
     */
    public static String writeBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.i(TAG, "图片为空，不能保存");
            return null;
        }
        File file = FileUtil.getExternalPhotoFile(context);
        if (file == null) {
            Log.i(TAG, "保存路径为空，不能保存");
            return null;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        }
        return file != null ? file.getPath() : null;
    }

    /**
     * 保存图片到相册
     */
    public static String writeBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.i(TAG, "图片为空，不能保存");
            return null;
        }
        File file = FileUtil.getOutputMediaFile();
        if (file == null) {
            Log.i(TAG, "保存路径为空，不能保存");
            return null;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        }
        return file != null ? file.getPath() : null;
    }

    /**
     * 图片转字节
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 视图转bitmap
     */
    public static Bitmap convertViewToBitmap(View v) {
        if(v == null){
            return null;
        }
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        cacheBitmap.recycle();
        return bitmap;
    }
}
