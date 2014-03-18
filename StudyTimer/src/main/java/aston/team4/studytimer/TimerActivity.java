package aston.team4.studytimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;

public class TimerActivity extends ActionBarActivity
{
    public static final String STUDY_LENGTH = "aston.team4.studytimer.TimerActivity.SESSION_LENGTH";
    public static final String BREAK_LENGTH = "aston.team4.studytimer.TimerActivity.BREAK_LENGTH";
    private static final String STUDY_END = "aston.team4.studytimer.TimerActivity.STUDY_END";

    private TextView timerText;

//    private long startTime = 0L;

    private Handler tickReceiverThreadHandler = new Handler();

//    private long sessionLength; //TODO: Remove this when timing is given its own service

    private long studyLength, breakLength;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timer );
        long sessionLength = getIntent().getLongExtra( STUDY_LENGTH, 0 );

        timerText = (TextView) findViewById( R.id.TimerText );

        addTimer( "studyTimer", sessionLength );
        setupBroadcastReceiver();

        studyLength = sessionLength;
    }

    @Override
    protected void onPause()
    {
        super.onPause();

//        unregisterReceiver( tickReceiverThread.tickReceiver );
//        tickReceiverThreadHandler.
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setupBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.timer, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.action_settings )
        {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    private void setupBroadcastReceiver()
    {
//        IntentFilter intentFilter = new IntentFilter( TimerService.TICK_SECOND );
////        intentFilter.addCategory( Intent.CATEGORY_DEFAULT );
//        registerReceiver( TickReceiver, intentFilter );
        tickReceiverThread.start();
    }

    private void addTimer( String sessionName, long sessionLength )
    {
        Intent intent = new Intent( this, TimerService.class );
        intent.putExtra( TimerService.ACTION, TimerService.ACTION_ADD_TIMER );
        intent.putExtra( TimerService.SESSION_NAME, sessionName );
        intent.putExtra( TimerService.SESSION_LENGTH, sessionLength ); //TODO: add a session length inside of the service
        startService( intent );
    }

    private void stopTimer( String sessionName )
    {
        Intent intent = new Intent( this, TimerService.class );
        intent.putExtra( TimerService.ACTION, TimerService.ACTION_STOP_TIMER );
        intent.putExtra( TimerService.SESSION_NAME, sessionName );
        startService( intent );
    }

    public void intervalComplete()
    {

    }

    public void studyComplete()
    {
        //TODO: figure out what the hell's up with this
//        Notification.Builder nb = new Notification.Builder( this )
        NotificationCompat.Builder nb = new NotificationCompat.Builder( this )
                .setContentTitle( "Study Done" )
                .setContentText( "You can stop studying now" )
                .setSmallIcon( R.drawable.ic_launcher );

//        Uri alarmSound = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM );
//        nb.setSound( alarmSound );

        Notification notification = nb.build();
        notification.flags |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManager nm = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        nm.notify( STUDY_END, 0, notification );
    }

    private void updateTimer( long timeLeft, String timerName )
    {
        int secs = (int) timeLeft;
        int mins = secs / 60;
        int hours = mins / 60;

        mins = mins % 60;
        secs = secs % 60;

        String text = String.format( "%01d:%02d:%02d", hours, mins, secs );
        timerText.setText( timerName + "\n" + text );
    }

    private Thread tickReceiverThread = new Thread()
    {
        @Override
        public void run()
        {
            IntentFilter intentFilter = new IntentFilter( TimerService.TICK_SECOND );
            registerReceiver( tickReceiver, intentFilter );
        }

        public BroadcastReceiver tickReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive( Context context, Intent intent )
            {
                long timeLeft = intent.getLongExtra( TimerService.TIME_LEFT, 0 );
                String timerName = intent.getStringExtra( TimerService.SESSION_NAME );

                updateTimer( timeLeft, timerName );

                if(timeLeft <= 0) {
                    if(timerName.equals("studyTimer")) {
                        long breakLength = getIntent().getLongExtra(BREAK_LENGTH, 0);
                        addTimer("breakTimer", breakLength);
                    } else if (timerName.equals("breakTimer")) {
                        long breakLength = getIntent().getLongExtra(STUDY_LENGTH, 0);
                        addTimer("study0", breakLength);
                    }
                }
            }
        };
    };

}
