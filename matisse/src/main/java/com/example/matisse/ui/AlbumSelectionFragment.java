package com.example.matisse.ui;

import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;

import androidx.fragment.app.Fragment;

public class AlbumSelectionFragment extends Fragment {




    public interface ItemClickListener {
        void itemClick(Album album, Item item);
    }

}
