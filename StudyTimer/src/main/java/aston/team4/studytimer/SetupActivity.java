package aston.team4.studytimer;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    public void onStudyStartButtonPressed( View view ) {
        Intent intent = new Intent(this, TimerActivity.class);

        /*
        Contains all the EditText fields for that will contribute to a timer.
         */
        EditText[] inputFields = {
                (EditText) findViewById(R.id.inputSessionHours),
                (EditText) findViewById(R.id.inputSessionMins),

                (EditText) findViewById(R.id.inputStudyHours),
                (EditText) findViewById(R.id.inputStudyMins),

                (EditText) findViewById(R.id.inputBreakHours),
                (EditText) findViewById(R.id.inputBreakMins),
        };

        /*
        Gets the values from the fields, converts to seconds and insert into TimerActivity.

        Every type of input (session, study, break) has 2 fields (HH and MM), therefore loop increments by 2.
         */
        boolean canStart = true;
        for (int i = 0; i < inputFields.length; i += 2) {
            String inputHoursString = inputFields[i].getText().toString();
            String inputMinsString = inputFields[i + 1].getText().toString();
            if(inputHoursString.length() == 0){
                inputHoursString= "0";

            }


            if (!((inputHoursString.length() == 0) || (inputMinsString.length() == 0))) {
                long inputHoursAsSecs = Integer.valueOf(inputHoursString);
                long inputMinsAsSecs = Integer.valueOf(inputMinsString);

                if((inputHoursAsSecs + inputMinsAsSecs) >= 0) { // make sure they're not set to 0. can't have timers endlessly switching between one and the other.
                    long inputTime = ((inputHoursAsSecs * 60) * 60) + (inputMinsAsSecs * 60);

                    if (i == 0) {
                        intent.putExtra(TimerActivity.SESSION_LENGTH, inputTime);
                    } else if (i == 2) {
                        intent.putExtra(TimerActivity.STUDY_LENGTH, inputTime);
                    } else if (i == 4) {
                        intent.putExtra(TimerActivity.BREAK_LENGTH, inputTime);
                    }
                } else {
                    canStart = false;
                }
            } else {
                canStart = false;

                printToast("Please fill in all fields.");
            }
        }

        if(canStart) {
            startActivity(intent);
        }
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

}