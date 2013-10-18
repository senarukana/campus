/*package com.campusrecruit.activity;

public class AlarmActivity extends BaseActivity{
	 TimePicker myTimePicker;
	    Button buttonstartSetDialog;
	    TextView textAlarmPrompt;
	    TimePickerDialog timePickerDialog;

	    final static int RQS_1 = 1;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);

	        textAlarmPrompt = (TextView) findViewById(R.id.alarmprompt);

	        buttonstartSetDialog = (Button) findViewById(R.id.startAlaram);
	        buttonstartSetDialog.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                textAlarmPrompt.setText("");
	                openTimePickerDialog(false);

	            }
	        });

	    }

	    private void openTimePickerDialog(boolean is24r) {
	        Calendar calendar = Calendar.getInstance();

	        timePickerDialog = new TimePickerDialog(MainActivity.this,
	                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
	                calendar.get(Calendar.MINUTE), is24r);
	        timePickerDialog.setTitle("Set Alarm Time");

	        timePickerDialog.show();

	    }

	    OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

	        @Override
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

	            Calendar calNow = Calendar.getInstance();
	            Calendar calSet = (Calendar) calNow.clone();

	            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
	            calSet.set(Calendar.MINUTE, minute);
	            calSet.set(Calendar.SECOND, 0);
	            calSet.set(Calendar.MILLISECOND, 0);

	            if (calSet.compareTo(calNow) <= 0) {
	                // Today Set time passed, count to tomorrow
	                calSet.add(Calendar.DATE, 1);
	            }

	            setAlarm(calSet);
	        }
	    };

	    private void setAlarm(Calendar targetCal) {

	        textAlarmPrompt.setText("\n\n***\n" + "Alarm is set "
	                + targetCal.getTime() + "\n" + "***\n");

	        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(
	                getBaseContext(), RQS_1, intent, 0);
	        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
	                pendingIntent);

	    }  
}
*/