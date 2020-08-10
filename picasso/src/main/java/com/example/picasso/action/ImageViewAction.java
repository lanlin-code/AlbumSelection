package com.example.picasso.action;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.picasso.Picasso;
import com.example.picasso.request.Request;

public class ImageViewAction extends Action<ImageView> {
    public ImageViewAction(Picasso picasso, ImageView target, int errorId, Request request, Object tag, String key) {
        super(picasso, target, errorId, request, tag, key);
    }

    @Override
    public void complete(Bitmap result) {
        if (result == null) {
            Log.d("TAG", "complete: result is null");
            onError();
            return;
        }
        ImageView target = super.getTarget();
        if (target == null) return;
        Log.d("TAG", "complete: all right");
        target.setImageBitmap(result);

    }

    @Override
    public void onError() {
        ImageView target = super.getTarget();
        if (target == null || getErrorId() == 0) return;
        target.setImageResource(getErrorId());
    }

    @Override
    public void onCancel() {

    }
}
