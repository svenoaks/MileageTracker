package com.smp.mileagetracker;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TrackActivity extends ActionBarActivity
{
    @Override
    public void onBackPressed()
    {
        new MyDialogFragment().show(getSupportFragmentManager(), "MyDialog");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TrackFragment())
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public static class MyDialogFragment extends DialogFragment
    {


        public MyDialogFragment()
        {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            Context context = getActivity();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_cancel_title));
            alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_cancel_message));
            //null should be your on click listener
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_cancel_positive),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getActivity().finish();
                        }
                    });
            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel_negative),
                    new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });


            return alertDialogBuilder.create();
        }
    }
}
