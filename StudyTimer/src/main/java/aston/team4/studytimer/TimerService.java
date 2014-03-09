package aston.team4.studytimer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimerService extends Service
{
    public static final String SESSION_LENGTH = "aston.team4.studytimer.TimerService.SESSION_LENGTH";
    public static final String TICK_MINUTE = "aston.team4.studytimer.TimerService.TICK_MINUTE";
    public static final String TICK_SECOND = "aston.team4.studytimer.TimerService.TICK_SECOND";


    public TimerService()
    {
    }

    @Override
    public IBinder onBind( Intent intent )
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException( "Not yet implemented" );
    }
}
