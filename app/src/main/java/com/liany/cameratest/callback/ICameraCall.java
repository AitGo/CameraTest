package com.liany.cameratest.callback;


//一个类只能集成一个类，但可以实现很多个接口

public interface ICameraCall {
    // 相机拍照接口
    void onCameraData(byte[] data);
}
