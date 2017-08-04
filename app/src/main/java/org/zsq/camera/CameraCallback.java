package org.zsq.camera;

import android.hardware.Camera;

/**
 * Created by zsq on 2017/7/28.
 *
 */

public interface CameraCallback {
    public void onPreviewFrame(byte[] bytes, Camera camera);
}
