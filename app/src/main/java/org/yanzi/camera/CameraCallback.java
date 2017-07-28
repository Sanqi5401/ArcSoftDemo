package org.yanzi.camera;

import android.hardware.Camera;

/**
 * Created by Administrator on 2017/7/28.
 */

public interface CameraCallback {
    public void onPreviewFrame(byte[] bytes, Camera camera);
}
