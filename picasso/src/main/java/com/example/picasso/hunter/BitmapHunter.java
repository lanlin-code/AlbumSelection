package com.example.picasso.hunter;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.example.picasso.Picasso;
import com.example.picasso.action.Action;
import com.example.picasso.dispatcher.Dispatcher;
import com.example.picasso.handler.RequestHandler;
import com.example.picasso.request.Request;

import java.util.concurrent.Future;

public class BitmapHunter implements Runnable{
    private Bitmap result;
    private Action action;
    private Request data;
    private RequestHandler requestHandler;
    private Picasso picasso;
    private Dispatcher dispatcher;
    private String key;
    public Future<?> future;

    public BitmapHunter(Action action, Dispatcher dispatcher) {
        this.action = action;
        this.data = action.getRequest();
        key = action.getKey();
        this.requestHandler = action.getPicasso().getRequestHandler();
        this.picasso = action.getPicasso();
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        result = hunt();
        dispatcher.dispatchComplete(this);
    }

    public String getKey() {
        return key;
    }

    public Picasso getPicasso() {
        return picasso;
    }



    private Bitmap hunt() {
        Bitmap bitmap = picasso.getResultFromMemory(key);
        if (bitmap != null && checkSize(bitmap)) {
            Log.d("TAG", "hunt: ");
            return bitmap;
        }
        if (requestHandler.canResolve(data.getUri())) {
            bitmap = requestHandler.load(data.getUri());
        }

        if (bitmap != null && data.isSetSize()) {
            bitmap = decodeBitmap(bitmap);
        }
        return bitmap;
    }

    private boolean checkSize(Bitmap bitmap) {
        return bitmap.getWidth() == data.getTargetWidth() && bitmap.getHeight() == data.getTargetHeight();
    }

    public boolean cancel() {
        return action == null && future.cancel(false);
    }

    private Bitmap decodeBitmap(Bitmap bitmap) {
        return ThumbnailUtils.extractThumbnail(bitmap, data.getTargetWidth(), data.getTargetHeight());
    }

    public Bitmap getResult() {
        return result;
    }

    public Action getAction() {
        return action;
    }

    public boolean isCanceled() {
        return future != null && future.isCancelled();
    }
}
