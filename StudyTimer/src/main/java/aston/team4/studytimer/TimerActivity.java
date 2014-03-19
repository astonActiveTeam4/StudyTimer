package aston.team4.studytimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TimerActivity extends ActionBarActivity
{
    public static final String SESSION_LENGTH = "aston.team4.studytimer.TimerActivity.SESSION_LENGTH";
    public static final String STUDY_LENGTH = "aston.team4.studytimer.TimerActivity.STUDY_LENGTH";
    public static final String BREAK_LENGTH = "aston.team4.studytimer.TimerActivity.BREAK_LENGTH";
    private static final String STUDY_END = "aston.team4.studytimer.TimerActivity.STUDY_END";

    //TODO: remove these strings, these IDs are temporary!
    private static final String STUDY_ID = "study time";
    private static final String BREAK_ID = "break time";

    private TextView timerText;

//    private long startTime = 0L;

    private Handler customHandler = new Handler();

    private long sessionLength, studyLength, breakLength, totalTimeStudied = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timer );
        sessionLength = getIntent().getLongExtra( SESSION_LENGTH, 0 );
        studyLength = getIntent().getLongExtra( STUDY_LENGTH, 0 );
        breakLength = getIntent().getLongExtra( BREAK_LENGTH, 0 );

        timerText = (TextView) findViewById( R.id.TimerText );

        addTimer( STUDY_ID, studyLength );
//        startService( intent );
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

    private void addTimer( String sessionName, long sessionLength )
    {
        Intent intent = new Intent( this, TimerService.class );
        intent.putExtra( TimerService.ACTION, TimerService.ACTION_ADD_TIMER );
        intent.putExtra( TimerService.SESSION_NAME, sessionName );
        intent.putExtra( TimerService.SESSION_LENGTH, sessionLength ); //TODO: add a session length inside of the service

        timerService.timerFromIntent( intent );
    }

    private void stopTimer( String sessionName )
    {
        Intent intent = new Intent( this, TimerService.class );
        intent.putExtra( TimerService.ACTION, TimerService.ACTION_STOP_TIMER );
        intent.putExtra( TimerService.SESSION_NAME, sessionName );

        timerService.timerFromIntent( intent );
    }

    public void intervalComplete( String timerName )
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

    private void updateTimer( long sessionTimeLeft, long intervalTimeLeft, String timerName )
    {
        int secs = (int) intervalTimeLeft;
        int mins = secs / 60;
        int hours = mins / 60;

        mins = mins % 60;
        secs = secs % 60;

        String text = String.format( "%01d:%02d:%02d", hours, mins, secs );

        //DEBUG PUT THIS SOMEWHERE ELSE
        secs = (int) sessionTimeLeft;
        mins = secs / 60;
        hours = mins / 60;

        mins = mins % 60;
        secs = secs % 60;


        String sessionText = String.format( "%01d:%02d:%02d", hours, mins, secs );

        timerText.setText( timerName + "\n" + text + "\nSession time left\n" + sessionText );
    }

    private TimerService timerService = new TimerService( this );

    public void tickCallback( Intent intent )
    {
        long intervalTimeLeft = intent.getLongExtra( TimerService.TIME_LEFT, 0 );
        long intervalLength = intent.getLongExtra( TimerService.SESSION_LENGTH, 0 );
        long sessionTimeLeft = ( sessionLength + intervalTimeLeft ) - ( totalTimeStudied + intervalLength ); //TODO: this algorithm works but I have no idea why, need to refactor it
        String timerName = intent.getStringExtra( TimerService.SESSION_NAME );

        updateTimer( sessionTimeLeft, intervalTimeLeft, timerName );

        //Move this down below the log down there to let the last interval end before stopping
        if ( sessionTimeLeft <= 0 )
        {
            //Total session length over
            stopTimer( timerName );

            updateTimer( sessionTimeLeft, intervalTimeLeft, "Studying over" );

            studyComplete();
            return;
        }

        if ( intervalTimeLeft <= 0 )
        {
            Log.d( "TimerActivity", "Stopping timer: " + timerName );
            stopTimer( timerName );

            intervalComplete( timerName );

            if ( timerName.equals( STUDY_ID ) )
            {
                addTimer( BREAK_ID, breakLength );
                totalTimeStudied += breakLength;
            }
            else if ( timerName.equals( BREAK_ID ) )
            {
                addTimer( STUDY_ID, studyLength );
                totalTimeStudied += studyLength;
            }
        }
    }

}
