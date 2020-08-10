package com.example.picasso.request;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.example.picasso.Picasso;
import com.example.picasso.action.Action;
import com.example.picasso.action.ImageViewAction;
import com.example.picasso.util.PicassoUtil;

public class RequestCreator {
    private Picasso picasso;
    private Request.Builder data;
    private int placeHolderId;
    private Bitmap placeHolderDrawable;
    private boolean place;
    private int errorId;
    private Object tag;

    public RequestCreator(Picasso picasso, Uri uri) {
        this.picasso = picasso;
        data = new Request.Builder();
        data.setUri(uri);
    }

    public RequestCreator placeHolder(int placeHolderId) {
        this.placeHolderId = placeHolderId;
        place = true;
        return this;
    }

    public RequestCreator placeHolder(Bitmap bitmap) {
        this.placeHolderDrawable = bitmap;
        place = true;
        return this;
    }

    public RequestCreator error(int errorId) {
        this.errorId = errorId;
        return this;
    }

    public RequestCreator tag(Object tag) {
        if (tag == null) throw new IllegalArgumentException("This tag is invalid");
        this.tag = tag;
        return this;
    }

    public RequestCreator resize(int targetWidth, int targetHeight) {
        data.setTargetSize(targetWidth, targetHeight);
        return this;
    }

    public void into(ImageView target) {
        PicassoUtil.checkMain();
        if (target == null) throw new IllegalArgumentException("target can not be null");
        Request request = data.build();
        if (!request.hasImage()) {
            picasso.cancelRequest(target);
            if (place) setPlaceHolder(target);
            return;
        }
        String key = request.getName();
        Bitmap bitmap = picasso.getResultFromMemory(key);
        if (bitmap != null) {
            target.setImageBitmap(bitmap);
            picasso.cancelRequest(target);
            return;
        }
        if (place) setPlaceHolder(target);
        Action action = new ImageViewAction(picasso, target, errorId, request, tag, key);
        picasso.enqueueSubmit(action);
    }

    private void setPlaceHolder(ImageView target) {
        if (place) {
            if (placeHolderDrawable != null) target.setImageBitmap(placeHolderDrawable);
            else if (placeHolderId != 0) target.setImageResource(placeHolderId);
        }
    }
}
