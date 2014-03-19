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

        EditText sessionTimeInput = (EditText) findViewById( R.id.SessionTimeInput );
        long sessionTime = Integer.valueOf( sessionTimeInput.getText().toString() );

        EditText studyTimeInput = (EditText) findViewById( R.id.StudyTimeInput );
        long studyTime = Integer.valueOf( studyTimeInput.getText().toString() );

        EditText breakTimeInput = (EditText) findViewById( R.id.BreakTimeInput );
        long breakTime = Integer.valueOf( breakTimeInput.getText().toString() );

        intent.putExtra( TimerActivity.SESSION_LENGTH, sessionTime * 60 );
        intent.putExtra( TimerActivity.STUDY_LENGTH, studyTime * 60 );
        intent.putExtra( TimerActivity.BREAK_LENGTH, breakTime * 60 );

        startActivity( intent );
    }

}
