package org.zsq.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.zsq.util.DisplayUtil;

import java.io.IOException;
import java.util.List;

/*************************************************************************
 *
 * deepCam CONFIDENTIAL
 * FILE: com.deepcam.plugin.views
 *
 *  [2016] - [2017] DeepCam, LLC and DeepCam
 *  All Rights Reserved.

 NOTICE:
 * All information contained herein is, and remains the property of DeepCam LLC.
 * The intellectual and technical concepts contained herein are proprietary to DeepCam
 * and may be covered by U.S. and Foreign Patents,patents in process, and are protected by
 * trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * DeepCam, LLC.
 *
 *
 * Written: ji.zheng(ji.zheng@deepcam.com)
 * Updated: 2017/5/9
 */


public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private Camera mCamera = null;
    private Context mContext;
    private boolean isUseFront = true; //是否调用前置摄像头 默认用前置摄像头
    private Camera.Size mSize;
    private boolean isPreview;
    private CameraCallback callback;
    private int mWidth, mHeight;
    private boolean isStop = false;

    public CameraSurface(Context context) {
        this(context, null);
//        this.mContext = context;
//        this.getHolder().addCallback(this);
    }

    public CameraSurface(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
//        this.mContext = context;
//        this.getHolder().addCallback(this);
    }

    public CameraSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.getHolder().addCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (callback != null)
            callback.onPreviewFrame(bytes, camera);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rotateCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        getHolder().removeCallback(null);
        releaseCamera();
    }

    public void onResume() {
        if (isStop) {
            mCamera.startPreview();
            isStop = false;
        }
    }
    public void onPause() {
        if (!isStop) {
            isStop = true;
            mCamera.stopPreview();
        }
    }
    /**
     * 旋转镜头成像
     */
    public void rotateCamera() {
        mCamera.stopPreview();
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(isUseFront ? 1 : 0, info);
        int rotation = DisplayUtil.getRotation(mContext);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 90;
                break;
            case Surface.ROTATION_90:
                degrees = 0;
                break;
            case Surface.ROTATION_180:
                degrees = -90;
                break;
            case Surface.ROTATION_270:
                degrees = 180;
                break;
        }
        Log.d("Tag", "degress = " + degrees);
        mCamera.setDisplayOrientation(degrees);
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
    }


    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            if (isPreview) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
                mCamera = null;
                isPreview = false;
            }
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        if (mCamera == null) {
            if (isUseFront) {
                mCamera = Camera.open(1);
            } else {
                mCamera = Camera.open(0);
            }
            try {
                mCamera.setPreviewDisplay(getHolder());
                Point wh = DisplayUtil.getScreenMetrics(mContext);
                Log.e("Tag", "width = " + wh.x + "  height = " + wh.y);
                mWidth = wh.y;
                mHeight = wh.x;
                initCameraParams(mWidth, mHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
            finally {
                startPreview();
            }
        }
    }

    /**
     * 开始预览
     */
    private boolean startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();//开始预览
            mCamera.cancelAutoFocus();
            isPreview = true;
        } else {
            isPreview = false;
        }
        return isPreview;
    }

    /**
     * 初始化相机参数
     *
     * @param width
     * @param height
     */
    private void initCameraParams(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();//得到摄像头的参数
        int PreviewWidth = width, PreviewHeight = height;
        boolean isSize = false;

        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if (sizeList.size() > 1) {
            for (Camera.Size cur : sizeList) {
                if (cur.width == PreviewWidth
                        && cur.height == PreviewHeight) {
                    PreviewWidth = cur.width;
                    PreviewHeight = cur.height;
                    isSize = true;
                    break;
                }
            }
            if (!isSize) {
                Camera.Size cur = sizeList.get(0);
                PreviewWidth = cur.width;
                PreviewHeight = cur.height;
                isSize = true;
            }
        } else if (sizeList.size() > 0) {
            Camera.Size cur = sizeList.get(0);
            PreviewWidth = cur.width;
            PreviewHeight = cur.height;
            isSize = true;
        } else {
            isSize = false;
        }

        if (isSize) {
            parameters.setPreviewSize(PreviewWidth, PreviewHeight);//设置预览照片的大小
            parameters.setPictureFormat(ImageFormat.JPEG);//设置照片的格式
            parameters.setJpegQuality(100);//设置照片的质量
            parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置照片的大小，默认是和     屏幕一样大
            mCamera.setParameters(parameters);

            mSize = mCamera.getParameters().getPictureSize();
        }
    }

    /**
     * 是否用前置摄像头
     *
     * @param useFront true：用前置摄像头
     *                 false:调用后置摄像头
     */
    public void setUseFront(boolean useFront) {
        isUseFront = useFront;
    }

    /**
     * 判断是否预览模式
     */
    public boolean getPreview() {
        return isPreview;
    }

    public void setCallback(CameraCallback callback) {
        this.callback = callback;
    }
}
