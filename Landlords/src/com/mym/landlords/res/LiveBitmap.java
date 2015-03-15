package com.mym.landlords.res;

import java.io.IOException;

import com.mym.util.BitmapUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 代表已被加载到内存中的Bitmap对象。
 * @author Muyangmin
 * @create 2015-3-15
 */
public final class LiveBitmap {
	private final Bitmap bitmap;
	private final int rawWidth;
	private final int rawHeight;

	// 强制使用工厂方法
	private LiveBitmap(Bitmap bitmap, int rawWidth, int rawHeight) {
		super();
		this.bitmap = bitmap;
		this.rawWidth = rawWidth;
		this.rawHeight = rawHeight;
	}

	/**
	 * 按原有尺寸加载图片。
	 * @param context 上下文信息。
	 * @param assets 要加载的Bitmap文件名（必须是Assets文件）。
	 * @return 加载得到的位图；如果失败，返回null。
	 */
	public static final LiveBitmap loadBitmap(Context context, String assets) {
		return loadBitmap(context, assets, 0, 0);
	}

	/**
	 * 按原有尺寸加载图片，再缩放到指定尺寸。
	 * @param context 上下文信息。
	 * @param assets 要加载的Bitmap文件名（必须是Assets文件）。
	 * @param width 目标宽度。
	 * @param height 目标高度。
	 * @return 加载得到的位图；如果失败，返回null。
	 */
	public static final LiveBitmap loadBitmap(Context context, String assets,
			int width, int height) {
		LiveBitmap instance = null;
		AssetManager am = context.getAssets();
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(am.open(assets));
			if (width == 0 || height == 0) {
				instance = new LiveBitmap(bitmap, bitmap.getWidth(),
						bitmap.getHeight());
			} else {
				bitmap = BitmapUtil.scaleBitmap(bitmap, width, height);
				instance = new LiveBitmap(bitmap, width, height);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getRawWidth() {
		return rawWidth;
	}

	public int getRawHeight() {
		return rawHeight;
	}
}
