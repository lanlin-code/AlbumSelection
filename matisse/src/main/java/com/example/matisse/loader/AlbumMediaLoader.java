package com.example.matisse.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.matisse.entity.Album;

import androidx.loader.content.CursorLoader;

public class AlbumMediaLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};


    // === params for ordinary album && showSingleMediaType: true ===
    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }


    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";


    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, Album album) {
        String selection;
        String[] selectionArgs;
        selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
        selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, album.getId());
        return new AlbumMediaLoader(context, selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }

}
