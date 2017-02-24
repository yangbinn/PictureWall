package com.example.picturewall.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Toast 工具类
 *
 * @author youngbin
 *         2016-09-06
 */
public class ToastUtil {

    private static Toast mToast;

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }


    public static void show(Context context, CharSequence text, int duration) {
        if (context != null && !TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(context, text, duration);
            } else {
                mToast.setDuration(duration);
                mToast.setText(text);
            }
            mToast.show();
        }
    }




}
