package org.yanzi.activity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.yanzi.callback.FaceCallBack;
import org.yanzi.camera.CameraCallback;

import org.yanzi.camera.CameraSurface;
import org.yanzi.model.FaceCMD;
import org.yanzi.model.FaceContrl;
import org.yanzi.playcamera.R;
import org.yanzi.util.DisplayUtil;
import org.yanzi.util.FaceManager;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.widget.TextView;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKFace;

public class CameraActivity extends Activity implements CameraCallback, FaceCallBack {
    private static final String TAG = "yanzi";
    public static final int STATE_FACE_START = 0X0001;//开始识别
    public static final int STATE_FACE_STOP = 0X0002;//结束识别


    CameraSurface surfaceView = null;
    private SurfaceView surfaceViewRect = null;
    private SurfaceHolder surfaceHolder = null;
    private TextView textViewStatus = null;
    private Paint m_Paint = null;
    private long time = 0l;

    private int m_nScreenWidth, m_nScreenHeight;
    private HandlerThread handlerThread = new HandlerThread("ARF");
    private Handler handler;
    private ExecutorService fixed = Executors.newCachedThreadPool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        initUI();
        init();
//		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//		wakeLock.acquire();

        //shutterBtn.setOnClickListener(new BtnListeners());
    }

    public void init() {
        FaceManager.getInstant(this).init();

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
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        surfaceViewRect = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceViewRect.setZOrderOnTop(true);

        surfaceHolder = surfaceViewRect.getHolder();

        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);//去锯齿
        m_Paint.setStyle(Paint.Style.STROKE);//空心
        m_Paint.setARGB(255, 253, 122, 18);
        m_Paint.setStrokeWidth(4.0f);
    }

    @Override
    protected void onDestroy() {
        if (surfaceView != null) {
            surfaceView.releaseCamera();
        }
        super.onDestroy();
    }


    public void FaceDetectionOut(List<AFD_FSDKFace> result, long nEclipseTime, int width, int height) {
        if (result != null) {

            float scaleX = (((float) m_nScreenWidth) / height);
            float scaleY = (((float) m_nScreenHeight) / width);

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); //清楚掉上一次的画框。
                    for (AFD_FSDKFace face : result) {
                        Rect r = face.getRect();
//                        r.left = (int) (r.left * scaleX);
//                        r.right = (int) (r.right * scaleX);
//                        r.top = (int) (r.top * scaleY);
//                        r.bottom = (int) (r.bottom * scaleY);
                        canvas.drawRect(r, m_Paint);
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

        if (FaceContrl.getInstant().isNull()) {
            Log.e("Tag", "width = " + m_nScreenWidth + "   " + m_nScreenHeight +
                    "  w =" + camera.getParameters().getPreviewSize().width
                    + "  h = " + camera.getParameters().getPreviewSize().height);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(1, info);
            Log.e("Tag","Camera degree = " + info.orientation);
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;
            byte[] data = new byte[bytes.length];
            switch (info.orientation) {
                case 90:
                    start(rotateYUV420Degree90(bytes, width, height),height,width);
                    break;
                case 180:
                    start(rotateYUV420Degree180(bytes, width, height),width,height);
                    break;
                case 270:
                    start(rotateYUV420Degree270(bytes, width, height),height,width);
                    break;
                default:
                    start(bytes,width,height);
                    break;
            }
        }
    }

    private void start(byte[] data,int width,int height){
        FaceContrl.getInstant().startCmd(new FaceCMD(
                data
                , width
                , height
                , CameraActivity.this));
    }

    @Override
    public void initSuccess() {
        showText("初始化成功");
        Log.d("Tag", "初始化成功");
    }

    @Override
    public void detection(byte[] nv21, int width, int height, long time, List<AFD_FSDKFace> faceList) {
        Log.d("Tag", "检测到人脸 ：" + faceList.size());
        FaceDetectionOut(faceList, time, width, height);
    }


    @Override
    public void recognize(AFR_FSDKFace face) {
        Log.d("Tag", " 特征提取");
//        showText("特征提取成功");
    }

    @Override
    public void match(float score) {
//        Log.d("Tag", " 相似度：" + score);
        showText("相似度：" + score);
    }

    @Override
    public void onError(int errCode) {
        Log.e("Tag", "错误码：" + errCode);
//        showText("错误码：" + errCode);

    }


    private void showText(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewStatus.setText(msg);
            }
        });
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

}
