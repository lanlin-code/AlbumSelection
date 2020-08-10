package com.example.matisse.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.adapter.SelectionAdapter;
import com.example.matisse.collection.AlbumMediaCollection;
import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;
import com.example.matisse.entity.SelectionSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumSelectionFragment extends Fragment implements AlbumMediaCollection.AlbumMediaCallback,
        SelectionAdapter.ImageClickListener {

    private static final String ALBUM_KEY = "album_key";
    private RecyclerView mRecyclerView;
    private AlbumMediaCollection mAlbumMediaCollection;
    private SelectionAdapter mSelectionAdapter;
    private SelectionAdapter.CheckClickListener mCheckListener;
    private ItemClickListener mItemClickListener;

    // 创建一个实例，并把传入的Album对象保存
    public static AlbumSelectionFragment newInstance(Album album) {
        AlbumSelectionFragment fragment = new AlbumSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ALBUM_KEY, album);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 得到两个回调接口
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 提供选择的数据的接口,检查是否应该显示checkview
        if (context instanceof SelectionAdapter.CheckClickListener)
            mCheckListener = (SelectionAdapter.CheckClickListener) context;
        if (context instanceof ItemClickListener)
            mItemClickListener = (ItemClickListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_album_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.album_select_list);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Album album = bundle.getParcelable(ALBUM_KEY);
            mAlbumMediaCollection = new AlbumMediaCollection();
            mAlbumMediaCollection.onCreate(getActivity(), this);
            mAlbumMediaCollection.loadItems(album);
            int gridCount = SelectionSpec.newInstance().spanCount;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), gridCount);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCheckListener = null;
        mItemClickListener = null;
        mSelectionAdapter.unregisterCheckListener();
        mSelectionAdapter.unregisterImageClickListener();
        mSelectionAdapter.clearSelectedView();
        mAlbumMediaCollection.onDestroy();
    }

    @Override
    public void onFinished(Cursor cursor) {
        if (mSelectionAdapter == null) {
            mSelectionAdapter = new SelectionAdapter();
            mSelectionAdapter.swapCursor(cursor);
            mSelectionAdapter.registerCheckListener(mCheckListener);
            mSelectionAdapter.registerImageClickListener(this);
            mRecyclerView.setAdapter(mSelectionAdapter);
        } else mSelectionAdapter.swapCursor(cursor);
    }

    @Override
    public void onReset() {
        mSelectionAdapter.swapCursor(null);
    }

    public void update() {
        mSelectionAdapter.onUpdate();
    }


    @Override
    public void imageClick(Item item) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Album album = bundle.getParcelable(ALBUM_KEY);
            mItemClickListener.itemClick(album, item);
        }

    }


    public interface ItemClickListener {
        void itemClick(Album album, Item item);
    }

}
