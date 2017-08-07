package com.arcsoft.library;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.library.database.module.Face;
import com.arcsoft.library.module.FaceData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class FaceService extends Service {

    private final static String APPID = "AeGta6Y1EuWcV2QX6Jb1xqmAMMHzHps4aiUg6ymuBZaz";
    private final static String APD_SDK = "7ia9gtggpr7keK7wvK3xgD2h2uSuDQXE5fvjMJuWGrMC";
    private final static String APR_SDK = "7ia9gtggpr7keK7wvK3xgD2wMhyEXJ7rJwwuEmC1zSR4";


    private FaceManager manager;
    private LocalBind local = new LocalBind();



    public class LocalBind extends Binder{
        public FaceService getService(){
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
        manager = new FaceManager(this);
        manager.onCreate();
        EventBus.getDefault().register(this);
    }

    public boolean enroll(String path,String name) {
        FaceData data = manager.decodePath(path);
        if (data != null) {
           List<AFD_FSDKFace> list =  manager.detection(data);
            if(list != null && list.size() > 0){
                byte[] feature = manager.recognize(data,list.get(0).getRect(), list.get(0).getDegree());
                ContentValues values = new ContentValues();
                values.put(Face.NAME,name);
                values.put(Face.FEATURE, Base64.encodeToString(feature,Base64.DEFAULT));
                values.put(Face.PATH,path);
                new Face(FaceService.this,values);
                return true;
            }
        }
        return false;
    }

    public void cameraRecognize(FaceData data){
        if(data != null){
            List<AFD_FSDKFace> list = manager.detection(data);
            for (AFD_FSDKFace afd_fsdkFace : list) {
                byte[] feature = manager.recognize(data,afd_fsdkFace.getRect(), afd_fsdkFace.getDegree());
                int i = 0;
                while (true){
                    Face face = new Face(FaceService.this,i);
                    if(face == null || TextUtils.isEmpty(face.getName())
                            || TextUtils.isEmpty(face.getPath())
                            || face.getFeature() == null
                            || face.getFeature().length <= 0){
                        break;
                    }else {
                        manager.match(feature, face, afd_fsdkFace, data.getOrientation());
                    }
                    i++;
                }
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(FaceData data) {
        cameraRecognize(data);
    };

}
