package com.example.matisse.collection;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.example.matisse.entity.Album;
import com.example.matisse.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int SECOND = 2;
    private AlbumMediaCallback mCallback;
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private boolean finished = false;
    private final String ALBUM_KEY = "album";


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (mContext.get() == null || args == null) return null;
        Album album = args.getParcelable(ALBUM_KEY);
        if (album == null) return null;
        finished = false;
        return AlbumMediaLoader.newInstance(mContext.get(), album);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (mContext.get() == null) return;
        if (!finished) {
            finished = true;
            if (mCallback != null) mCallback.onFinished(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mContext == null) return;
        if (mCallback != null) mCallback.onReset();
    }

    public void onCreate(FragmentActivity activity, AlbumMediaCallback callback) {
        mContext = new WeakReference<Context>(activity);
        mCallback = callback;
        mLoaderManager = LoaderManager.getInstance(activity);
    }

    public void loadItems(Album album) {
        if (mContext.get() == null) return;
        Bundle args = new Bundle();
        args.putParcelable(ALBUM_KEY, album);
        mLoaderManager.initLoader(SECOND, args, this);
    }

    public void onDestroy() {
        mCallback = null;
        mLoaderManager.destroyLoader(SECOND);
    }


    public interface AlbumMediaCallback {
        void onFinished(Cursor cursor);

        void onReset();
    }

}
