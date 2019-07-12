package com.liany.cameratest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 */

public class FileUtils {
    private Context context;
    private final DisplayMetrics dm;

    public FileUtils(Context context) {
        this.context = context;
        dm = context.getResources().getDisplayMetrics();
    }

    // 保存图片到sd卡中
    public File saveToSDCard(byte[] data, String savePath) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap waterMarkBitmap = createWaterMarkBitmap(bitmap);
        File fileFolder = new File(savePath);

        if (!fileFolder.exists()) // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdirs();

        File jpgFile = new File(fileFolder, System.currentTimeMillis() + ".jpeg");
        if (jpgFile.exists()) jpgFile.delete();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(jpgFile);
            waterMarkBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("lrxc", "saveToSDCard: " + e.getMessage());
        } finally {
            try {
                if (fos != null) fos.close(); // 关闭输出流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jpgFile;
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
     * 获取遮罩内的图像
     *
     * @param rect 遮罩矩形
     * @param bm   原图
     * @return 矩形内图像
     */
    private Bitmap getRectBmp(Bitmap bm, Rect rect) {
        int width = rect.right - rect.left;
        int hight = rect.bottom - rect.top;
        Log.i("ddms", "getRectBmp: " + rect.left + " " + rect.top + " " + width + " " + hight + " " + bm.getWidth() + " " + bm.getHeight());

        Bitmap rectbitmap = null;
        //横竖屏--按比例割取图片
        if (CameraParams.getInstance().oritation == 90) {
            int dpWidth = bm.getWidth();
            int dpHeight = bm.getHeight() * hight / dm.heightPixels;
            rectbitmap = Bitmap.createBitmap(bm, 0, bm.getHeight() * rect.top / dm.heightPixels, dpWidth, dpHeight);
        } else if (CameraParams.getInstance().oritation == 0) {
            int dpWidth = bm.getWidth() * width / dm.widthPixels;
            int dpHeight = bm.getHeight();
            rectbitmap = Bitmap.createBitmap(bm, bm.getWidth() * rect.left / dm.widthPixels, 0, dpWidth, dpHeight);
        }
        if (!bm.isRecycled()) {
            bm.recycle();
        }
        return rectbitmap;
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return 缩放后图片
     */
    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }
}
