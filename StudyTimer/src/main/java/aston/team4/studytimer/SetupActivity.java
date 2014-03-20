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

        EditText studyTimeInput = (EditText) findViewById( R.id.StudyTimeInput );
        int studyTime = Integer.valueOf( studyTimeInput.getText().toString() );

        EditText breakTimeInput = (EditText) findViewById( R.id.BreakTimeInput );
        int breakTime = Integer.valueOf( breakTimeInput.getText().toString() );

        intent.putExtra( TimerActivity.STUDY_LENGTH, (long) ( studyTime * 60 ) );
        intent.putExtra( TimerActivity.BREAK_LENGTH, (long) ( breakTime * 60 ));

        startActivity( intent );
    }

}
