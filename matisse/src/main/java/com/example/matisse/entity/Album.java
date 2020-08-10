package com.example.matisse.entity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.matisse.loader.AlbumLoader;

import androidx.annotation.NonNull;

public class Album implements Parcelable{
    private final String mId;
    private final Uri mCoverUri;
    private final String mDisplayName;
    private long mCount;

    public Album(String id, Uri coverUri, String albumName, long count) {
        mId = id;
        mCoverUri = coverUri;
        mDisplayName = albumName;
        mCount = count;
    }

    protected Album(Parcel in) {
        mId = in.readString();
        mCoverUri = in.readParcelable(Uri.class.getClassLoader());
        mDisplayName = in.readString();
        mCount = in.readLong();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mCoverUri, flags);
        dest.writeString(mDisplayName);
        dest.writeLong(mCount);
    }

    public static Album valueOf(Cursor cursor) {
        String clumn = cursor.getString(cursor.getColumnIndex(AlbumLoader.COLUMN_URI));
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                Uri.parse(clumn != null ? clumn : ""),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
    }

    public String getId() {
        return mId;
    }

    public Uri getCoverUri() {
        return mCoverUri;
    }

    public long getCount() {
        return mCount;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public boolean isEmpty() {
        return mCount == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "uri=" + mCoverUri + ",name=" + mDisplayName + ",count=" + mCount;
    }
}
