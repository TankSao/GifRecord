package com.example.administrator.gifrecord;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Thread thread;
    private boolean isStop = false;
    private int delay = 2000;//间隔时间
    private List<String> pics = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT >= 23){
            requestAllPower();
        }
    }
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @OnClick({R.id.start,R.id.end})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.start:
                isStop = false;
                thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        while (!isStop){
                            shootAndPic();
                            try {
                                sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
                break;
            case R.id.end:
                if(thread!=null){
                    isStop = true;
                    try{
                        thread.interrupt();
                    }catch (Exception e){
                        Log.e("error",e.getMessage()+"");
                    }
                }
                makeGif();
                break;
        }
    }

    private void makeGif() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GifEncoder localAnimatedGifEncoder = new GifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
        if (pics.isEmpty()) {
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground));
        } else {
            for (int i = 0; i < pics.size(); i++) {
                localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(pics.get(i)));
            }
        }
        localAnimatedGifEncoder.finish();//finish

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/GifRecord/GifImage");
        if (!file.exists()) file.mkdir();
        String path = Environment.getExternalStorageDirectory().getPath() + "/GifRecord/GifImage/" + System.currentTimeMillis() + ".gif";

        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("resultGif","Gif已生成。保存路径：\n" + path);
        Intent intent = new Intent(MainActivity.this,GifViewActivity.class);
        intent.putExtra("gif_path",path);
        startActivity(intent);
    }

    //截图并保存
    private void shootAndPic() {
        //构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );

        //获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        String SavePath = getSDCardPath()+"/GifRecord/ScreenImage";

        //保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath + "/"+System.currentTimeMillis()+".png";
            pics.add(filepath);
            File file = new File(filepath);
            if(!path.exists()){
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Log.e("result","截屏文件已保存至SDCard/GifRecord/ScreenImage/下");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取SDCard的目录路径功能
     * @return
     */
    private String getSDCardPath(){
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

}
