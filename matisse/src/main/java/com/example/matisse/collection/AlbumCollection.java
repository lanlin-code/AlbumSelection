package com.example.matisse.collection;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.example.matisse.loader.AlbumLoader;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int FIRST = 1;
    private AlbumCallback mAlbumCallback;
    private WeakReference<Context> mContext;
    private boolean finished = false;
    private LoaderManager mLoaderManager;
    private int mCurrentPosition = 0;

    public void onCreate(FragmentActivity activity, AlbumCallback albumCallback) {
        mContext = new WeakReference<Context>(activity);
        mAlbumCallback = albumCallback;
        mLoaderManager = LoaderManager.getInstance(activity);
    }

    public void loadAlbum() {
        mLoaderManager.initLoader(FIRST, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Context context = mContext.get();
        if (context == null) return null;
        finished = false;
        return AlbumLoader.newInstance(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) return;
        if (!finished) {
            finished = true;
            mAlbumCallback.onAlbumFinish(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mContext.get() == null) return;
        mAlbumCallback.onAlbumReset();
    }

    public void setCurrentPosition(int position) {
        mCurrentPosition = position;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void onDestroy() {
        mAlbumCallback = null;
        mLoaderManager.destroyLoader(FIRST);
    }


    public interface AlbumCallback {

        void onAlbumFinish(Cursor cursor);

        void onAlbumReset();

    }

}
