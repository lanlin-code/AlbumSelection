package com.example.matisse;

import android.app.Activity;
import android.content.Intent;

import com.example.matisse.entity.SelectionSpec;
import com.example.matisse.ui.MatisseActivity;

import androidx.fragment.app.Fragment;

public class SelectionCreator {

    private SelectionSpec selectionSpec;
    private Matisse matisse;

    public SelectionCreator(Matisse matisse) {
        this.matisse = matisse;
        selectionSpec = SelectionSpec.newInstance();
    }

    public SelectionCreator SetCountable(boolean countable) {
        selectionSpec.countable = countable;
        return this;
    }

    public SelectionCreator SetMaxSelectionSize(int size) {
        if (size < 0) throw new IllegalArgumentException("Can't allow maxSelectionSize < 0");
        selectionSpec.maxSelection = size;
        return this;
    }

    public SelectionCreator setSpanCount(int count) {
        if (count <= 0) throw new IllegalArgumentException("Can't allow spanCount <= 0");
        selectionSpec.spanCount = count;
        return this;
    }

    public SelectionCreator setThumbnailScale(float thumbnailScale) {
        if (thumbnailScale <= 0.0f || thumbnailScale > 1.0f)
            throw new IllegalArgumentException("Can't allow thumbnailScale <= 0.0f or > 1.0f");
        selectionSpec.thumbnailScale = thumbnailScale;
        return this;
    }

    public void forResult(int requestCode) {
        Activity activity = matisse.getActivity();
        Fragment fragment = matisse.getFragment();
        if (activity == null) return;
        Intent intent = new Intent(activity, MatisseActivity.class);
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
