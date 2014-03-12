package aston.team4.studytimer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Timer;

public class TimerService extends IntentService
{
    public static final String SERVICE_NAME = "aston.team4.studytimer.TimerService";
    public static final String SESSION_LENGTH = "aston.team4.studytimer.TimerService.SESSION_LENGTH";
    public static final String SESSION_NAME = "aston.team4.studytimer.TimerService.SESSION_NAME";
    public static final String TICK_MINUTE = "aston.team4.studytimer.TimerService.TICK_MINUTE";
    public static final String TICK_SECOND = "aston.team4.studytimer.TimerService.TICK_SECOND";
    public static final String TIME_LEFT = "aston.team4.studytimer.TimerService.TIME_LEFT";

    private static final int TIMER_DELAY = 100; //Maybe up this to 1 second?

    private ArrayList<TimerEvent> timers = new ArrayList<TimerEvent>();
    private Handler customHandler = new Handler();

    public TimerService()
    {
        super( SERVICE_NAME );
    }

    @Override
    public IBinder onBind( Intent intent )
    {
        return null;
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent( Intent intent )
    {
        long runningTime = intent.getLongExtra( SESSION_LENGTH, -1 );
        String timerName = intent.getStringExtra( SESSION_NAME );
        if ( runningTime < 0 || timerName.length() > 0 )
        {
            return;
        }

        TimerEvent te = new TimerEvent();
        te.startTime = SystemClock.uptimeMillis();
        te.length = runningTime;
        te.timerName = timerName;

        timers.add( te );

        startTimerThread();
    }

    private boolean isTimerRunning = false;

    private void startTimerThread() //Is there a better way to have this without needed isTimerRunning?
    {
        if ( !isTimerRunning )
        {
            customHandler.postDelayed( updateTimerThread, TIMER_DELAY );
            isTimerRunning = true;
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        private long lastMinute = 0;

        public void run()
        {
            long cTime = SystemClock.uptimeMillis();

            for ( TimerEvent te : timers )
            {
                if ( ( cTime - te.lastUpdate ) > 1000 ) //Ensure it has been at least 1 second since the last update, no point waiting any less
                {
                    long timeMsec = cTime - te.startTime;
                    long timeLeftMsec = te.length - timeMsec;
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

    }

    private void onTick1Minute( TimerEvent te, long timeLeftMsec )
    {

    }

    private void sendTimerUpdate( TimerEvent te, long timeLeftMsec )
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction( TICK_SECOND );
        broadcastIntent.addCategory( Intent.CATEGORY_DEFAULT );
        broadcastIntent.putExtra( SESSION_NAME, te.timerName );
        broadcastIntent.putExtra( TICK_SECOND, Math.round( timeLeftMsec / 1000 ) );
        sendBroadcast( broadcastIntent );
    }

    private class TimerEvent
    {
        public long startTime;
        public long length;
        public String timerName;
        public long lastUpdate;
    }
}
