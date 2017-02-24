package com.example.picturewall.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList;

    public BaseAdapter(List<T> list) {
        this.mList = list;
    }

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<T> list) {
        if (list != null && !list.isEmpty()) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            for (T t : list) {
                mList.add(t);
            }
            notifyDataSetChanged();
        }
    }

    public List<T> getList() {
        return mList;
    }

    public int getCount() {
        return mList != null ? mList.size() : 0;
    }


    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public OnItemClickListener<T> mListener;


    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T t);
    }

    public OnItemLongClickListener<T> mLongListener;


    public void setOnItemLongClickListener(OnItemLongClickListener<T> longListener) {
        mLongListener = longListener;
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View view, int position, T t);
    }


    public OnClickListener mClickListener;

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(View view);
    }

    public OnLongClickListener<T> mOnLongListener;

    public void setOnLongClickListener(OnLongClickListener<T> onLongClickListener) {
        this.mOnLongListener = onLongClickListener;

    }

    public interface OnLongClickListener<T> {
        void onLongListener(View view, int position, T t);
    }
}
