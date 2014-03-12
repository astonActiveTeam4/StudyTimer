package aston.team4.studytimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TimerActivity extends ActionBarActivity
{
    public static final String SESSION_LENGTH = "aston.team4.studytimer.TimerActivity.SESSION_LENGTH";
    private static final String STUDY_END = "aston.team4.studytimer.TimerActivity.STUDY_END";

    private TextView timerText;

    private long startTime = 0L;

//    private Handler customHandler = new Handler();

//    private long sessionLength; //TODO: Remove this when timing is given its own service

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timer );
        long sessionLength = getIntent().getLongExtra( SESSION_LENGTH, -1 );

        timerText = (TextView) findViewById( R.id.TimerText );

//        Intent intent = new Intent( this, TimerService.class );
//        intent.putExtra( SESSION_LENGTH, sessionLength ); //TODO: add a session length inside of the service
//        startService( intent );

        //TODO: Remove this when timing is given its own service
        startTime = SystemClock.uptimeMillis();
//        customHandler.postDelayed( updateTimerThread, 0 );
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
        //TODO: write service broadcast recevier
    }

    public void intervalComplete()
    {

    }

    public void studyComplete()
    {
        Notification.Builder nb = new Notification.Builder( this )
                .setContentTitle( "Study Done" )
                .setContentText( "You can stop studying now" );

        if ( Build.VERSION.SDK_INT >= 11 )
        {
            nb.setSmallIcon( R.drawable.ic_launcher );
        }

        Uri alarmSound = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_ALARM );
        nb.setSound( alarmSound );

        Notification notification = nb.build();
        notification.flags |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        NotificationManager nm = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        nm.notify( STUDY_END, 0, notification );
    }

    private void updateTimer( long timeLeftMsec )
    {
        int secs = (int) ( timeLeftMsec / 1000 );
        int mins = secs / 60;
        int hours = mins / 60;

        mins = mins % 60;
        secs = secs % 60;

        String text = String.format( "%01d:%02d:%02d", hours, mins, secs );
        timerText.setText( text );
    }

//    //TODO: Remove this when timing is given its own service
//    //From: http://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
//    private Runnable updateTimerThread = new Runnable()
//    {
//        public void run()
//        {
//            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
//
//            long timeLeft = ( sessionLength * 1000 ) - timeInMilliseconds;
//
//            if ( timeLeft > 0 )
//            {
//                customHandler.postDelayed( this, 50 );
//            }
//            else
//            {
//                studyComplete();
//            }
//
//            updateTimer( timeLeft );
//        }
//
//    };

    private class TickReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            long timeLeft = intent.getLongExtra( TimerService.TIME_LEFT, -1 );
            String timerName = intent.getStringExtra( TimerService.SESSION_NAME );

            updateTimer( timeLeft );
        }
    }
}
