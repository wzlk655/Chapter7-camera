package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int REQUEST_CAMERA_PERMITION = 10;
    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    //private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMITION = 1010;
    private  File imgFile;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                ActivityCompat.requestPermissions(TakePictureActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        TakePictureActivity.REQUEST_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(TakePictureActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        TakePictureActivity.REQUEST_IMAGE_CAPTURE);
            } else {
                takePicture();
            }
        });

    }

    private void takePicture() {
        //todo 打开相机
        Intent takePictrueIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePictrueIntent, REQUEST_IMAGE_CAPTURE);
        imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if(imgFile!=null){
            Uri fileUri =
                    FileProvider.getUriForFile(this,"com.bytedance.camera.demo", imgFile);
            takePictrueIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(takePictrueIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Intent takePictrueIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
//            if(imgFile!=null){
//                Uri fileUri =
//                        FileProvider.getUriForFile(this,"com.bytedance.camera.demo", imgFile);
//                takePictrueIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
//                startActivityForResult(takePictrueIntent,REQUEST_IMAGE_CAPTURE);
//            }
            setPic();
        }
    }

    private void setPic() {
        //todo 根据imageView裁剪
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        //todo 根据缩放比例读取文件，生成Bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        Toast.makeText(TakePictureActivity.this,"Stored in" + imgFile.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);

        //todo 如果存在预览方向改变，进行图片旋转
        try{
            Bitmap bitmap = rotateImage(bmp,imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
//                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    takePicture();
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    takePicture();
                break;
            }
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, String path)throws Exception{
        ExifInterface srcExif = new ExifInterface(path);
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation){
            case(ExifInterface.ORIENTATION_ROTATE_90):
                angle = 90;
                break;
            case(ExifInterface.ORIENTATION_ROTATE_180):
                angle = 180;
                break;
            case(ExifInterface.ORIENTATION_ROTATE_270):
                angle = 270;
                break;
            default:
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }
}
