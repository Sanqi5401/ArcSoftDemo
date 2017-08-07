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

    private AFD_FSDKEngine afdFsdkEngine;
    private AFR_FSDKEngine afrFsdkEngine;
    private AFD_FSDKError afdFsdkError;
    private AFR_FSDKError afrFsdkError;
    private boolean isInit = false;
    private Context context;

    public FaceManager(Context context) {
        afdFsdkEngine = new AFD_FSDKEngine();
        afrFsdkEngine = new AFR_FSDKEngine();
        this.context = context;
    }


    public void onCreate() {
        init();
    }


    public void onDestory() {
        afdFsdkEngine.AFD_FSDK_UninitialFaceEngine();
        afrFsdkEngine.AFR_FSDK_UninitialEngine();
        afdFsdkEngine = null;
        afrFsdkEngine = null;
        afrFsdkError = null;
        afdFsdkError = null;
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
                isInit = true;
                EventBus.getDefault().post(new FaceResponse(code, FaceResponse.FaceType.INIT));
                return true;
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


    public List<AFD_FSDKFace> detection(FaceData data) {
        int width = data.getWidth();
        int height = data.getHeight();
        byte[] nv21 = data.getNv21();
        List<AFD_FSDKFace> list = new ArrayList<>();
        afdFsdkError = afdFsdkEngine.AFD_FSDK_StillImageFaceDetection(nv21, width, height, AFD_FSDKEngine.CP_PAF_NV21, list);
        if (afdFsdkError.getCode() == 0) {
            EventBus.getDefault().post(new FaceResponse(afdFsdkError.getCode(), FaceResponse.FaceType.DETECTION, list));
            return list;
        }
        EventBus.getDefault().post(new FaceResponse(afdFsdkError.getCode(), FaceResponse.FaceType.DETECTION));
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
            EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.RECOGNITION, face));
            return face;
        }
        EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.RECOGNITION));
        return null;
    }

    public float match(byte[] mface1, Face mface2, AFD_FSDKFace face, int orientation) {
        AFR_FSDKMatching score = new AFR_FSDKMatching();
        afrFsdkError = afrFsdkEngine.AFR_FSDK_FacePairMatching(mface1, mface2.getFeature(), score);
        if (afrFsdkError.getCode() == 0) {
            EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.MATCH, score.getScore(), face, mface2.getName(), orientation));
            return score.getScore();

        }
        EventBus.getDefault().post(new FaceResponse(afrFsdkError.getCode(), FaceResponse.FaceType.MATCH));
        return 0;
    }



}
