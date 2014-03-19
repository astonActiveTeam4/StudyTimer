package aston.team4.studytimer;

//import android.app.IntentService;

import android.content.Intent;
import android.os.Handler;
//import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;

public class TimerService
{
    public static final String SERVICE_NAME = "aston.team4.studytimer.TimerService";
    public static final String SESSION_LENGTH = "aston.team4.studytimer.TimerService.SESSION_LENGTH";
    public static final String SESSION_NAME = "aston.team4.studytimer.TimerService.SESSION_NAME";
    public static final String TICK_MINUTE = "aston.team4.studytimer.TimerService.TICK_MINUTE";
    public static final String TICK_SECOND = "aston.team4.studytimer.TimerService.TICK_SECOND";
    public static final String TIME_LEFT = "aston.team4.studytimer.TimerService.TIME_LEFT";
    public static final String ALERT_SESSION_END = "aston.team4.studytimer.TimerService.ALERT_SESSION_END";
    public static final String ACTION = "aston.team4.studytimer.TimerService.ACTION";
    public static final String ACTION_ADD_TIMER = "aston.team4.studytimer.TimerService.ACTION.ADD_TIMER";
    public static final String ACTION_STOP_TIMER = "aston.team4.studytimer.TimerService.ACTION_STOP_TIMER";

    private static final int TIMER_DELAY = 500; //Maybe up this to 1 second?

    private HashMap<String, TimerEvent> timers = new HashMap<String, TimerEvent>();
    private Handler customHandler = new Handler();


    private TimerActivity timerActivity;

    public TimerService( TimerActivity ta )
    {
        timerActivity = ta;
    }

    public void timerFromIntent( Intent intent )
    {
        String action = intent.getStringExtra( ACTION );

        Log.d( SERVICE_NAME, "Got intent" );
        Log.d( SERVICE_NAME, "Intent: " + action );

        if ( action.equals( ACTION_ADD_TIMER ) )
        {
            addTimerIntent( intent );
        }
        else if ( action.equals( ACTION_STOP_TIMER ) )
        {
            stopTimerIntent( intent );
        }
    }

    private void addTimerIntent( Intent intent )
    {
        Log.d( SERVICE_NAME, "Adding timer" );
        long runningTime = intent.getLongExtra( SESSION_LENGTH, -1 );
        String timerName = intent.getStringExtra( SESSION_NAME );
        if ( runningTime < 0 || timerName.length() == 0 )
        {
            return;
        }

        TimerEvent te = new TimerEvent();
        te.startTime = SystemClock.uptimeMillis();
        te.length = runningTime;
        te.timerName = timerName;

        timers.put( timerName, te );

        Log.d( SERVICE_NAME, "Added timer" );

        startTimerThread();
    }

    private void stopTimerIntent( Intent intent )
    {
        Log.d( SERVICE_NAME, "Stopping timer" );
        String timerName = intent.getStringExtra( SESSION_NAME );

        onAlertSessionEnd( timers.get( timerName ) );
        timers.remove( timerName );
    }

    private boolean isTimerRunning = false;

    private void startTimerThread() //Is there a better way to have this without needed isTimerRunning?
    {
        if ( !isTimerRunning )
        {
            customHandler.postDelayed( updateTimerThread, TIMER_DELAY );
            isTimerRunning = true;

            Log.d( SERVICE_NAME, "Started timer" );
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        private long lastMinute = 0;

        public void run()
        {
            long cTimeMsec = SystemClock.uptimeMillis();

            boolean notifyMinute = false;
            long cMinute = Math.round( ( cTimeMsec / 1000 ) / 60 );
            if ( ( cMinute % 60 ) == 0 && cMinute != lastMinute )
            {
                notifyMinute = true;
                lastMinute = cMinute;
            }

            for ( TimerEvent te : timers.values() )
            {
                if ( ( cTimeMsec - te.lastUpdate ) > 1000 ) //Ensure it has been at least 1 second since the last update, no point waiting any less
                {
                    long timePassedMsec = cTimeMsec - te.startTime;
                    long timeLeftMsec = te.length - timePassedMsec;

                    onTick1Second( te, timeLeftMsec );

                    if ( notifyMinute )
                    {
                        onTick1Minute( te, timeLeftMsec );
                    }

                    if ( timeLeftMsec <= 0 )
                    {
                        onAlertSessionEnd( te );
                    }
                }
            }

            if ( timers.size() > 0 )
            {
                customHandler.postDelayed( this, TIMER_DELAY );
            }
            else
            {
                isTimerRunning = false;
            }
        }

    };

    private void onTick1Second( TimerEvent te, long timeLeftMsec )
    {
        Log.d( SERVICE_NAME, "Broadcast 1 second!" );

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction( TICK_SECOND );
        broadcastIntent.putExtra( SESSION_NAME, te.timerName );
        broadcastIntent.putExtra( TICK_SECOND, timeLeftMsec / 1000 );
//        sendBroadcast( broadcastIntent );

        timerActivity.tickCallback( broadcastIntent );
    }

    private void onTick1Minute( TimerEvent te, long timeLeftMsec ) //Do we actually need this?
    {
        Log.d( SERVICE_NAME, "Broadcast 1 minute!" );
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction( TICK_MINUTE );
        broadcastIntent.putExtra( SESSION_NAME, te.timerName );
        broadcastIntent.putExtra( TIME_LEFT, timeLeftMsec / 1000 );
//        sendBroadcast( broadcastIntent );

        timerActivity.tickCallback( broadcastIntent );
    }

    private void onAlertSessionEnd( TimerEvent te )
    {

    }

    /**
     * TimerEvent
     * <p/>
     * All times are in milliseconds
     */
    private class TimerEvent
    {
        public long startTime;
        public long length;
        public String timerName;
        public long lastUpdate;
    }

    //Foreground running ahead
}
