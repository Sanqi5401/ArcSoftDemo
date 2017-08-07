package org.zsq.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

public class DisplayUtil {
	private static final String TAG = "DisplayUtil";
	/**
	 * dipתpx
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){            
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(dipValue * scale + 0.5f);         
	}     
	
	/**
	 * pxתdip
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){                
		final float scale = context.getResources().getDisplayMetrics().density;                 
		return (int)(pxValue / scale + 0.5f);         
	} 
	
	/**
	 * ��ȡ��Ļ��Ⱥ͸߶ȣ���λΪpx
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context){
		DisplayMetrics dm =context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);
		
	}
	
	/**
	 * ��ȡ��Ļ�����
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context){
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H/W);
	}



	/**
	 * 获取屏幕宽高
	 *
	 * @return int[]  , width=int[0]  , height=int[1]
	 */
	public static int[] getWidthAndHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		return new int[]{dm.widthPixels, dm.heightPixels};
	}

	/**
	 * 获取屏幕真实宽高
	 *
	 * @return height
	 */
	public static int[] getScreen(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		int heightPixels;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		// since SDK_INT = 1;
		heightPixels = metrics.heightPixels;

		// includes window decorations (statusbar bar/navigation bar)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Point realSize = new Point();
			Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
			heightPixels = realSize.y;
		}

		return new int[]{metrics.widthPixels, heightPixels};
	}

//    /**
//     * 获取底部虚拟按钮高度
//     *
//     * @return int
//     */
//    public static int getNavigationBar() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        int h1 = getWidthAndHeight()[1];
//        int h2 = getScreen()[1];
//        return h2 - h1;
//    }

	/**
	 * 获取镜头的方向
	 *
	 * @return 方向
	 */
	public static int getRotation(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay().getRotation();
	}

	/**
	 * 是否横屏
	 *
	 * @return true 是， false 不是
	 */
	public static boolean isLandscape(Context context) {
		Configuration mOrientation = context.getResources().getConfiguration();
		return mOrientation.orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
}
