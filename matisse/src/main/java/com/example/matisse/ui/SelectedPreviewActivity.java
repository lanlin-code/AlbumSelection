package com.example.matisse.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.adapter.PreviewAdapter;
import com.example.matisse.entity.Item;
import com.example.matisse.entity.ItemSelectedCollection;
import com.example.matisse.ui.widget.CheckView;
import com.example.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SelectedPreviewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY = "selected_item";
    private ArrayList<Item> data = new ArrayList<>();
    private List<Item> removeData = new ArrayList<>();
    private TextView percentText;
    private Button send;
    private ViewPager imagePager;
    private CheckView checkView;
    private int currentPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Picasso.with(this).clearCache();
        init();
    }


    private void init() {
        Bundle bundle = getIntent().getBundleExtra(KEY);
        if (bundle != null) data = bundle.getParcelableArrayList(ItemSelectedCollection.DATA_KEY);
        ImageButton back = findViewById(R.id.back);
        send = findViewById(R.id.selected_preview_send);
        checkView = findViewById(R.id.preview_check);
        Button select = findViewById(R.id.preview_select);
        percentText = findViewById(R.id.percent_text);
        String string = "1/" + data.size();
        percentText.setText(string);
        updateButton(send,true);
        imagePager = findViewById(R.id.show_selected_image);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        select.setOnClickListener(this);
        checkView.setOnClickListener(this);
        addChildViews();
        check(currentPosition);
    }

    // 更新按钮
    private void updateButton(Button button, boolean clickable) {
        String string = "发送" + (data.size() - removeData.size()) + "/" + data.size();
        button.setText(string);
        button.setClickable(clickable);
        if (clickable) button.setBackgroundResource(R.color.selected_color);
        else button.setBackgroundResource(R.color.no_selected_color);
    }


    // 初始化viewpager
    private void addChildViews() {
        List<View> children = new ArrayList<>();
        for (int i = 0; i < data.size(); i ++) {
            View view = LayoutInflater.from(this).inflate(R.layout.preview_item, imagePager, false);
            children.add(view);
        }
        PreviewAdapter adapter = new PreviewAdapter(children, data);
        imagePager.setAdapter(adapter);
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String string = (position + 1) + "/" + data.size();
                percentText.setText(string);
                check(position);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.preview_check || id == R.id.preview_select) {
            if (checkView.getChecked()) {
                removeData.add(data.get(currentPosition));
                checkView.setChecked(false);
                if (removeData.size() < data.size()) {
                    updateButton(send, true);
                } else updateButton(send, false);
            } else {
                removeData.remove(data.get(currentPosition));
                checkView.setChecked(true);
                updateButton(send, true);
            }
        } else if (id == R.id.back) {
            setResult(RESULT_CANCELED, getReturnIntent());
            finish();
        } else if (id == R.id.send) {
            setResult(RESULT_OK, getReturnIntent());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getReturnIntent());
        finish();
    }

    // 得到返回数据
    private Intent getReturnIntent() {
        data.removeAll(removeData);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ItemSelectedCollection.DATA_KEY, data);
        Intent intent = new Intent();
        intent.putExtra(MatisseActivity.RETURN_CODE, bundle);
        return intent;
    }



    // 更新顶部操作栏
    private void check(int position) {
        Item item = data.get(position);
        if (removeData.indexOf(item) != -1) {
            checkView.setChecked(false);
        } else checkView.setChecked(true);
    }
}
