package aston.team4.studytimer;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends ActionBarActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
      //  setContentView( R.layout.activity_setup );
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

    public void onStudyStartButtonPressed( View view ) {
        closeKeyboard();

        Intent intent = new Intent(this, TimerActivity.class);

        String inputSessionHours = ((EditText) findViewById(R.id.InputSessionHours)).getText().toString();
        String inputSessionMins  = ((EditText) findViewById(R.id.InputSessionMins)).getText().toString();
        String inputStudyMins    = ((EditText) findViewById(R.id.InputStudyMins)).getText().toString();
        String inputBreakMins    = ((EditText) findViewById(R.id.InputBreakMins)).getText().toString();

        long totalTime = getTime(inputSessionHours,inputSessionMins);
        long studyTime = getTime("0", inputStudyMins); // as no hours for study, input "0"
        long breakTime = getTime("0", inputBreakMins); // as no hours for break, input "0"

        if(totalTime < (studyTime + breakTime)) {
            printToast("Total must be greater than study and break.");
            return;
        }

        if(totalTime <= 0 || studyTime <= 0 || breakTime <= 0) {
            printToast("Please fill out each section");
            return;
        }

        intent.putExtra(TimerActivity.SESSION_LENGTH, totalTime);
        intent.putExtra(TimerActivity.STUDY_LENGTH, studyTime);
        intent.putExtra(TimerActivity.BREAK_LENGTH, breakTime);

        startActivity(intent);
    }

    private long getTime(String hours, String mins) {
        long inputHoursAsSecs = 0;
        long inputMinsAsSecs = 0;

        if(hours != null && hours.trim().length() > 0) {
            inputHoursAsSecs = Integer.valueOf(hours);
        }

        if(mins != null && mins.trim().length() > 0) {
            inputMinsAsSecs = Integer.valueOf(mins);
        }

        return ((inputHoursAsSecs * 60) * 60) + (inputMinsAsSecs * 60);
    }

    /**
     * Print out a short android toast with a message.
     * @param message The message to display on screen - should be kept short.
     */
    public void printToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void closeKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

}