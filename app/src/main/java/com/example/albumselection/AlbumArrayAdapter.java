package com.example.albumselection;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlbumArrayAdapter extends ArrayAdapter<Uri> {

    private int resource;
    private List<Uri> uris;

    public AlbumArrayAdapter(@NonNull Context context, int resource, @NonNull List<Uri> objects) {
        super(context, resource, objects);
        this.resource = resource;
        uris = objects;
    }

    @Override
    public int getCount() {
        return uris == null ? 0 : uris.size();
    }

    @Nullable
    @Override
    public Uri getItem(int position) {
        return uris.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.show_photo);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Picasso.with(parent.getContext()).load(getItem(position)).resize(100, 150)
                .placeHolder(R.drawable.ic_android_black_24dp).into(viewHolder.imageView);
        return view;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
