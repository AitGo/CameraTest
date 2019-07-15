package com.liany.cameratest;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

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
public class PhotoViewActivity extends Activity {

    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        ButterKnife.bind(this);
        String filePath = getIntent().getStringExtra("filePath");
        ivPhoto.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
