package com.example.picturewall.manager;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.example.picturewall.R;
import com.example.picturewall.entity.Picture;
import com.example.picturewall.entity.Result;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBitmapManager implements Result {

    private static final String TAG = "LoadBitmapManager";

    private SparseArray<ImageView> mImageViewMap;
    private LruImageCache mLruImageCache;
    private TestPost mTestPost;
    private ExecutorService mExecutorService;

    private static LoadBitmapManager mInstance;

    public static LoadBitmapManager getInstance() {
        if (null == mInstance) {
            synchronized (LoadBitmapManager.class) {
                if (null == mInstance)
                    mInstance = new LoadBitmapManager();
            }
        }
        return mInstance;
    }

    private LoadBitmapManager() {
        mImageViewMap = new SparseArray<>();
        mLruImageCache = LruImageCache.getInstance();
        mTestPost = new TestPost(new Handler(Looper.getMainLooper()));
        mExecutorService = Executors.newFixedThreadPool(4);
    }


    public void display(String path, ImageView imageView, int width, int height, boolean isLoad) {
        display(path, imageView, width, height, isLoad, this);
    }

    public void display(String path, ImageView imageView, int width, int height, boolean isLoad, Result result) {
        if (TextUtils.isEmpty(path) || imageView == null) {
            Log.i(TAG, "display: params error");
            return;
        }
        Bitmap bitmap = mLruImageCache.getBitmap(mLruImageCache.getKey(path, width, height));
        if (bitmap != null && !bitmap.isRecycled()) {
            imageView.setImageBitmap(bitmap);
            if (result != null) {
                result.onSuccess(path, imageView, bitmap, width, height);
            }
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
            if (isLoad) {
                mExecutorService.execute(new BitmapTask(path, imageView, mTestPost, result, width, height));
            }
        }
    }

    public void display(String path, ImageView imageView, int width, int height) {
        display(path, imageView, width, height, true);
    }

    public void display(Picture picture, ImageView imageView, int width, int height, boolean isLoad) {
        if (picture != null) {
            display(picture.getPath(), imageView, width, height, isLoad);
        }
    }

    public void addImageView(int position, ImageView imageView, int width, int height) {
        mImageViewMap.put(position, imageView);
        String path = (String) imageView.getTag();
        display(path, imageView, width, height, false);
    }


    public void displayList(int start, int count, List<Picture> pathList, int width, int height) {
        if (start < 0 || count < 0 || pathList == null || pathList.isEmpty()) {
            Log.i(TAG, "displayList: params error");
            return;
        }
        if (start + count > pathList.size()) {
            Log.i(TAG, "displayList: list`s size must > start + count");
            return;
        }
        for (int i = start, size = start + count; i < size; i++) {
            Picture picture = pathList.get(i);
            ImageView imageView = mImageViewMap.get(i);
            display(picture, imageView, width, height, true);
        }
    }


    @Override
    public void onSuccess(String path, ImageView imageView, Bitmap bitmap, int width, int height) {
        if (path == null || imageView == null || bitmap == null || bitmap.isRecycled()) {
            Log.i(TAG, "onSuccess: params error");
            return;
        }
        if (!path.equals(imageView.getTag())) {
            Log.i(TAG, "onSuccess: imageView change");
            return;
        }
        String key = mLruImageCache.getKey(path, width, height);
        mLruImageCache.putBitmap(key, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onFailed(String path, ImageView imageView) {
        if (path == null || imageView == null) {
            Log.i(TAG, "onFailed: params error");
            return;
        }
        imageView.setImageResource(R.mipmap.ic_launcher);
    }

}
