package com.example.picasso.request;

import android.net.Uri;

public class Request {
    private Uri uri;
    private int targetWidth;
    private int targetHeight;

    String getName() {
        if (uri != null) return String.valueOf(uri);
        return null;
    }

    boolean hasImage() {
        return uri != null;
    }

    private Request(Uri uri, int targetWidth, int targetHeight) {
        this.uri = uri;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public Uri getUri() {
        return uri;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public boolean isSetSize() {
        return targetWidth != 0 || targetHeight != 0;
    }

    public static class Builder {
        Uri uri;

        int targetWidth;
        int targetHeight;

        public Builder setUri(Uri uri) {
            this.uri = uri;
            return this;
        }


        Builder setTargetSize(int targetWidth, int targetHeight) {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            return this;
        }

        Request build() {
            return new Request(uri, targetWidth, targetHeight);
        }
    }
}
