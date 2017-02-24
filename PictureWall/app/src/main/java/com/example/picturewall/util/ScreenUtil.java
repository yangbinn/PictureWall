package com.example.picturewall.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class ScreenUtil {

    /**
     * 获取屏幕尺寸
     */
    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    /**
     * 获取屏幕密度
     */
    public static float getDensity() {
        return getDisplayMetrics().density;
    }

    public static float getScaledDensity() {
        return getDisplayMetrics().scaledDensity;
    }

    /**
     * 获取屏幕高
     */
    public static int getHeight() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽
     */
    public static int getWidth() {
        return getDisplayMetrics().widthPixels;
    }


    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图
     */
    public static Bitmap getScreenBitmap(Context context, boolean hasStatusBar) {
        View view = ((Activity) context).getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getWidth();
        int height = getHeight();
        Bitmap bp = null;
        int statusBarHeight = 0;
        if (hasStatusBar) {
            statusBarHeight = getStatusHeight(context);
        }
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float value) {
        return (int) (value * getDensity() + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float value) {
        return (int) (value / getDensity() + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

}
