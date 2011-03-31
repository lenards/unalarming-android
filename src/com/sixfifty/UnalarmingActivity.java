package com.sixfifty;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class UnalarmingActivity extends Activity {
	private static final String TAG = UnalarmingActivity.class.getSimpleName();
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("h:mm a z");
	private Calendar calendar = Calendar.getInstance();
	
	protected static final int REQUEST_CODE = 0;
	
	private static final int TIME_DIALOG_ID = 0;
	private static final int MINUTES_DIALOG_ID = 1;
	
	private AlarmManager alarmMgr;
	private AudioManager audioMgr;
	private int hourOfDay;
	private int minOfDay; 
	private int previousRingerMode = -1;
	
	private TimePickerDialog.OnTimeSetListener timeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        @Override
			public void onTimeSet(TimePicker view, int hour, int minute) {
	        	hourOfDay = hour;
	        	minOfDay = minute;
	        	scheduleAlarm();
			}
	};

	private final CharSequence[] minuteValues = { "5", "10", "15", "20", "25",
			"30", "35", "40", "45", "50", "55", "60", "65", "70", "75" };	
	
	private DialogInterface.OnClickListener minutesSelectedListener = 
		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0) {
					CharSequence selected = minuteValues[which];
					Calendar c = Calendar.getInstance();
					c.add(Calendar.MINUTE, Integer.valueOf(selected.toString()));
					hourOfDay = c.get(Calendar.HOUR_OF_DAY);
					minOfDay = c.get(Calendar.MINUTE);
					scheduleAlarm();
					dialog.dismiss();
				}
			}
		};
	    
	/** 
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("Unalarming - The Gentle Meditation Alarm");
        calendar = Calendar.getInstance();
        
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        audioMgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        // determine current ringer mode setting
        previousRingerMode = audioMgr.getRingerMode();
        
        // grab the set-alarm button and attach listener 
        Button btnSetAlarm = (Button) findViewById(R.id.btn_set_alarm);
        btnSetAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
        
        Button btnSetAlarmDuration = (Button) findViewById(R.id.btn_set_alarm_duration);
        btnSetAlarmDuration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(MINUTES_DIALOG_ID);
			}
		});
    }

    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case TIME_DIALOG_ID:
            hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            minOfDay = calendar.get(Calendar.MINUTE);
    		return new TimePickerDialog(this, timeSetListener, hourOfDay, minOfDay, false);
    	case MINUTES_DIALOG_ID:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Select duration (in minutes)");
    		builder.setSingleChoiceItems(minuteValues, -1, minutesSelectedListener);
    		return builder.create();
    	}
    	return null;
    }

    private void scheduleAlarm() {	
        // this should be how we turn off the ringer during meditation
        audioMgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    	
		Intent intent = new Intent(UnalarmingActivity.this, OnetimeAlarmReceiver.class);
		Log.i(TAG, "intent: " + intent.toString());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(UnalarmingActivity.this, REQUEST_CODE, intent, 0);
		Log.i(TAG, "pending-intent: " + pendingIntent.toString());

		Calendar alarm = Calendar.getInstance();
		Calendar now = (Calendar)alarm.clone();
		
		alarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
		alarm.set(Calendar.MINUTE, minOfDay);
		
		Log.i(TAG, "alarm-set: " + alarm.getTimeInMillis());
		Log.i(TAG, "system-time: " + System.currentTimeMillis());
		
		int hourDelta = elapsed(now, alarm, Calendar.HOUR_OF_DAY);
		int minDelta = elapsed(now, alarm, Calendar.MINUTE);
		String msg = String.format("Alarm set for %d hours and %d minutes from now: %s.  Enjoy!", 
				hourDelta, minDelta, FORMAT.format(alarm.getTime()));
		alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), pendingIntent);
		Toast.makeText(UnalarmingActivity.this, msg, Toast.LENGTH_SHORT).show();    	
    }

    /**
     * Computes the elapsed time between two time-periods. 
     * 
     * @author BalusC - http://stackoverflow.com/users/157882/balusc
     * @see http://stackoverflow.com/questions/567659/calculate-elapsed-time-in-java-groovy
     * 
     * @param before the time prior to after
     * @param after the time following before
     * @param field the field of the Calendar (YEAR, DATE, MINUTE, HOUR_OF_DAY)
     * @return integer representing the time difference for the field
     */
    private static int elapsed(Calendar before, Calendar after, int field) {
        Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
        int elapsed = -1;
        while (!clone.after(after)) {
            clone.add(field, 1);
            elapsed++;
        }
        return elapsed;
    }

    
    /*
     * Project Management in Comments!!!!!
		> 1st Pass
			* provide UI to set alarm for set-period (25 minutes)
			* write handler to set alarm
			* notify alarm set UI 
		> 2nd Pass
			* enable user to enter time-period (minutes from now)
			* make handler use time-period set by user - not default
		> 3rd Pass 
			* Add background picture
			** http://www.flickr.com/photos/rossap/4540965708/
			* Add simple launcher icon 
		> 4th Pass
			* provide user-prefs activity?
			* find default menu/prefs icons
			* make photo silent when alarm set
			** have phone return to off silent mode if it wasn't set before
			* provide info menu on the background photo 
		> 5th Pass
			* redesign the launcher icon
			* rotation several pictures: 
			* http://www.flickr.com/photos/hyougushi/61769775/
			* http://www.flickr.com/photos/kuckibaboo/121543162/
			* http://www.flickr.com/photos/caseyyee/4278883548/
			* http://www.flickr.com/photos/sebastiantiger/3038525954/
			* http://www.flickr.com/photos/sofafort/246731874/
			* http://www.flickr.com/photos/jpellgen/3611094608/
		> Later
			* have zen-bell ring as alarm? 
			* have zen-bell ring on start? 
			* Allow user to set duration/pattern of vibration
			** How do persist this?  (as a preference with a tokenize string for pattern spec)

     */
    
    /*
     * When we decide to vibrate, this is what the code will look like: 
     * 
     * // Start immediately
     * // Vibrate for 200 milliseconds
     * // Sleep for 500 milliseconds
     * long[] pattern = { 0, 200, 500 };
     * 
     * v.vibrate(pattern, -1);
     */
}