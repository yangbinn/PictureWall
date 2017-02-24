package com.example.picturewall;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.picturewall.adapter.BaseAdapter;
import com.example.picturewall.adapter.PictureAdapter;
import com.example.picturewall.entity.Picture;
import com.example.picturewall.manager.LoadBitmapManager;
import com.example.picturewall.manager.SpacesItemDecoration;
import com.example.picturewall.util.FileUtil;
import com.example.picturewall.util.ScreenUtil;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PictureActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = PictureActivity.class.getSimpleName();

    public static final int REQUEST_CAMERA = 10001;
    private static final int REQUEST_CODE_SDCARD = 10005; //读取sd卡权限

    private TextView mFinishView;
    private RecyclerView mRecyclerView;

    private PictureAdapter mPictureAdapter;
    private int mFirst;
    private int mCount;
    private boolean mIsFirst = true;
    private String mPath;
    private Uri mUri;
//    private ImageLoader mImageLoader;

    private LoadBitmapManager mLoadBitmapManager;
    private List<Picture> mList;
    public int mMaxCount;

    public static void startActivityForResult(Activity activity, int requestCode) {
        startActivityForResult(activity, null, requestCode);
    }

    public static void startActivityForResult(Activity activity, List<String> pathList, int requestCode) {
        startActivityForResult(activity, pathList, 9, requestCode);
    }

    public static void startActivityForResult(Activity activity, List<String> pathList, int maxCount, int requestCode) {
        Intent intent = new Intent(activity, PictureActivity.class);
        intent.putExtra("max_count", maxCount);
        if (pathList != null)
            intent.putStringArrayListExtra("select_list", (ArrayList<String>) pathList);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_picture);
        mFinishView = (TextView) findViewById(R.id.picture_finish_tv);
        mRecyclerView = (RecyclerView) findViewById(R.id.picture_recycler);
    }

    @Override
    protected void initListener() {
        mRecyclerView.addOnScrollListener(mScrollListener);
        mFinishView.setOnClickListener(this);
    }

    @Override
    protected void initReceiver() {
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mFinishView.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        mMaxCount = intent.getIntExtra("max_count", 9);
        List<String> selectList = intent.getStringArrayListExtra("select_list");

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ScreenUtil.dip2px(2)));
        mPictureAdapter = new PictureAdapter(mList, selectList, mMaxCount, mLoadBitmapManager = LoadBitmapManager.getInstance());
        mRecyclerView.setAdapter(mPictureAdapter);
        mPictureAdapter.setOnItemClickListener(mItemClickListener);
        mPictureAdapter.setOnSelectListener(mSelectListener);

        if (selectList != null && selectList.size() > 0)
            mFinishView.setText(String.format(Locale.getDefault(), "%d/%d  完成", selectList.size(), mMaxCount));
        else
            mFinishView.setText("完成");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MPermissions.requestPermissions(PictureActivity.this, REQUEST_CODE_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            mPictureAdapter.setList(mList = getBitmapPath());
        }

    }

    @Override
    protected void saveData(Bundle outState) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUEST_CODE_SDCARD)
    public void requestSdcardSuccess() {
        mPictureAdapter.setList(mList = getBitmapPath());
    }

    @PermissionDenied(REQUEST_CODE_SDCARD)
    public void requestSdcardFailed() {
        showToast("没有读取权限");
    }

    private BaseAdapter.OnItemClickListener<Picture> mItemClickListener = new BaseAdapter.OnItemClickListener<Picture>() {
        @Override
        public void onItemClick(View view, int position, Picture picture) {
            if (position == 0) {
                //地址为空，大小小于0, 调用拍照
                List<String> pictureList = mPictureAdapter.getSelectList();
                if (pictureList != null && pictureList.size() >= mMaxCount) {
                    showToast(String.format(Locale.getDefault(), "最多只能选%d张", mMaxCount));
                } else {
                    openCamera();
                }
            } else {
                PictureDetailActivity.startActivity(PictureActivity.this, picture.getPath());
            }
        }
    };

    private PictureAdapter.OnSelectListener mSelectListener = new PictureAdapter.OnSelectListener() {
        @Override
        public void onSelect(View view, int size) {
            if (size == 0)
                mFinishView.setText("完成");
            else
                mFinishView.setText(String.format(Locale.getDefault(), "%d/%d  完成", size, mMaxCount));
        }
    };

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                Log.i(TAG, "onScrollStateChanged,first=" + mFirst);
                mLoadBitmapManager.displayList(mFirst, mCount, mList, 200, 200);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
                mFirst = linearLayoutManager.findFirstVisibleItemPosition();
                mCount = linearLayoutManager.findLastVisibleItemPosition() - mFirst + 1;
            } else if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
                int[] firsts = layoutManager.findFirstVisibleItemPositions(null);
                int[] lasts = layoutManager.findLastVisibleItemPositions(null);
                mFirst = getMinPosition(firsts);
                mCount = getMaxPosition(lasts) - mFirst + 1;
            }
            if (mIsFirst && mCount > 0) {
                Log.i(TAG, "onScrolled,first=" + mFirst);
                mLoadBitmapManager.displayList(mFirst, mCount, mList, 200, 200);
                mIsFirst = false;
            }
        }
    };

    /**
     * 打开相机
     */
    private void openCamera() {
        File file = FileUtil.getOutputMediaFile();
        if (file != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mUri = Uri.fromFile(file);
            mPath = file.getPath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    /**
     * 获得最大的位置
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * 获得最小的位置
     */
    private int getMinPosition(int[] positions) {
        int minPosition = Integer.MAX_VALUE;
        for (int position : positions) {
            if (position < 0) {
                continue;
            }
            minPosition = Math.min(minPosition, position);
        }
        return minPosition;
    }

    private List<Picture> getBitmapPath() {
        List<Picture> list = new ArrayList<>();
        list.add(new Picture("", 0));
        ContentResolver contentResolver = getContentResolver();
        // 构造相册索引
        String columns[] = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Thumbnails.DATA
        };
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , columns, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null)
            return null;
        int _id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int _data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int _display_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int _size = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()) {
            String id = cursor.getString(_id);
            String data = cursor.getString(_data);
            String display_name = cursor.getString(_display_name);
            long size = cursor.getLong(_size);
            if (size > 10000) {
                Log.i(TAG, "id=" + id + ",data=" + data + ",display_name=" + display_name + ",size=" + size);
                list.add(new Picture(data, size));
            }
        }
        cursor.close();
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    //通知系统添加图片
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mUri));
                    Intent intent = new Intent();
                    List<String> selectList = mPictureAdapter.getSelectList();
                    if (selectList == null) {
                        selectList = new ArrayList<>();
                    }
                    selectList.add(mPath);
                    intent.putStringArrayListExtra("select_list", (ArrayList<String>) selectList);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.picture_finish_tv) {
            Log.i(TAG, "完成");
            Intent intent = new Intent();
            intent.putStringArrayListExtra("select_list", (ArrayList<String>) mPictureAdapter.getSelectList());
            setResult(RESULT_OK, intent);
            finish();

        }
    }

}
