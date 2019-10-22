package com.liany.cameratest.google;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.google.android.cameraview.CameraView;
import com.liany.cameratest.PhotoViewActivity;
import com.liany.cameratest.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 ly
 * @创建时间 2019/8/28
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class CameraActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{

    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.iv_left_flash)
    ImageView ivLeftFlash;
    @BindView(R.id.iv_camera_front)
    ImageView ivCameraFront;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;

    private String TAG = "camera";
    //用于在子线程中处理图片数据
    private Handler mBackgroundHandler;
    private ProgressDialog dialog;
    private String filePath = "";

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };
    private static final int[] FLASH_ICONS = {
            R.mipmap.ic_camera_top_bar_flash_auto_normal,
            R.mipmap.ic_camera_top_bar_flash_off_normal,
            R.mipmap.ic_camera_top_bar_flash_torch_normal,
    };
    private int mCurrentFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_camera);
        ButterKnife.bind(this);
        //添加相机监听回调
        if (camera != null) {
            camera.addCallback(mCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @OnClick({R.id.iv_left_flash, R.id.iv_camera_front, R.id.iv_back, R.id.iv_img, R.id.iv_camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left_flash:
                if (camera != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    ivLeftFlash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    camera.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                break;
            case R.id.iv_camera_front:
                if (camera != null) {
                    int facing = camera.getFacing();
                    camera.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_img:
                if(!filePath.equals("")) {
                    Intent intent = new Intent(this,PhotoViewActivity.class);
                    intent.putExtra("filePath",filePath);
                    startActivity(intent);
                }
                break;
            case R.id.iv_camera:
                if(camera != null) {
                    dialog = ProgressDialog.show(this, "", "拍照中", true, true);
                    camera.takePicture();
                }
                break;
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }


    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    //在子线程中保存图片
                    final File file = new File(Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg");
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                filePath = file.getAbsolutePath();
                                ivImg.setImageBitmap(bitmap);
                            }
                        });
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
            });
        }
    };
}
