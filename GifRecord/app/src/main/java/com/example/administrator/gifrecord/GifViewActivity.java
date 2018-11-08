package com.example.administrator.gifrecord;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/11/8.
 */

public class GifViewActivity extends AppCompatActivity{
    @BindView(R.id.gif)
    ImageView gif;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_view);
        ButterKnife.bind(this);
        String path = getIntent().getStringExtra("gif_path");
        Glide.with(this).load(path).asGif().fitCenter().into(gif);
    }
}
