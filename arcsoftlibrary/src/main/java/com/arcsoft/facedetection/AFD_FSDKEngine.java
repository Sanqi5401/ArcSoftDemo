//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.arcsoft.facedetection;

import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import java.util.List;

public class AFD_FSDKEngine {
    private final String TAG = this.getClass().toString();
    public static final int CP_PAF_NV21 = 2050;
    public static final int AFD_OPF_0_ONLY = 1;
    public static final int AFD_OPF_90_ONLY = 2;
    public static final int AFD_OPF_270_ONLY = 3;
    public static final int AFD_OPF_180_ONLY = 4;
    public static final int AFD_OPF_0_HIGHER_EXT = 5;
    public static final int AFD_FOC_0 = 1;
    public static final int AFD_FOC_90 = 2;
    public static final int AFD_FOC_270 = 3;
    public static final int AFD_FOC_180 = 4;
    public static final int AFD_FOC_30 = 5;
    public static final int AFD_FOC_60 = 6;
    public static final int AFD_FOC_120 = 7;
    public static final int AFD_FOC_150 = 8;
    public static final int AFD_FOC_210 = 9;
    public static final int AFD_FOC_240 = 10;
    public static final int AFD_FOC_300 = 11;
    public static final int AFD_FOC_330 = 12;
    private Integer handle = Integer.valueOf(0);
    private AFD_FSDKError error = new AFD_FSDKError();
    private AFD_FSDKFace[] mFaces = new AFD_FSDKFace[16];
    private int mFaceCount = 0;

    private native int FD_Init(String var1, String var2, int var3, int var4, int var5, AFD_FSDKError var6);

    private native String FD_Version(int var1);

    private native int FD_Process(int var1, byte[] var2, int var3, int var4, int var5);

    private native int FD_Config(int var1, int var2);

    private native int FD_GetErrorCode(int var1);

    private native int FD_GetResult(int var1, AFD_FSDKFace[] var2);

    private native int FD_UnInit(int var1);

    public AFD_FSDKEngine() {
    }

    private AFD_FSDKFace[] obtainFaceArray(int size) {
        if(this.mFaceCount < size) {
            if(this.mFaces.length < size) {
                this.mFaces = new AFD_FSDKFace[(size / 16 + 1) * 16];
            }

            for(int i = this.mFaceCount; i < size; ++i) {
                this.mFaces[i] = new AFD_FSDKFace();
            }

            this.mFaceCount = size;
        }

        return this.mFaces;
    }

    public AFD_FSDKError AFD_FSDK_InitialFaceEngine(String appid, String sdkkey, int orientsPriority, int scale, int maxFaceNum) {
        this.handle = Integer.valueOf(this.FD_Init(appid, sdkkey, orientsPriority, scale, maxFaceNum, this.error));
        return this.error;
    }

    public AFD_FSDKError AFD_FSDK_UninitialFaceEngine() {
        if(this.handle.intValue() != 0) {
            this.error.mCode = this.FD_UnInit(this.handle.intValue());
            this.handle = Integer.valueOf(0);
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    public AFD_FSDKError AFD_FSDK_StillImageFaceDetection(byte[] data, int width, int height, int format, List<AFD_FSDKFace> list) {
        if(list != null && data != null) {
            if(this.handle.intValue() != 0) {
                int count = this.FD_Process(this.handle.intValue(), data, width, height, format);
                this.error.mCode = this.FD_GetErrorCode(this.handle.intValue());
                if(count > 0 && this.error.mCode == 0) {
                    AFD_FSDKFace[] result = this.obtainFaceArray(count);
                    this.error.mCode = this.FD_GetResult(this.handle.intValue(), result);

                    for(int i = 0; i < count; ++i) {
                        list.add(result[i]);
                    }
                }
            } else {
                this.error.mCode = 5;
            }
        } else {
            this.error.mCode = 2;
        }

        return this.error;
    }

    public AFD_FSDKError AFD_FSDK_GetVersion(AFD_FSDKVersion version) {
        if(version == null) {
            this.error.mCode = 2;
        } else if(this.handle.intValue() != 0) {
            this.error.mCode = 0;
            version.mVersion = this.FD_Version(this.handle.intValue());
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    static {
        System.loadLibrary("mpbase");
        System.loadLibrary("ArcSoft_FDEngine");
    }
}
