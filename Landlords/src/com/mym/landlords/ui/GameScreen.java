package com.mym.landlords.ui;

import android.graphics.Canvas;

import com.mym.landlords.res.GameGraphics;

/**
 * @author Muyangmin
 * @create 2015-3-17
 */
public interface GameScreen {
	/** 用于在界面重绘时展示本界面的必要元素。 */
	void updateUI(GameGraphics graphics, Canvas canvas);
}
