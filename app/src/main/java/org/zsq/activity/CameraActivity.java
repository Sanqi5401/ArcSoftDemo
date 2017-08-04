package org.zsq.activity;

import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zsq.app.DemoApplication;
import org.zsq.camera.CameraCallback;

import org.zsq.camera.CameraSurface;
import org.zsq.playcamera.R;
import org.zsq.util.DisplayUtil;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.widget.TextView;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.library.FaceService;
import com.arcsoft.library.module.FaceData;
import com.arcsoft.library.module.FaceResponse;

/**
 * create by zsq
 */
public class CameraActivity extends Activity implements CameraCallback {
    private static final String TAG = "zsq";
    CameraSurface surfaceView = null;
    private SurfaceView surfaceViewRect = null;
    private SurfaceHolder surfaceHolder = null;
    private Paint m_Paint = null;
    private int m_nScreenWidth, m_nScreenHeight;
    private FaceService faceService;

    private long time = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        faceService = ((DemoApplication) getApplication()).getFaceService();
        initUI();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        if (surfaceView != null) {
            surfaceView.releaseCamera();
        }
        super.onDestroy();
    }

    public void init() {
        Point p = DisplayUtil.getScreenMetrics(this);
        m_nScreenWidth = p.x;
        m_nScreenHeight = p.y;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }


    private void initUI() {
        surfaceView = (CameraSurface) findViewById(R.id.camera_surfaceview);
        //shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
        surfaceView.setCallback(this);

        //设置为后置摄像头
        surfaceView.setUseFront(true);
        surfaceViewRect = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceViewRect.setZOrderOnTop(true);

        surfaceHolder = surfaceViewRect.getHolder();

        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);//去锯齿
        m_Paint.setStyle(Paint.Style.STROKE);//空心
        m_Paint.setARGB(255, 253, 122, 18);
        m_Paint.setStrokeWidth(4.0f);
        m_Paint.setTextSize(40);
    }


    public void faceOut(AFD_FSDKFace result, float score, String name) {
        if (result != null) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); //清楚掉上一次的画框。
                    Rect r = result.getRect();
                    canvas.drawRect(r, m_Paint);
                    if (score > 0.5) {
                        canvas.drawText("" + score, r.left, r.top - 20, m_Paint);
                        canvas.drawText(name, r.left, r.top - 60, m_Paint);
                    }
                }
            } catch (Exception Ex) {

            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
//            if (result.size() > 0) {
//                showText("人脸检测：有 " + result.size() + " 个人。检测耗时：" + nEclipseTime + " ms");
//            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Canvas canvas = null;
        try {

            surfaceViewRect.setZOrderOnTop(true);
            canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); //清楚掉上一次的画框。
                Rect r = new Rect((int) event.getX() - 50, (int) event.getY() - 50, (int) event.getX() + 50, (int) event.getY() + 50);
                canvas.drawRect(r, m_Paint);
            }
        } catch (Exception Ex) {

        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {

        if (time != 0 || (System.currentTimeMillis() - time) < 1000) {
            return;
        } else {
            Log.e("Tag", "width = " + m_nScreenWidth + "   " + m_nScreenHeight +
                    "  w =" + camera.getParameters().getPreviewSize().width
                    + "  h = " + camera.getParameters().getPreviewSize().height);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(1, info);
            Log.e("Tag", "Camera degree = " + info.orientation);
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;
            switch (info.orientation) {
                case 90:
                    start(rotateYUV420Degree90(bytes, width, height), height, width);
                    break;
                case 180:
                    start(rotateYUV420Degree180(bytes, width, height), width, height);
                    break;
                case 270:
                    start(rotateYUV420Degree270(bytes, width, height), height, width);
                    break;
                default:
                    start(bytes, width, height);
                    break;
            }
        }
    }

    private void start(byte[] data, int width, int height) {
        faceService.cameraRecognize(new FaceData(data, width, height));
    }


    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
// Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }

        }
// Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    private byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    private byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = imageWidth - 1; x >= 0; x--) {
            for (int y = 0; y < imageHeight; y++) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }// Rotate the U and V color components
        i = imageWidth * imageHeight;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i++;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i++;
            }
        }
        return yuv;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FaceResponse event) {
        if (event.getCode() != 0) {
            Log.e("Tag", "错误码：" + event.getCode());
        } else {
            Log.e("Tag", "成功：" + event.getType());
            if (event.getType() == FaceResponse.FaceType.MATCH) {
                Log.e("TAG", "match : " + event.getScore());
                faceOut(event.getFace(), event.getScore(), event.getName());
            }
        }
    }

    ;

}
