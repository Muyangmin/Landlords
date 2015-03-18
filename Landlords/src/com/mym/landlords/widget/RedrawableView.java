package com.mym.landlords.widget;

/**
 * 用于实现游戏的虚拟屏幕。
 * 
 * <p>
 * 本游戏为棋牌类游戏，并不需要刷帧，设计思路为被动式刷新。定义接口 {@link RedrawableView} 供View或SurfaceView进行实现，
 * 展示界面的Activity通过使用按钮的事件回调改变数据和逻辑，并调用 {@link #redraw()}方法执行UI的重绘。
 * </p>
 * 
 * @author Muyangmin
 * @create 2015-3-17
 */
public interface RedrawableView {
	/**
	 * 用于客户端请求重绘UI。
	 */
	void redraw();
}
