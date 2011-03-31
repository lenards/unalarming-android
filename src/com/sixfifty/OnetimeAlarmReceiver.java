package com.sixfifty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class OnetimeAlarmReceiver extends BroadcastReceiver {
	private static final String TAG = OnetimeAlarmReceiver.class.getSimpleName();
	
	public static final int DO_NOT_REPEAT = -1;
	
	private Vibrator vib;
	
	@Override
	public void onReceive(Context ctxt, Intent intent) {
		Log.d(TAG, "in method onReceive");
		
		restoreRingerModeToPreviousState(ctxt, intent);		
        fireVibrationEvent(ctxt);
	}

	private void fireVibrationEvent(Context ctxt) {
		// Get instance of Vibrator from current Context
        vib = (Vibrator) ctxt.getSystemService(Context.VIBRATOR_SERVICE);
    	long[] pattern = { 0, 200, 500, 200, 500 };
    	vib.vibrate(pattern, DO_NOT_REPEAT);
    	Toast.makeText(ctxt, "Meditation period over...", Toast.LENGTH_LONG).show();
	}

	private void restoreRingerModeToPreviousState(Context ctxt, Intent intent) {
		AudioManager audio = (AudioManager) ctxt.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = intent.getIntExtra("ringerMode", AudioManager.RINGER_MODE_VIBRATE);
		Log.i(TAG, "Ringer Mode stored in the intent was: " + ringerMode);
		audio.setRingerMode(ringerMode);
	}
}