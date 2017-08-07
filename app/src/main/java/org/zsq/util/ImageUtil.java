package org.zsq.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class ImageUtil {
	/**
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
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

	public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
		byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
		int i;
		int count = 0;

		for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
			yuv[count] = data[i];
			count++;
		}

		for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth * imageHeight; i -= 2) {
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

	public static Rect rotateRact(Rect r, int orientation, int cameraWidth, int cameraHeight) {
		Rect r1 = new Rect();
		if(orientation == 90) {
			r1.top = r.left;
			r1.left = cameraHeight - r.bottom;
			r1.bottom = r.right;
			r1.right = cameraHeight - r.top;
		} else if (orientation == 180) {
			r1.top = cameraHeight - r.bottom;
			r1.left = cameraWidth - r.right;
			r1.bottom = cameraHeight - r.top;
			r1.right = cameraWidth - r.left;
		} else if (orientation == 270) {
			r1.top = cameraWidth - r.right;
			r1.left = r.top;
			r1.bottom = cameraWidth - r.left;
			r1.right = r.bottom;
		} else {
			r1.top = r.top;
			r1.left = r.left;
			r1.bottom = r.bottom;
			r1.right = r.right;
		}
		return r1;
	}
}
