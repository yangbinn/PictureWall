package com.example.picturewall.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.picturewall.R;
import com.example.picturewall.entity.Picture;
import com.example.picturewall.manager.LoadBitmapManager;

import java.util.ArrayList;
import java.util.List;

public class PictureAdapter extends BaseAdapter<Picture> {

    private LoadBitmapManager mImageLoader;
    private List<String> mSelectList;
    private int mMaxCount;

    public PictureAdapter(List<Picture> mList, int maxCount, LoadBitmapManager imageLoader) {
        this(mList, null, maxCount, imageLoader);
    }

    public PictureAdapter(List<Picture> mList, List<String> selectList, int maxCount, LoadBitmapManager imageLoader) {
        super(mList);
        this.mImageLoader = imageLoader;
        this.mSelectList = selectList;
        mMaxCount = maxCount;
    }

    public List<String> getSelectList() {
        return mSelectList;
    }

    public void setSelectList(List<String> selectList) {
        this.mSelectList = selectList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PictureViewHolder viewHolder = (PictureViewHolder) holder;
        Picture picture = mList.get(position);
        viewHolder.imageView.setTag(picture.getPath());
        if (position == 0) {
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
            viewHolder.checkView.setVisibility(View.GONE);
        } else {
            viewHolder.checkView.setVisibility(View.VISIBLE);
            if (mSelectList != null && mSelectList.contains(picture.getPath())) {
                viewHolder.checkView.setImageResource(R.mipmap.icon_check_select);
            } else {
                viewHolder.checkView.setImageResource(R.mipmap.icon_check_normal);
            }
            mImageLoader.addImageView(position, viewHolder.imageView, 200, 200);
        }
    }

    private class PictureViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView checkView;

        private PictureViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.picture_item_image_iv);
            checkView = (ImageView) itemView.findViewById(R.id.picture_item_check_iv);

            checkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectList == null) {
                        mSelectList = new ArrayList<>();
                    }
                    Picture picture = mList.get(getLayoutPosition());
                    if (getLayoutPosition() != 0) {
                        if (mSelectList.contains(picture.getPath())) {
                            mSelectList.remove(picture.getPath());
                        } else {
                            if (mSelectList.size() <= mMaxCount - 1)
                                mSelectList.add(picture.getPath());
                        }
                        if (mSelectListener != null) {
                            mSelectListener.onSelect(itemView, mSelectList.size());
                        }
                        notifyItemChanged(getLayoutPosition());
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v, getLayoutPosition(), mList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    public OnSelectListener mSelectListener;

    public interface OnSelectListener {
        void onSelect(View view, int size);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }
}
