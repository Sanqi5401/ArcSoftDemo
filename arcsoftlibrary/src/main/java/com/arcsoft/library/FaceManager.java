package com.arcsoft.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.library.database.module.Face;
import com.arcsoft.library.module.FaceData;
import com.arcsoft.library.module.FaceResponse;
import com.arcsoft.library.utils.BitmapUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zsq on 2017/7/25.
 */

public class FaceManager {
    private final static String APPID = "AeGta6Y1EuWcV2QX6Jb1xqmAMMHzHps4aiUg6ymuBZaz";
    private final static String APD_SDK = "7ia9gtggpr7keK7wvK3xgD2h2uSuDQXE5fvjMJuWGrMC";
    private final static String APR_SDK = "7ia9gtggpr7keK7wvK3xgD2wMhyEXJ7rJwwuEmC1zSR4";
    private final static String APT_SDK = "7ia9gtggpr7keK7wvK3xgD2pCJi2skZAqDYy2iri7nvE";

    private AFD_FSDKEngine afdFsdkEngine;
    private AFR_FSDKEngine afrFsdkEngine;
    private AFT_FSDKEngine aftFsdkEngine;

    private AFD_FSDKError afdFsdkError;
    private AFR_FSDKError afrFsdkError;
    private AFT_FSDKError aftFsdkError;

    private boolean isInit = false;

    public FaceManager() {
        afdFsdkEngine = new AFD_FSDKEngine();
        afrFsdkEngine = new AFR_FSDKEngine();
        aftFsdkEngine = new AFT_FSDKEngine();
    }


    public void onCreate() {
        init();
    }


    public void onDestory() {
        afdFsdkEngine.AFD_FSDK_UninitialFaceEngine();
        afrFsdkEngine.AFR_FSDK_UninitialEngine();
        aftFsdkEngine.AFT_FSDK_UninitialFaceEngine();
        afdFsdkEngine = null;
        afrFsdkEngine = null;
        aftFsdkEngine = null;
        afrFsdkError = null;
        afdFsdkError = null;
        aftFsdkError = null;
    }


    public boolean init() {
        if (isInit) {
            EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.INIT));
            return true;
        }
        afdFsdkError = afdFsdkEngine.AFD_FSDK_InitialFaceEngine(APPID, APD_SDK, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        int code = afdFsdkError.getCode();
        if (afdFsdkError.getCode() == 0) {
            afrFsdkError = afrFsdkEngine.AFR_FSDK_InitialEngine(APPID, APR_SDK);
            code = afrFsdkError.getCode();
            if (afrFsdkError.getCode() == 0) {
                aftFsdkError = aftFsdkEngine.AFT_FSDK_InitialFaceEngine(APPID, APT_SDK, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
                if (aftFsdkError.getCode() == 0) {
                    isInit = true;
                    EventBus.getDefault().post(new FaceResponse(code, FaceResponse.FaceType.INIT));
                    return true;
                }
            }
        }
        EventBus.getDefault().post(new FaceResponse(code, FaceResponse.FaceType.INIT));
        return false;
    }

    public FaceData decodePath(String path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] nv21 = BitmapUtils.getNV21(width, height, bitmap);
            return new FaceData(nv21, width, height, 0);
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new FaceResponse(0XA001, FaceResponse.FaceType.DETECTION));
        }
        return null;
    }


    public List<AFD_FSDKFace> fdDetection(FaceData data) {
        int width = data.getWidth();
        int height = data.getHeight();
        byte[] nv21 = data.getNv21();
        List<AFD_FSDKFace> list = new ArrayList<>();
        afdFsdkError = afdFsdkEngine.AFD_FSDK_StillImageFaceDetection(nv21, width, height, AFD_FSDKEngine.CP_PAF_NV21, list);
        if (afdFsdkError.getCode() == 0) {
            return list;
        }
        EventBus.getDefault().post(new FaceResponse(afdFsdkError.getCode(), FaceResponse.FaceType.DETECTION));
        return null;
    }

    public List<AFT_FSDKFace> ftDetection(FaceData data){
        int width = data.getWidth();
        int height = data.getHeight();
        byte[] nv21 = data.getNv21();
        List<AFT_FSDKFace> list = new ArrayList<>();
        aftFsdkError = aftFsdkEngine.AFT_FSDK_FaceFeatureDetect(nv21, width, height, AFT_FSDKEngine.CP_PAF_NV21, list);
        if (aftFsdkError.getCode() == 0) {
            return list;
        }
        EventBus.getDefault().post(new FaceResponse(aftFsdkError.getCode(), FaceResponse.FaceType.DETECTION));
        return null;
    }


    public byte[] recognize(FaceData data, Rect rect, int degree) {
        byte[] face = new byte[22020];
        int width = data.getWidth();
        int height = data.getHeight();
        byte[] nv21 = data.getNv21();
        afrFsdkError = afrFsdkEngine.AFR_FSDK_ExtractFRFeature(nv21, width, height,
                AFR_FSDKEngine.CP_PAF_NV21, rect, degree, face);
        if (afrFsdkError.getCode() == 0) {
            return face;
        }
        EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.RECOGNITION));
        return null;
    }


    public float match(byte[] mface1, Face mface2) {
        AFR_FSDKMatching score = new AFR_FSDKMatching();
        afrFsdkError = afrFsdkEngine.AFR_FSDK_FacePairMatching(mface1, mface2.getFeature(), score);
        if (afrFsdkError.getCode() == 0) {
            return score.getScore();
        }
        EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.MATCH));
        return 0;
    }


}
