package com.arcsoft.library;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.library.database.module.Face;
import com.arcsoft.library.module.ArcsoftFace;
import com.arcsoft.library.module.FaceData;
import com.arcsoft.library.module.FaceResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class FaceService extends Service {

    //sdk 控制器
    private FaceManager manager;

    // 阈值 默认为0.7
    private float thresholdValue = 0.7f;

    private LocalBind local = new LocalBind();


    public class LocalBind extends Binder {
        public FaceService getService() {
            return FaceService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return local;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        manager.onDestory();
        EventBus.getDefault().unregister(this);
        return super.onUnbind(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        manager = new FaceManager();
        manager.onCreate();
        EventBus.getDefault().register(this);
    }

   //设置阈值
    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public boolean enroll(String path, String name) {
        FaceData data = manager.decodePath(path);
        if (data != null) {
            List<AFD_FSDKFace> list = manager.fdDetection(data);
            if (list != null) {
                EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.DETECTION, list));
                if (list.size() > 0) {
                    byte[] feature = manager.recognize(data, list.get(0).getRect(), list.get(0).getDegree());
                    if (feature != null) {
                        EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.RECOGNITION, feature));
                        ContentValues values = new ContentValues();
                        values.put(Face.NAME, name);
                        values.put(Face.FEATURE, Base64.encodeToString(feature, Base64.DEFAULT));
                        values.put(Face.PATH, path);
                        new Face(FaceService.this, values);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void cameraRecognize(FaceData data) {
        if (data != null) {
            List<AFT_FSDKFace> list = manager.ftDetection(data);
            if (list != null) {
                EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.DETECTION));
                for (AFT_FSDKFace afd_fsdkFace : list) {
                    byte[] feature = manager.recognize(data, afd_fsdkFace.getRect(), afd_fsdkFace.getDegree());
                    EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.RECOGNITION, feature));
                    if (feature == null)
                        return;
                    int i = 0;
                    while (true) {
                        Face face = new Face(FaceService.this, i);
                        if (face == null || TextUtils.isEmpty(face.getName())
                                || TextUtils.isEmpty(face.getPath())
                                || face.getFeature() == null
                                || face.getFeature().length <= 0) {
                            break;
                        } else {
                            float  mScore = manager.match(feature, face);
                            if(mScore > thresholdValue){
                                EventBus.getDefault().post(new FaceResponse(0, FaceResponse.FaceType.MATCH, mScore, afd_fsdkFace, face.getName(), data.getOrientation()));
                            }
                        }
                        i++;
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(FaceData data) {
        cameraRecognize(data);
    }

    ;
}
