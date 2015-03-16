package com.mym.landlords.res;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 
 * @author Muyangmin
 * @create 2015-3-16
 */
public final class GlobalSoundPool {
	
	private static GlobalSoundPool instance;
	private SoundPool soundPool;
	
	private GlobalSoundPool(Context context){
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	}
	
	public static GlobalSoundPool getInstance(Context context){
		if (instance==null){
			synchronized (GlobalSoundPool.class) {
				instance = new GlobalSoundPool(context);
			}
		}
		return instance;
	}
	
	/**
	 * 加载音效。
	 * @param context 上下文信息。
	 * @param assets 要加载的音效文件名
	 * @return 加载后得到的soundId，可用于播放。
	 */
	protected final int loadSound(Context context, String assets){
		try {
			AssetManager am = context.getAssets();
			AssetFileDescriptor afd = am.openFd(assets);
			return soundPool.load(afd, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void play(int soundId){
	}
}
