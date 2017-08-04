package org.zsq.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.arcsoft.library.FaceService;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;

import org.zsq.imagloader.BoxingFrescoLoader;
import org.zsq.imagloader.BoxingUcrop;

/**
 * Created by zsq on 16/5/22.
 *
 */
public class DemoApplication extends Application {
    private FaceService faceService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            faceService = ((FaceService.LocalBind)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IBoxingMediaLoader loader = new BoxingFrescoLoader(this);
        BoxingMediaLoader.getInstance().init(loader);
        BoxingCrop.getInstance().init(new BoxingUcrop());
        bindService(new Intent(getApplicationContext(), FaceService.class),connection,BIND_AUTO_CREATE);
    }

    public FaceService getFaceService() {
        return faceService;
    }
}