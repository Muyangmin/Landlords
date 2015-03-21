package com.mym.landlords.ui;

import java.lang.ref.WeakReference;

import com.mym.landlords.R;
import com.mym.landlords.res.Assets;
import com.mym.landlords.res.Assets.LoadingProgressListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

public class LoadingActivity extends Activity {

	protected static final String LOG_TAG = "LoadingActivity";

	private ProgressBar progressBar;
	
	private AsyncTask<Void, Integer, Void> loadTask = new AsyncTask<Void, Integer, Void>() {
		protected Void doInBackground(Void... params) {
			Assets.loadAssets(LoadingActivity.this, new LoadingProgressListener() {

				@Override
				public void onProgressChanged(int progress) {
					publishProgress(progress);
				}

				@Override
				public void onLoadCompleted() {
					//do nothing.在onPostExecute中执行操作。
				}
			});
			return null;
		};
		
		protected void onProgressUpdate(Integer... values) {
			progressBar.setProgress((int)values[0]);
		};
		
		protected void onPostExecute(Void result) {
			Log.d(LOG_TAG, "loading completed.");
			startActivity(MainActivity.getIntent(LoadingActivity.this));
			finish();
			
		};
	};

	private Handler handler = new LoadHandler(new WeakReference<LoadingActivity>(this));
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		progressBar = (ProgressBar) findViewById(R.id.loading_prg);
		handler.sendEmptyMessageDelayed(LoadHandler.MSG_START_LOADING, 1000);//Splash显示时间
	}
	
	private static final class LoadHandler extends Handler{
		private static final int MSG_START_LOADING = 1;
		private WeakReference<LoadingActivity> wk;

		public LoadHandler(WeakReference<LoadingActivity> wk) {
			super();
			this.wk = wk;
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LoadingActivity activity = wk.get();
			if (msg.what==MSG_START_LOADING && activity!=null){
				activity.loadTask.execute();
			}
		}
	}
}
