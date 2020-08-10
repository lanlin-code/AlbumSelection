package com.example.matisse;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.matisse.entity.Item;
import com.example.matisse.entity.ItemSelectedCollection;
import com.example.matisse.executor.MyThreadPool;
import com.example.matisse.ui.MatisseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class Matisse {

    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragment;

    private Matisse(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
        MyThreadPool.newInstance();
    }

    public static Matisse from(Activity activity) {
        return new Matisse(activity, null);
    }

    public static Matisse from(Fragment fragment) {
        return new Matisse(fragment.getActivity(), fragment);
    }

    Activity getActivity() {
        return mActivity.get();
    }

    Fragment getFragment() {
        return mFragment.get();
    }

    public SelectionCreator build() {
        return new SelectionCreator(this);
    }

    public static List<Uri> getSelectedUri(Intent data) {
        List<Uri> list = new ArrayList<>();
        if (data != null) {
            Bundle bundle = data.getBundleExtra(MatisseActivity.RETURN_CODE);
            if (bundle != null) {
                List<Item> itemList = bundle.getParcelableArrayList(ItemSelectedCollection.DATA_KEY);
                if (itemList != null) {
                    for (Item item : itemList) {
                        list.add(item.getContentUri());
                    }
                }

            }
        }
        return list;
    }

}
