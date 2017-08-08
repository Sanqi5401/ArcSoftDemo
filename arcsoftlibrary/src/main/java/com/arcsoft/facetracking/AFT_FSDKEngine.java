package com.arcsoft.facetracking;

import java.util.List;

public class AFT_FSDKEngine
{
    private final String TAG = getClass().toString();
    public static final int CP_PAF_NV21 = 2050;
    public static final int AFT_OPF_0_ONLY = 1;
    public static final int AFT_OPF_90_ONLY = 2;
    public static final int AFT_OPF_270_ONLY = 3;
    public static final int AFT_OPF_180_ONLY = 4;
    public static final int AFT_OPF_0_HIGHER_EXT = 5;
    public static final int AFT_FOC_0 = 1;
    public static final int AFT_FOC_90 = 2;
    public static final int AFT_FOC_270 = 3;
    public static final int AFT_FOC_180 = 4;
    private Integer handle;
    private AFT_FSDKError error;
    private AFT_FSDKFace[] mFaces;
    private int mFaceCount;

    private native int FT_Init(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, AFT_FSDKError paramAFT_FSDKError);

    private native int FT_Process(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4);

    private native int FT_Config(int paramInt1, int paramInt2);

    private native int FT_GetResult(int paramInt, AFT_FSDKFace[] paramArrayOfAFT_FSDKFace);

    private native int FT_GetErrorCode(int paramInt);

    private native int FT_UnInit(int paramInt);

    private native String FT_Version(int paramInt);

    public AFT_FSDKEngine()
    {
        this.handle = Integer.valueOf(0);
        this.error = new AFT_FSDKError();
        this.mFaces = new AFT_FSDKFace[16];
        this.mFaceCount = 0;
    }

    private AFT_FSDKFace[] obtainFaceArray(int size)
    {
        if (this.mFaceCount < size) {
            if (this.mFaces.length < size) {
                this.mFaces = new AFT_FSDKFace[(size / 16 + 1) * 16];
            }
            for (int i = this.mFaceCount; i < size; i++) {
                this.mFaces[i] = new AFT_FSDKFace();
            }
            this.mFaceCount = size;
        }
        return this.mFaces;
    }

    public AFT_FSDKError AFT_FSDK_InitialFaceEngine(String appid, String sdkkey, int orientsPriority, int scale, int maxFaceNum)
    {
        this.handle = Integer.valueOf(FT_Init(appid, sdkkey, orientsPriority, scale, maxFaceNum, this.error));
        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_FaceFeatureDetect(byte[] data, int width, int height, int format, List<AFT_FSDKFace> list)
    {
        if ((list == null) || (data == null)) {
            this.error.mCode = 2;
        }
        else if (this.handle.intValue() != 0) {
            int count = FT_Process(this.handle.intValue(), data, width, height, format);
            this.error.mCode = FT_GetErrorCode(this.handle.intValue());
            if ((count > 0) && (this.error.mCode == 0)) {
                AFT_FSDKFace[] result = obtainFaceArray(count);
                this.error.mCode = FT_GetResult(this.handle.intValue(), result);
                for (int i = 0; i < count; i++)
                    list.add(result[i]);
            }
        }
        else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_UninitialFaceEngine()
    {
        if (this.handle.intValue() != 0) {
            this.error.mCode = FT_UnInit(this.handle.intValue());
            this.handle = Integer.valueOf(0);
        } else {
            this.error.mCode = 5;
        }
        return this.error;
    }

    public AFT_FSDKError AFT_FSDK_GetVersion(AFT_FSDKVersion version)
    {
        if (version == null) {
            this.error.mCode = 2;
        }
        else if (this.handle.intValue() != 0) {
            this.error.mCode = 0;
            version.mVersion = FT_Version(this.handle.intValue());
        } else {
            this.error.mCode = 5;
        }

        return this.error;
    }

    static
    {
        System.loadLibrary("mpbase");
        System.loadLibrary("ArcSoft_FTEngine");
    }
}