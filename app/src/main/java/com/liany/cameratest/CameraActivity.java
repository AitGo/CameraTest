package com.liany.cameratest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.liany.cameratest.callback.ICameraCall;
import com.liany.cameratest.camera.CameraPreviewView;
import com.liany.cameratest.centerPopup.OrientationPopup;
import com.liany.cameratest.utils.CameraParams;
import com.liany.cameratest.utils.FileUtils;
import com.liany.cameratest.utils.ScaleGestureListener;
import com.lxj.xpopup.XPopup;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 ly
 * @创建时间 2019/7/12
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class CameraActivity extends AppCompatActivity implements ICameraCall {

    @BindView(R.id.camePreview)
    CameraPreviewView camePreview;
    @BindView(R.id.iv_left_flash)
    ImageView ivLeftFlash;
    @BindView(R.id.iv_camera_front)
    ImageView ivCameraFront;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;

    public String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cameraTest/";
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.camera_take)
    RelativeLayout cameraTake;
    @BindView(R.id.fl_camera)
    FrameLayout flCamera;

    private ScaleGestureDetector gestureDetector;//缩放手势
    private int mSgType = 2;//1 不开启闪光灯 2自动 3长亮
    private ProgressDialog dialog;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapreview);
        ButterKnife.bind(this);

        camePreview.setOnCameraListener(this);
        //缩放手势
        gestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener(camePreview));
    }

    @OnClick({R.id.iv_left_flash, R.id.iv_camera_front, R.id.iv_back, R.id.iv_camera, R.id.camePreview, R.id.iv_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_flash:
                //闪光灯模式
                switchFlashMode();
                break;
            case R.id.iv_camera_front:
                //切换摄像头
                camePreview.switchFrontCamera();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                dialog = ProgressDialog.show(this, "", "拍照中", true, true);
                camePreview.takePicture(); //拍照
                break;
            case R.id.camePreview:
                //手动对焦
//                camePreview.autoFocus();
                break;
            case R.id.iv_img:
                Intent intent = new Intent(CameraActivity.this, PhotoViewActivity.class);
                intent.putExtra("filePath", file.getAbsolutePath());
                startActivity(intent);
                break;
        }
    }

    //切换闪关灯模式
    private void switchFlashMode() {
        switch (mSgType) {
            case 1:
                ivLeftFlash.setImageResource(R.mipmap.ic_camera_top_bar_flash_auto_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mSgType = 2;
                break;
            case 2:
                ivLeftFlash.setImageResource(R.mipmap.ic_camera_top_bar_flash_torch_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mSgType = 3;
                break;
            case 3:
                ivLeftFlash.setImageResource(R.mipmap.ic_camera_top_bar_flash_off_normal);
                camePreview.setIsOpenFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mSgType = 1;
                break;
        }
    }

    @Override
    public void onCameraData(byte[] data) {
        //在构造方法中获取屏幕尺寸，以备后用
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenHeight = display.getHeight();
        int screenWidth = display.getWidth();

        //裁剪图片
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        file = new FileUtils(this).saveToSDCard(bitmap, filePath);
        Bitmap waterMarkBitmap = createWaterMarkBitmap(bitmap);
        waterMarkBitmap = cropBitmap(waterMarkBitmap);
        //保存到本地
        file = new FileUtils(this).saveToSDCard(waterMarkBitmap, filePath);
        dialog.dismiss();
        camePreview.start();
        ivImg.setVisibility(View.VISIBLE);
        ivImg.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        //显示弹窗
        new XPopup.Builder(this)
                .asCustom(new OrientationPopup(this))
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手势缩放识别手势
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //旋转图片
    private Bitmap createWaterMarkBitmap(Bitmap src) {
        //设置bitmap旋转
        Matrix matrix = new Matrix();
        matrix.setRotate(CameraParams.getInstance().oritation);
        src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return src;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
//        int cropHeight = (int) (cropWidth / 1.2);
        int width = (int) (w * 0.5f);
        int height = (int) (h * 0.36f);
        int left = (w - width) / 2;
        int top = (h - height) / 2;
//        int right = width + left;
//        int bottom = height + top;
        return Bitmap.createBitmap(bitmap, left, top, width, height, null, false);
    }

}
