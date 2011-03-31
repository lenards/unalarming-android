package com.sixfifty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class OnetimeAlarmReceiver extends BroadcastReceiver {
	
	public static final int DO_NOT_REPEAT = -1;
	private Vibrator vib;
	
	@Override
	public void onReceive(Context ctxt, Intent intent) {
		Log.d("OnetimeAlarmReceiver", "in method onReceive");
        // Get instance of Vibrator from current Context
        vib = (Vibrator) ctxt.getSystemService(Context.VIBRATOR_SERVICE);
    	long[] pattern = { 0, 200, 500, 200, 500 };
    	vib.vibrate(pattern, DO_NOT_REPEAT);
    	Toast.makeText(ctxt, "Meditation period over...", Toast.LENGTH_LONG).show();
	}
	
}