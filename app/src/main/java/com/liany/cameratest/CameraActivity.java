package com.liany.cameratest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.liany.cameratest.callback.ICameraCall;
import com.liany.cameratest.camera.CameraPreviewView;
import com.liany.cameratest.utils.FileUtils;
import com.liany.cameratest.utils.ScaleGestureListener;

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

    private ScaleGestureDetector gestureDetector;//缩放手势
    private int mSgType = 2;//1 不开启闪光灯 2自动 3长亮
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerapreview);
        ButterKnife.bind(this);

        camePreview.setOnCameraListener(this);
        //缩放手势
        gestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener(camePreview));
    }

    @OnClick({R.id.iv_left_flash, R.id.iv_camera_front, R.id.iv_back, R.id.iv_camera, R.id.camePreview})
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
        //保存到本地
        File file = new FileUtils(this).saveToSDCard(data, filePath);
        dialog.dismiss();
        camePreview.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手势缩放识别手势
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}
