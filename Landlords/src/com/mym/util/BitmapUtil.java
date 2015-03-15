package com.mym.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;

/**
 * 处理Bitmap相关的操作（旋转、缩放，etc.）。
 * 
 * @author Muyangmin
 * @create 2015-2-12
 */
public final class BitmapUtil {
	/**
	 * 计算Bitmap占用的内存大小，以字节为单位。
	 * @param bitmap 目标位图。
	 * @return 返回Bitmap所占空间的大小。
	 */
	@TargetApi(19)
	public static final int getBitmapSize(Bitmap bitmap){
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){     	//API 19
	        return bitmap.getAllocationByteCount();
	    }
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
	        return bitmap.getByteCount();
	    }
	    return bitmap.getRowBytes() * bitmap.getHeight();               //earlier version
	}
	
	/**
	 * 缩放图片到指定尺寸。
	 * @param bitmap 原图
	 * @param width 目标宽度
	 * @param height 目标高度
	 * @return 缩放后的位图
	 */
	public static final Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
		if (width <=0 || height <=0 ){
			throw new IllegalArgumentException("target size must be positive!");
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	
	/**
	 * 缩放图片到指定比例。
	 * @param bitmap 原图
	 * @param scalew 横向缩放比例
	 * @param scaleh 纵向缩放比例
	 * @return 缩放后的位图
	 */
	public static final Bitmap scaleBitmap(Bitmap bitmap, float scalew,
			float scaleh) {
		if (scalew <=0 || scaleh <=0 ){
			throw new IllegalArgumentException("scale rate must be positive!");
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scalew, scaleh);// 利用矩阵进行缩放不会造成内存溢出
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return newbmp;
	}
	/**
	 * 保存Bitmap到文件,将采用无损格式保存（PNG格式）。
	 * @param path 文件的绝对路径。如果目标文件不存在，则创建之。
	 * @param bitmap 要保存的Bitmap。
	 */
	public static void saveBitmapToFile(String path, Bitmap bitmap) {
		saveBitmapToFile(path, bitmap, CompressFormat.PNG, 100);
	}

	/**
	 * 保存Bitmap到文件，将使用指定的压缩比率和格式。
	 * @param path 文件的绝对路径。如果目标文件不存在，则创建之。
	 * @param bitmap 要保存的Bitmap。
	 * @param format 要使用的压缩格式。
	 * @param quality 图片质量（0=最小压缩，100=最大质量）。
	 */
	public static void saveBitmapToFile(String path, Bitmap bitmap,
			CompressFormat format, int quality) {
		File dir = new File(path).getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File photoFile = new File(path); // 在指定路径下创建文件
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(photoFile);
			if (bitmap != null) {
				if (bitmap.compress(format, quality, fos)) {
					fos.flush();
				}
			}
		} catch (FileNotFoundException e) {
			photoFile.delete();
			e.printStackTrace();
		} catch (IOException e) {
			photoFile.delete();
			e.printStackTrace();
		} finally {
			try {
				if (fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
