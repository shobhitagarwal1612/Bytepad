package com.example.bytepad;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class splash extends Activity {
	MediaPlayer ourSong;

	@Override
	protected void onCreate(Bundle splashbackground) {
		// TODO Auto-generated method stub
		super.onCreate(splashbackground);
		setContentView(R.layout.splash);

		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					Intent openCounter = new Intent(
							"com.example.bytepad.MAINACTIVITY");
					startActivity(openCounter);
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		finish();
	}

}
