package com.example.picturewall.manager;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.picturewall.entity.Result;
import com.example.picturewall.util.BitmapUtil;

public class BitmapTask implements Runnable {

    private static final String TAG = "BitmapTask";

    private String mPath;
    private TestPost mTestPost;
    private Result mResult;
    private ImageView mImageView;
    private int mWidth;
    private int mHeight;

    public BitmapTask(String path, ImageView imageView, TestPost testPost, Result result, int width, int height) {
        mPath = path;
        mImageView = imageView;
        mTestPost = testPost;
        mResult = result;
        mWidth = width;
        mHeight = height;
    }


    @Override
    public void run() {
        if (mPath == null || mImageView == null || mTestPost == null || mResult == null) {
            Log.i(TAG, "run: params error");
            return;
        }
        if (!mPath.equals(mImageView.getTag())) {
            Log.i(TAG, "run: imageView change");
            return;
        }
        Bitmap bitmap = BitmapUtil.decodeBitmapFromPath(mPath, mWidth, mHeight);
        if (bitmap != null && !bitmap.isRecycled()) {
            mTestPost.postSuccess(mPath, mImageView, bitmap, mResult, mWidth, mHeight);
        }else{
            mTestPost.postFailed(mPath, mImageView, mResult);
        }
        Log.i(TAG, "run: memory=" + android.os.Debug.getNativeHeapAllocatedSize() + "---" + android.os.Debug.getNativeHeapSize());
    }
}
