package com.example.picturewall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.picturewall.entity.Result;
import com.example.picturewall.manager.LoadBitmapManager;
import com.example.picturewall.manager.LruImageCache;
import com.example.picturewall.util.ScreenUtil;
import com.example.picturewall.view.ZoomImageView;

public class PictureDetailActivity extends BaseActivity implements ZoomImageView.OnPhotoTapListener, ZoomImageView.OnViewTapListener,
        Result {

    private static final String TAG = "PictureDetailActivity";

    private ZoomImageView mImageView;
    private ProgressBar mProgressBar;
    private LoadBitmapManager mLoadBitmapManager;
    private LruImageCache mLruImageCache;
    private String mPath;

    public static void startActivity(Activity activity, String path) {
        Intent intent = new Intent(activity, PictureDetailActivity.class);
        intent.putExtra("path", path);
        activity.startActivity(intent);
    }


    @Override
    protected void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_picture_detail);
        mImageView = (ZoomImageView) findViewById(R.id.picture_detail_iv);
        mProgressBar = (ProgressBar) findViewById(R.id.picture_detail_pb);
    }

    @Override
    protected void initListener() {
        mImageView.setOnPhotoTapListener(this);
        mImageView.setOnViewTapListener(this);
    }

    @Override
    protected void initReceiver() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mPath = intent.getStringExtra("path");
        mLruImageCache = LruImageCache.getInstance();
        mLoadBitmapManager = LoadBitmapManager.getInstance();
        mImageView.setTag(mPath);
        mLoadBitmapManager.display(mPath, mImageView, ScreenUtil.getWidth(), ScreenUtil.getHeight(), true, this);
    }

    @Override
    protected void saveData(Bundle outState) {

    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        finish();
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
    }

    @Override
    public void onSuccess(String path, ImageView imageView, Bitmap bitmap, int width, int height) {
        mProgressBar.setVisibility(View.GONE);
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
        mProgressBar.setVisibility(View.GONE);
        if (path == null || imageView == null) {
            Log.i(TAG, "onFailed: params error");
            return;
        }
        imageView.setImageResource(R.mipmap.ic_launcher);
    }
}
