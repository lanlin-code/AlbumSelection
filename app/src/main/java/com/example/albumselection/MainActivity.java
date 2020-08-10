package com.example.albumselection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.matisse.Matisse;
import com.example.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int code = 1;
    private ListView listView;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else init();


    }

    private void init() {
        Matisse.from(this).build().SetCountable(true).SetMaxSelectionSize(9).
                setSpanCount(4).setThumbnailScale(1.0f).forResult(code);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.selected_list);
        button = findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == code && resultCode == RESULT_OK) {
            List<Uri> list = Matisse.getSelectedUri(data);
            Picasso.with(this).clearCache();
            AlbumArrayAdapter albumArrayAdapter = new AlbumArrayAdapter(this, R.layout.relative_layout_item, list);
            listView.setAdapter(albumArrayAdapter);
        } else finish();
    }
}
