package com.example.matisse.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.entity.Album;
import com.example.matisse.entity.Cache;
import com.example.matisse.executor.MyThreadPool;
import com.example.matisse.util.ThreadAdjust;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PopupAdapter extends CursorAdapter {

    private Cache cache;

    public PopupAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        cache = new Cache();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.linearlayout_album, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        TextView name = view.findViewById(R.id.name);
        TextView count = view.findViewById(R.id.count);
        ImageView imageView = view.findViewById(R.id.cover);
        name.setText(album.getDisplayName());
        String s = "(" + album.getCount() + ")";
        count.setText(s);
        Bitmap bitmap = cache.get(album.getCoverUri());
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            getImage(imageView.getContext(), imageView, album.getCoverUri());
        }

    }


    private void getImage(final Context context, final ImageView imageView, final Uri uri) {
        MyThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    final Bitmap b = BitmapFactory.decodeStream(inputStream);
                    cache.put(uri, b);
                    ThreadAdjust.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(b);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}
