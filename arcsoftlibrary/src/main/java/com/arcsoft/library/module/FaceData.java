package com.arcsoft.library.module;

/**
 * Created by Administrator on 2017/8/3.
 */

public class FaceData {
    private byte[] nv21;
    private int width;
    private int height;
    private int orientation;

    public FaceData(byte[] nv21, int width, int height, int orientation) {
        this.nv21 = nv21;
        this.width = width;
        this.height = height;
        this.orientation = orientation;
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

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
