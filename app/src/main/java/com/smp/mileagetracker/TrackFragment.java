package com.smp.mileagetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class TrackFragment extends android.support.v4.app.Fragment implements View.OnClickListener
{
    @InjectView(R.id.text_distance)
    TextView textDistance;
    @InjectView(R.id.text_average_speed)
    TextView textAverageSpeed;
    @InjectView(R.id.text_current_speed)
    TextView textCurrentSpeed;
    @InjectView(R.id.text_money)
    TextView textMoney;
    @InjectView(R.id.text_time)
    TextView textTime;
    @InjectView(R.id.button_finish)
    Button buttonFinish;

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSED = 2;
    private static final String CURRENCY_SYMBOL_DEFAULT = "$";

    private int state;

    private TrackInfoComplete finishedInfo;

    public static TrackFragment newInstance(int sectionNumber)
    {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackFragment()
    {
    }

    public void onEvent(TrackInfoUpdateMessage message)
    {
        if (finishedInfo == null)
        {
            finishedInfo = new TrackInfoComplete();
            finishedInfo.startTime = System.currentTimeMillis();
        }
        updateTime(message);
        updateDistance(message);
        updateCurrentSpeed(message);
        updateAverageSpeed(message);
        updateExpense(message);
    }

    private void updateExpense(TrackInfoUpdateMessage message)
    {
        Locale locale = Locale.getDefault();
        textMoney.setText(DecimalFormat
                .getCurrencyInstance(locale).format(message.totalExpense));
    }

    private void updateAverageSpeed(TrackInfoUpdateMessage message)
    {
        Locale locale = Locale.getDefault();
        double averageSpeed = calculateAverageSpeed(message);

        textAverageSpeed.setText(String.format(locale, "%.1f", averageSpeed));
    }

    private double calculateAverageSpeed(TrackInfoUpdateMessage message)
    {
        double time = message.totalTime;
        double distance = message.totalDistance;

        double hours = time / (1000 * 60 * 60);
        return hours == 0.0 ? 0.0 : distance / hours;
    }

    private void updateCurrentSpeed(TrackInfoUpdateMessage message)
    {
        Locale locale = Locale.getDefault();
        textCurrentSpeed.setText(String.format(locale, "%.1f", message.currentSpeed));
    }

    private void updateDistance(TrackInfoUpdateMessage message)
    {
        Locale locale = Locale.getDefault();
        textDistance.setText(String.format(locale, "%.1f", message.totalDistance));
    }

    private void updateTime(TrackInfoUpdateMessage message)
    {
        textTime.setText(formatDuration(message.totalTime));
    }

    public static String formatDuration(long timems)
    {
        Period time = new Period(timems);
        PeriodFormatter dhm = new PeriodFormatterBuilder()
                .printZeroAlways()
                .appendHours()
                .appendSeparator(":")
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();

        return dhm.print(time.normalizedStandard());
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        state = STATE_RUNNING;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        ButterKnife.inject(this, rootView);
        buttonFinish.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sendDefaultEvent();
        EventBus.getDefault().registerSticky(this);
        getActivity().startService(new Intent(getActivity(), TrackService.class));

    }

    private void sendDefaultEvent()
    {
        TrackInfoUpdateMessage message = new TrackInfoUpdateMessage();
        onEvent(message);
    }

    @Override
    public void onPause()
    {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }



    @Override
    public void onDestroy()
    {
        Intent intent = new Intent(getActivity(), TrackService.class);
        getActivity().stopService(intent);
        EventBus.getDefault().removeStickyEvent(TrackInfoUpdateMessage.class);
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_finish:
                saveTracking();
                break;
        }
    }

    private void saveTracking()
    {
        if (finishedInfo != null)
        {
            TrackInfoUpdateMessage last = EventBus.getDefault()
                    .getStickyEvent(TrackInfoUpdateMessage.class);
            if (last != null)
            {
                finishedInfo.endTime = System.currentTimeMillis();
                finishedInfo.averageSpeed = calculateAverageSpeed(last);
                finishedInfo.totalDistance = last.totalDistance;
                finishedInfo.totalTime = last.totalTime;
                finishedInfo.totalExpense = last.totalExpense;

                finishedInfo.save();

                EventBus.getDefault().post(new NewLogItemEvent());
            }
        }
        getActivity().finish();
    }


}