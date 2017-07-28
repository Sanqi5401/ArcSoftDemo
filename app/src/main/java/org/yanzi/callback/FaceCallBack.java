package org.yanzi.callback;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */

public interface FaceCallBack {


    public void initSuccess();

    public void detection(byte[] nv21, int width, int height,long time, List<AFD_FSDKFace> faceList);

    public void recognize(AFR_FSDKFace face);

    public void match(float score);

    public void onError(int errCode);

}
