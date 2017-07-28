package org.yanzi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;


import org.yanzi.callback.FaceCallBack;
import org.yanzi.model.FaceCMD;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by zsq on 2017/7/25.
 */

public class FaceManager {
    private final static String APPID = "AeGta6Y1EuWcV2QX6Jb1xqmAMMHzHps4aiUg6ymuBZaz";
    private final static String APD_SDK = "7ia9gtggpr7keK7wvK3xgD2h2uSuDQXE5fvjMJuWGrMC";
    private final static String APR_SDK = "7ia9gtggpr7keK7wvK3xgD2wMhyEXJ7rJwwuEmC1zSR4";

    private static FaceManager INSTANT = null;
    private AFD_FSDKEngine afdFsdkEngine;
    private AFR_FSDKEngine afrFsdkEngine;
    private FaceCallBack callBack;
    private AFD_FSDKError afdFsdkError;
    private AFR_FSDKError afrFsdkError;
    private boolean isInit = false;
    private AFR_FSDKFace face = null;

    private FaceManager(FaceCallBack callBack) {
        afdFsdkEngine = new AFD_FSDKEngine();
        afrFsdkEngine = new AFR_FSDKEngine();
        this.callBack = callBack;

    }


    public static FaceManager getInstant(FaceCallBack callBack) {
        if (INSTANT == null)
            return INSTANT = new FaceManager(callBack);

        return INSTANT.setCallBack(callBack);
    }


    private FaceManager setCallBack(FaceCallBack callBack) {
        this.callBack = callBack;
        return INSTANT;
    }

    public void init() {
        if (isInit) {
            callBack.initSuccess();
            return;
        }
        afdFsdkError = afdFsdkEngine.AFD_FSDK_InitialFaceEngine(APPID, APD_SDK, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        int code = afdFsdkError.getCode();
        if (afdFsdkError.getCode() == 0) {
            afrFsdkError = afrFsdkEngine.AFR_FSDK_InitialEngine(APPID, APR_SDK);
            code = afrFsdkError.getCode();
            if (afrFsdkError.getCode() == 0) {
                callBack.initSuccess();
                isInit = true;
                return;
            }
        }
        showError(code);
    }

    public void detection(String path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] nv21 = BitmapUtils.getNV21(width, height, bitmap);
            detection(nv21, width, height);

        } catch (Exception e) {
            e.printStackTrace();
            showError(0xA001);
        }
    }


    public List<AFD_FSDKFace> detection(byte[] nv21, int width, int height) {
        List<AFD_FSDKFace> list = new ArrayList<>();
        long time = System.currentTimeMillis();
        afdFsdkError = afdFsdkEngine.AFD_FSDK_StillImageFaceDetection(nv21, width, height, AFD_FSDKEngine.CP_PAF_NV21, list);
        if (afdFsdkError.getCode() == 0) {
            if (callBack != null)
                callBack.detection(nv21, width, height, System.currentTimeMillis() - time, list);
            return list;
        } else {
            showError(afdFsdkError.getCode());
        }
        return null;
    }


    public AFR_FSDKFace recognize(byte[] nv21, int width, int height, Rect rect) {
        AFR_FSDKFace face = new AFR_FSDKFace();
        afrFsdkError = afrFsdkEngine.AFR_FSDK_ExtractFRFeature(nv21, width, height, AFR_FSDKEngine.CP_PAF_NV21, rect, AFR_FSDKEngine.AFR_FOC_0, face);
        if (afrFsdkError.getCode() == 0) {
            callBack.recognize(face);
            return face;
        }
        showError(afrFsdkError.getCode());
        return null;
    }

    public void match(AFR_FSDKFace mface1) {
        if (mface1 == null) {
            showError(0xA002);
        } else {
            AFR_FSDKMatching score = new AFR_FSDKMatching();
            afrFsdkError = afrFsdkEngine.AFR_FSDK_FacePairMatching(mface1, face, score);
            if (afrFsdkError.getCode() == 0) {
                callBack.match(score.getScore());
            }
        }
    }

    public void setFace(AFR_FSDKFace face) {
        this.face = face;
    }

    private void showError(int code) {
        if (callBack != null)
            callBack.onError(code);
    }


}
