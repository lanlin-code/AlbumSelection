package com.example.matisse.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;

import com.example.matisse.R;
import com.example.matisse.adapter.PopupAdapter;
import com.example.matisse.adapter.SelectionAdapter;
import com.example.matisse.collection.AlbumCollection;
import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;
import com.example.matisse.entity.ItemSelectedCollection;
import com.example.matisse.entity.SelectionSpec;

import java.util.List;

public class MatisseActivity extends AppCompatActivity implements View.OnClickListener,
        AlbumCollection.AlbumCallback, SelectionAdapter.CheckClickListener, AlbumSelectionFragment.ItemClickListener {

    private AlbumCollection collection;
    private PopupAdapter popupAdapter;
    private ListPopupWindow popupWindow;
    private Button showAlbumName;
    private Button send;
    private Button goPreview;

    private ItemSelectedCollection selectedCollection = new ItemSelectedCollection();
    private final int PREVIEW = 1;
    public static final String RETURN_CODE = "data_return";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse);
        ImageButton exit = findViewById(R.id.exit);
        showAlbumName = findViewById(R.id.album_select);
        send = findViewById(R.id.send);
        goPreview = findViewById(R.id.go_preview_item);
        goPreview.setOnClickListener(this);
        popupWindow = new ListPopupWindow(this);
        popupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = popupAdapter.getCursor();
                collection.setCurrentPosition(position);
                cursor.moveToPosition(position);
                Album album = Album.valueOf(cursor);
                showAlbumName.setText(album.getDisplayName());
                onAlbumSelected(album);
                popupWindow.dismiss();
            }
        });

        popupWindow.setAnchorView(showAlbumName);
        send.setOnClickListener(this);
        showAlbumName.setOnClickListener(this);
        exit.setOnClickListener(this);


        collection = new AlbumCollection();
        collection.onCreate(this, this);
        collection.loadAlbum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            } else {
                if (data != null) {
                    Bundle bundle = data.getBundleExtra(RETURN_CODE);
                    if (bundle != null) {
                        List<Item> list = bundle.getParcelableArrayList(ItemSelectedCollection.DATA_KEY);
                        selectedCollection.renew(list);
                        Fragment fragment = getSupportFragmentManager().
                                findFragmentByTag(AlbumSelectionFragment.class.getSimpleName());
                        if (fragment instanceof AlbumSelectionFragment) {
                            ((AlbumSelectionFragment) fragment).update();
                        }

                        updateBar();
                   }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.exit) {
            finish();
        } else if (id == R.id.album_select) {
            if (popupWindow.isShowing()) popupWindow.dismiss();
            else popupWindow.show();
        } else if (id == R.id.go_preview_item) {
//            Intent intent = new Intent(this, SelectedPreviewActivity.class);
//            intent.putExtra(SelectedPreviewActivity.KEY, selectedCollection.getItems());
//            startActivityForResult(intent, PREVIEW);
        } else if (id == R.id.send) {
            Intent data = new Intent();
            Bundle bundle = selectedCollection.getItems();
            data.putExtra(RETURN_CODE, bundle);
            setResult(RESULT_OK, data);
            finish();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        collection.onDestroy();
    }

    @Override
    public void onAlbumFinish(Cursor cursor) {


        if (popupAdapter == null) {
            popupAdapter = new PopupAdapter(this, cursor, false);
            popupWindow.setAdapter(popupAdapter);
        } else {
            popupAdapter.swapCursor(cursor);
        }
        cursor.moveToPosition(collection.getCurrentPosition());
        Album album = Album.valueOf(cursor);
        showAlbumName.setText(album.getDisplayName());
        onAlbumSelected(album);
    }


    @Override
    public void onAlbumReset() {
        popupAdapter.swapCursor(null);

        collection.setCurrentPosition(0);
        showAlbumName.setText(R.string.send_img);
    }

    // 更新顶部和底部操作栏
    private void updateBar() {
        Resources resources = getResources();
        if (selectedCollection.getSize() == 0) {
            String text =resources.getString(R.string.send_img);
            updateButtonState(send, text, R.color.no_selected_color, false);
            text = resources.getString(R.string.preview);
            updateButtonState(goPreview, text, R.color.black, false);
        } else {
            String s = resources.getString(R.string.send_img) +
                    selectedCollection.getSize() + resources.getString(R.string.fen)
                    + SelectionSpec.newInstance().maxSelection;
            updateButtonState(send, s, R.color.selected_color, true);
            s = resources.getString(R.string.preview) + resources.getString(R.string.left)
                    + selectedCollection.getSize() + resources.getString(R.string.right);
            updateButtonState(goPreview, s, R.color.black, true);
        }

    }

    // 更新button显示的文本，背景等
    private void updateButtonState(Button button, String text, int backgroundColor,
                                   boolean clickable) {
        button.setText(text);
        button.setBackgroundResource(backgroundColor);
        button.setClickable(clickable);
    }

    // 显示照片墙
    private void onAlbumSelected(Album album) {

        getSupportFragmentManager().beginTransaction().
                replace(R.id.show_photos_fragment, AlbumSelectionFragment.newInstance(album),
                        AlbumSelectionFragment.class.getSimpleName()).commit();
    }


    // 把选择的图片添加到集合里
    @Override
    public void add(Item item) {
        selectedCollection.add(item);
        updateBar();
    }

    // 得到该图片在集合的位置
    @Override
    public int getPosition(Item item) {
        return selectedCollection.indexOf(item);
    }

    // 移除图片
    @Override
    public void remove(Item item) {
        selectedCollection.remove(item);
        updateBar();
    }

    @Override
    public int size() {
        return selectedCollection.getSize();
    }



    @Override
    public void itemClick(Album album, Item item) {
//        Intent intent = new Intent(this, AlbumPreviewActivity.class);
//        Bundle bundle = selectedCollection.getItems();
//        bundle.putParcelable(AlbumPreviewActivity.ALBUM_DATA, album);
//        bundle.putParcelable(AlbumPreviewActivity.ITEM_DATA, item);
//        intent.putExtra(AlbumPreviewActivity.BUNDLE_DATA, bundle);
//        startActivityForResult(intent, PREVIEW);
    }
}

