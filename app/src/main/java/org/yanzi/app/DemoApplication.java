package org.yanzi.app;

import android.app.Application;

import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;

import org.yanzi.imagloader.BoxingFrescoLoader;
import org.yanzi.imagloader.BoxingUcrop;

/**
 * Created by wzfu on 16/5/22.
 */
public class DemoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        IBoxingMediaLoader loader = new BoxingFrescoLoader(this);
        BoxingMediaLoader.getInstance().init(loader);
        BoxingCrop.getInstance().init(new BoxingUcrop());

    }

}