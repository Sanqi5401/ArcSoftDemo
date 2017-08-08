package com.arcsoft.library.module;

import android.graphics.Rect;

import com.arcsoft.facedetection.AFD_FSDKFace;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ArcsoftFace {
    Rect mRect;
    int mDegree;

    public ArcsoftFace(AFD_FSDKFace self) {
        this.mRect = new Rect(self.getRect());
        this.mDegree = self.getDegree();
    }

    public ArcsoftFace() {
        this.mRect = new Rect();
        this.mDegree = 0;
    }

    public Rect getRect() {
        return this.mRect;
    }

    public int getDegree() {
        return this.mDegree;
    }

    public String toString() {
        return this.mRect.toString() + "," + this.mDegree;
    }
}
