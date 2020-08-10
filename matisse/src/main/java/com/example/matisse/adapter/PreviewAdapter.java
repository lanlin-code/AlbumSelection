package com.example.matisse.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PreviewAdapter extends PagerAdapter {

    private List<View> mChildren;
    private List<Item> mData;


    public PreviewAdapter(List<View> views, List<Item> items) {
        mChildren = views;
        mData = items;


    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mChildren.get(position);
        Item item = mData.get(position);

        ImageView imageView = (ImageView) view;
        Picasso.with(container.getContext()).load(item.getContentUri()).
                placeHolder(R.drawable.ic_android_black_24dp).resize(0, 0).error(R.drawable.ic_android_black_24dp).into(imageView);
        container.addView(view);
        return view;
    }






    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mChildren.get(position));
    }

    @Override
    public int getCount() {
        return mChildren.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
