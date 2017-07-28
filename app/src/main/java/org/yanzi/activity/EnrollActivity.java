package org.yanzi.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing_impl.ui.BoxingActivity;

import org.yanzi.callback.FaceCallBack;
import org.yanzi.playcamera.R;
import org.yanzi.util.FaceManager;

import java.util.List;

public class EnrollActivity extends AppCompatActivity implements FaceCallBack {
    private TextView textView;
    private StringBuffer buffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        textView = (TextView) findViewById(R.id.textView);
        FaceManager.getInstant(this).init();
        findViewById(R.id.btnEnroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG); // Mode：Mode.SINGLE_IMG, Mode.MULTI_IMG, Mode.VIDEO
                config.needCamera().needGif().withMaxCount(1); // 支持gif,相机，设置最大选图数
                Boxing.of(config).withIntent(EnrollActivity.this, BoxingActivity.class).start(EnrollActivity.this, 1000);
            }
        });

        findViewById(R.id.btnReg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnrollActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void initSuccess() {
        buffer.append("初始化成功\n");
        showText(buffer.toString());
    }

    @Override
    public void detection(byte[] nv21, int width, int height, long time, List<AFD_FSDKFace> faceList) {
        if (faceList.size() > 0) {
            buffer.append("人脸检测成功\n");
            showText(buffer.toString());
            FaceManager.getInstant(this).recognize(nv21, width, height, faceList.get(0).getRect());
        }
    }

    @Override
    public void recognize(AFR_FSDKFace face) {
        buffer.append("特征成功\n");
        showText(buffer.toString());
        //保存人脸
        FaceManager.getInstant(this).setFace(face);
    }

    @Override
    public void match(float score) {

    }

    @Override
    public void onError(int errCode) {
        buffer.append("人脸识别出错，错误码：" + errCode + "\n");
        showText(buffer.toString());
    }

    private void showText(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1000){
                List<BaseMedia> list = Boxing.getResult(data);
                String path = list.get(0).getPath();
                FaceManager.getInstant(this).detection(path);
            }
        }
    }

}

