package com.mym.landlords.ui;

import java.lang.ref.WeakReference;

import com.mym.landlords.R;
import com.mym.landlords.res.Assets;
import com.mym.landlords.res.Assets.LoadingProgressListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

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
			//隐藏进度条，并将按钮组展示出来。
			progressBar.setVisibility(View.INVISIBLE);
			findViewById(R.id.loading_ll_btns).setVisibility(View.VISIBLE);
		};
	};

	private Handler handler = new LoadHandler(new WeakReference<LoadingActivity>(this));
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		progressBar = (ProgressBar) findViewById(R.id.loading_prg);
		ToggleButton tgb = (ToggleButton) findViewById(R.id.loading_tgb_voice);
		tgb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Settings.setVoiceEnabled(isChecked);
			}
		});
		Settings.setVoiceEnabled(tgb.isChecked());
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
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.loading_btn_startgame:
			startActivity(MainActivity.getIntent(LoadingActivity.this));
			finish();
			break;
		case R.id.loading_btn_share:
			Intent intent=new Intent(Intent.ACTION_SEND); 
			intent.setType("text/plain"); 
			intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.default_share_title)); 
			intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.default_share_content));  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(Intent.createChooser(intent, getString(R.string.default_share_title)));
			break;
		default:
			break;
		}
	}
}
