package com.example.picturewall.manager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * recyclerView 间隔
 *
 * @author youngbin
 *         2016-09-21
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public SpacesItemDecoration(int mSpace) {
        this.mSpace = mSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mSpace;
        outRect.top = mSpace;
        outRect.right = mSpace;
        outRect.left = mSpace;
    }
}
