package com.arcsoft.library.module;

import com.arcsoft.facedetection.AFD_FSDKFace;

import java.util.List;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FaceData {
    private byte[] nv21;
    private int width;
    private int height;

    public FaceData(byte[] nv21, int width, int height) {
        this.nv21 = nv21;
        this.width = width;
        this.height = height;
    }
    public byte[] getNv21() {
        return nv21;
    }

    public void setNv21(byte[] nv21) {
        this.nv21 = nv21;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
