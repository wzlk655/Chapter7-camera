package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;

public class RecordVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_CAMERA = 101;

    private File videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        videoView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(RecordVideoActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                ActivityCompat.requestPermissions(RecordVideoActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RecordVideoActivity.REQUEST_VIDEO_CAPTURE);
            } else {
                recordVideo();
            }
        });
        findViewById(R.id.btn_pause).setOnClickListener(v -> {
            if(videoView.isPlaying())
                videoView.pause();
            else
                videoView.start();
        });
    }

    private void recordVideo(){
        //todo 打开相机拍摄
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                videoFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_VIDEO);
        if(recordVideoIntent.resolveActivity(getPackageManager())!=null){
//                    Uri fileUri =
//                            FileProvider.getUriForFile(this,"com.bytedance.camera.demo",videoFile);
//                    recordVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(recordVideoIntent,REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //todo 播放刚才录制的视频
            if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
                Uri videoUri = intent.getData();
                videoView.setVideoURI(videoUri);
                videoView.start();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_CAMERA: {
                //todo 判断权限是否已经授予
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    recordVideo();
                break;
            }
        }
    }
}
