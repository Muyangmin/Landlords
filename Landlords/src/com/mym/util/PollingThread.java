package com.mym.util;

import android.util.Log;

/**
 * 使用轮询方式进行工作的一种线程，启动线程后会不断执行 {@link #action()}方法。 通过
 * {@link #requestStopThread()}方法来结束线程 。
 * <p>
 * 推荐使用 {@link #PollingThread(String)}构造器，以使这种线程易于识别。
 * </p>
 * 
 * @author Muyangmin
 * @create 2015-3-21
 */
public abstract class PollingThread extends Thread {

	private boolean stopRequested;
	private long interval;

	/**
	 * 构造一个轮询线程
	 * @param threadName 线程名称。
	 * @param interval 设置线程每两次执行之间的间隔时间（单位：ms）。
	 *  	如果该值为0，线程不会做任何间隔处理。
	 */
	public PollingThread(String threadName, long interval) {
		super(threadName);
		this.interval = interval;
	}

	@Override
	public final void run() {
		while (!stopRequested) {
			try {
				action();
				if (interval>0){
					sleep(interval);
				}
			} catch (Exception e) {
				//This abstract class just ignore exception.
				onExceptionOccured(e);
			}
			action();
		}
	}

	/**
	 * 线程具体需要循环执行的操作。
	 */
	protected abstract void action();
	
	/**
	 * 当线程执行出现异常后调用该方法。
	 */
	protected void onExceptionOccured(Exception e){
		Log.d(getName(), "polling exception:" + e.getMessage());
	}

	/**
	 * 提交终止线程的操作。线程会在最后一次操作完成后销毁。
	 */
	public synchronized final void requestStopThread() {
		stopRequested = true;
	}
}
