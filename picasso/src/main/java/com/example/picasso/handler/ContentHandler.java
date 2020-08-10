package com.example.picasso.handler;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ContentHandler extends RequestHandler {
    private Context context;

    public ContentHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean canResolve(Uri uri) {
        return ContentResolver.SCHEME_CONTENT.equals(uri.getScheme());
    }

    @Override
    public Bitmap load(Uri uri) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);
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
        return bitmap;
    }
}
