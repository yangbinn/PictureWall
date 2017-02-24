package com.example.picturewall.manager;


import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.example.picturewall.entity.PostResult;
import com.example.picturewall.entity.Result;

import java.util.concurrent.Executor;

public class TestPost implements PostResult {

    private Executor mExecutor;

    public TestPost(final Handler handler) {
        mExecutor = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                handler.post(command);
            }
        };
    }


    @Override
    public void postSuccess(String path, ImageView imageView, Bitmap bitmap, Result result, int width, int height) {
        mExecutor.execute(new TestPostRunnable(path, imageView, bitmap, result, width, height));
    }

    @Override
    public void postFailed(String path, ImageView imageView, Result result) {
        mExecutor.execute(new TestPostRunnable(path, imageView, result));
    }

    private class TestPostRunnable implements Runnable {

        private String path;
        private ImageView imageView;
        private Bitmap bitmap;
        private Result result;
        private int width;
        private int height;

        private TestPostRunnable(String path, ImageView imageView, Bitmap bitmap, Result result, int width, int height) {
            this.path = path;
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.result = result;
            this.width = width;
            this.height = height;
        }

        private TestPostRunnable(String path, ImageView imageView, Result result) {
            this.path = path;
            this.imageView = imageView;
            this.result = result;
        }


        @Override
        public void run() {
            if (result != null) {
                if(bitmap == null || bitmap.isRecycled()){
                    result.onFailed(path, imageView);
                }else{
                    result.onSuccess(path, imageView, bitmap, width, height);
                }
            }
        }
    }

}
