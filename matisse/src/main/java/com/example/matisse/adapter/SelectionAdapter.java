package com.example.matisse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.entity.SelectionSpec;
import com.example.matisse.ui.widget.CheckView;
import com.example.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectionAdapter extends RecyclerView.Adapter {

    private Cursor mCursor;
    private Map<Item, CheckView> mAll;
    private CheckClickListener mListener;
    private ImageClickListener mImageListener;


    public SelectionAdapter() {
        mAll = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.framelayout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) return;
        final Item item = Item.valueOf(mCursor);
        ViewHolder viewHolder = (ViewHolder) holder;
        mAll.put(item, viewHolder.checkView);
        ImageView imageView = viewHolder.imageView;
        int size = getImageSize(imageView.getContext());
        Picasso.with(imageView.getContext()).load(item.getContentUri()).
                placeHolder(R.drawable.ic_android_black_24dp).error(R.drawable.ic_android_black_24dp).
                resize(size, size).into(imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageListener.imageClick(item);
            }
        });
        initCheckView(item, viewHolder.checkView);
    }



    private void initCheckView(final Item item, final CheckView checkView) {
        final SelectionSpec selectionSpec = SelectionSpec.newInstance();
        final boolean countable = selectionSpec.countable;
        showSelected(item, checkView);
        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener.getPosition(item) != -1) {
                    mListener.remove(item);
                    if (countable) {
                        checkView.setCheckedNum(CheckView.UNCHECKED);
                        onUpdate();
                    } else checkView.setChecked(false);
                } else {
                    if (mListener.size() < selectionSpec.maxSelection){
                        mListener.add(item);
                        if (countable) {
                            int selectedPosition = mListener.getPosition(item);
                            checkView.setCheckedNum(selectedPosition + 1);
                        } else checkView.setChecked(true);
                    }
                }

            }
        });
    }

    // 显示选择的checkview
    private void showSelected(Item item, CheckView checkView) {
        SelectionSpec selectionSpec = SelectionSpec.newInstance();
        boolean countable = selectionSpec.countable;
        int selectedPosition = mListener.getPosition(item);
        if (countable) {
            checkView.setCountable(true);
            if (selectedPosition != -1) {
                checkView.setCheckedNum(selectedPosition+1);
            } else checkView.setCheckedNum(CheckView.UNCHECKED);
        } else {
            if (selectedPosition != -1) {
                checkView.setChecked(true);
            } else checkView.setChecked(false);
        }
    }

    // 得到图片的大小
    private int getImageSize(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        SelectionSpec selectionSpec = SelectionSpec.newInstance();
        return (int) (screenWidth/selectionSpec.spanCount*selectionSpec.thumbnailScale);
    }

    public void clearSelectedView() {
        mAll.clear();
    }

    // 更新点击的视图
    public void onUpdate() {
        Set<Item> items = mAll.keySet();
        for (Item item : items) {
            showSelected(item, mAll.get(item));
        }
    }



    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void registerCheckListener(CheckClickListener listener) {
        mListener = listener;
    }

    public void unregisterCheckListener() {
        mListener = null;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return;
        }

        if (cursor != null) {
            mCursor  = cursor;
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
        }
    }

    public void registerImageClickListener(ImageClickListener imageClickListener) {
        mImageListener = imageClickListener;
    }

    public void unregisterImageClickListener() {
        mImageListener = null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        CheckView checkView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            checkView = itemView.findViewById(R.id.check);
        }
    }


    public interface CheckClickListener {
        void add(Item item);
        int getPosition(Item item);
        void remove(Item item);
        int size();
    }

    public interface ImageClickListener {
        void imageClick(Item item);
    }

}
