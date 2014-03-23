package aston.team4.studytimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SetupActivity extends ActionBarActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setup );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.setup, menu );
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

    public void onStudyStartButtonPressed( View view )
    {
        Intent intent = new Intent( this, TimerActivity.class );

        EditText inputSessionHours = (EditText) findViewById( R.id.inputSessionHours );
        EditText inputSessionMins = (EditText) findViewById( R.id.inputSessionMins );
        long sessionHoursInSecs = (Integer.valueOf( inputSessionHours.getText().toString() ) * 60) * 60;
        long sessionMinsInSecs = Integer.valueOf( inputSessionMins.getText().toString() ) * 60;
        long sessionTime = sessionHoursInSecs + sessionMinsInSecs;

        EditText inputStudyHours = (EditText) findViewById( R.id.inputStudyHours );
        EditText inputStudyMins = (EditText) findViewById( R.id.inputStudyMins );
        long studyHoursInSecs = (Integer.valueOf( inputStudyHours.getText().toString() ) * 60) * 60;
        long studyMinsInSecs = Integer.valueOf( inputStudyMins.getText().toString() ) * 60;
        long studyTime = studyHoursInSecs + studyMinsInSecs;

        EditText inputBreakHours = (EditText) findViewById( R.id.inputBreakHours );
        EditText inputBreakMins = (EditText) findViewById( R.id.inputBreakMins );
        long breakHoursInSecs = (Integer.valueOf( inputBreakHours.getText().toString() ) * 60) * 60;
        long breakMinsInSecs = Integer.valueOf( inputBreakMins.getText().toString() ) * 60;
        long breakTime = breakHoursInSecs + breakMinsInSecs;

        intent.putExtra( TimerActivity.SESSION_LENGTH, sessionTime );
        intent.putExtra( TimerActivity.STUDY_LENGTH, studyTime );
        intent.putExtra( TimerActivity.BREAK_LENGTH, breakTime );

        startActivity( intent );
    }

}
