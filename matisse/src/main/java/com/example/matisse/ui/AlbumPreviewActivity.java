package com.example.matisse.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.adapter.PreviewAdapter;
import com.example.matisse.collection.AlbumMediaCollection;
import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;
import com.example.matisse.entity.ItemSelectedCollection;
import com.example.matisse.ui.widget.CheckView;
import com.example.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlbumPreviewActivity extends AppCompatActivity implements View.OnClickListener,
        AlbumMediaCollection.AlbumMediaCallback {

    public static final String ALBUM_DATA = "album";
    public static final String ITEM_DATA = "item";
    public static final String BUNDLE_DATA = "bundle";


    private TextView percentText;
    private Button send;
    private ViewPager viewPager;
    private CheckView checkView;
    private int currentPosition;
    private AlbumMediaCollection collection;
    private ArrayList<Item> all = new ArrayList<>();
    private ArrayList<Item> selectedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Picasso.with(this).clearCache();
        init();
    }

    private void init() {
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_DATA);
        if (bundle != null) {
            List<Item> data = bundle.getParcelableArrayList(ItemSelectedCollection.DATA_KEY);
            if (data != null) selectedData.addAll(data);
            collection = new AlbumMediaCollection();
            collection.onCreate(this, this);
            Album album = bundle.getParcelable(ALBUM_DATA);
            collection.loadItems(album);
        }
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(this);
        percentText = findViewById(R.id.percent_text);
        send = findViewById(R.id.selected_preview_send);
        send.setOnClickListener(this);
        Button select = findViewById(R.id.preview_select);
        select.setOnClickListener(this);
        checkView = findViewById(R.id.preview_check);
        checkView.setOnClickListener(this);
        viewPager = findViewById(R.id.show_selected_image);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String string = (position + 1) + "/" + all.size();
                percentText.setText(string);
                currentPosition = position;
                check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getReturnData());
        finish();
    }

    private void check(int position) {
        Item item = all.get(position);
        if (selectedData.indexOf(item) != -1) {
            checkView.setChecked(true);
        } else checkView.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            onBackPressed();
        } else if (id == R.id.send) {
            setResult(RESULT_OK, getReturnData());
            finish();
        } else if (id == R.id.preview_check || id == R.id.preview_select) {
            if (isSelected()) {
                selectedData.remove(all.get(currentPosition));
                checkView.setChecked(false);
            } else {
                selectedData.add(all.get(currentPosition));
                checkView.setChecked(true);
            }
            updateSendButton();
        }
    }

    private void updateSendButton() {
        StringBuilder builder = new StringBuilder();
        builder.append("发送");
        if (selectedData.size() > 0) {
            builder.append(selectedData.size()).append("/").append(all.size());
            send.setBackgroundResource(R.color.selected_color);
            send.setClickable(true);
        } else {
            send.setBackgroundResource(R.color.no_selected_color);
            send.setClickable(false);
        }
        send.setText(builder.toString());

    }

    private boolean isSelected() {
        Item item = all.get(currentPosition);
        return selectedData.indexOf(item) != -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        collection.onDestroy();
    }

    @Override
    public void onFinished(Cursor cursor) {
        while (cursor.moveToNext()) {
            Item item = Item.valueOf(cursor);
            all.add(item);
        }
        initPage();
    }

    @Override
    public void onReset() {

    }

    // 初始化viewpager
    private void initPage() {
        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < all.size(); i ++) {
            View view = LayoutInflater.from(this).inflate(R.layout.preview_item, viewPager, false);
            viewList.add(view);
        }
        PreviewAdapter adapter = new PreviewAdapter(viewList, all);
        viewPager.setAdapter(adapter);
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_DATA);
        if (bundle != null) {
            Item item = bundle.getParcelable(ITEM_DATA);
            int position = all.indexOf(item);
            currentPosition = position;
            viewPager.setCurrentItem(position);
            String text = (position + 1) + "/" + all.size();
            percentText.setText(text);
            check(position);
        }
        updateSendButton();
    }

    private Intent getReturnData() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ItemSelectedCollection.DATA_KEY, selectedData);
        intent.putExtra(MatisseActivity.RETURN_CODE, bundle);
        return intent;
    }
}

