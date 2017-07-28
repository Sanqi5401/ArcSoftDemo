package org.yanzi.model;

import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKFace;

import org.yanzi.callback.FaceCallBack;
import org.yanzi.util.FaceManager;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public class FaceCMD {
    private byte[] data;
    private int width;
    private int height;
    private FaceCallBack callBack;

    public FaceCMD(byte[] data, int width, int height, FaceCallBack callBack) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.callBack = callBack;
    }

    public byte[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public FaceCallBack getCallBack() {
        return callBack;
    }

    public void execute() {
        List<AFD_FSDKFace> list = FaceManager.getInstant(callBack).detection(data, width, height);
        if (list != null && list.size() > 0) {
            Log.e("Tag","face degress =  " + list.get(0).getDegree() + list.get(0).getRect().toString());
            AFR_FSDKFace face = FaceManager.getInstant(callBack).recognize(data, width, height, list.get(0).getRect());
            if (face != null) {
                FaceManager.getInstant(callBack).match(face);
            }
        }
        init();
    }

    private void init() {
        data = null;
        width = 0;
        height = 0;
        callBack = null;
    }
}
