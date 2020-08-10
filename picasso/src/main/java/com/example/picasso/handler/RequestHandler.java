package com.example.picasso.handler;

import android.graphics.Bitmap;
import android.net.Uri;

public abstract class RequestHandler {
    public abstract boolean canResolve(Uri uri);
    public abstract Bitmap load(Uri uri);
}
