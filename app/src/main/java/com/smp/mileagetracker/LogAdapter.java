package com.smp.mileagetracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Steve on 1/11/15.
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>
{
    private List<TrackInfoComplete> data;
    private Context context;

    private final String DISTANCE_UNIT = "Miles";
    private final String SPEED_UNIT = "MPH";

    public LogAdapter(List<TrackInfoComplete> data, Context context)
    {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        TrackInfoComplete item = data.get(position);
        holder.textTotalTime.setText(processDuration(item.totalTime));
        holder.textStartDate.setText(processDate(item.startTime));
        holder.textStartTime.setText(processTime(item.startTime));
        holder.textEndTime.setText(processTime(item.endTime));
        holder.textTotalDistance.setText(processDistance(item.totalDistance));
        holder.textTotalExpense.setText(processTotalExpense(item.totalExpense));
        holder.textAverageSpeed.setText(processAverageSpeed(item.averageSpeed));
    }

    private String processAverageSpeed(double averageSpeed)
    {
        Locale locale = Locale.getDefault();
        return String.format(locale, "%.1f", averageSpeed) + " " + SPEED_UNIT;
    }

    private String processTotalExpense(double totalExpense)
    {
        Locale locale = Locale.getDefault();
        return DecimalFormat
                .getCurrencyInstance(locale).format(totalExpense);
    }

    private String processDistance(double totalDistance)
    {
        Locale locale = Locale.getDefault();
        return String.format(locale, "%.1f", totalDistance) + " " + DISTANCE_UNIT;
    }

    private String processDate(long startTime)
    {
        Locale locale = Locale.getDefault();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("M/d/yyyy");
        return dtf.print(startTime);
    }

    private String processTime(long startTime)
    {
        Locale locale = Locale.getDefault();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("h:mm a");
        return dtf.print(startTime);
    }


    private String processDuration(long totalTime)
    {
        Locale locale = Locale.getDefault();
        return TrackFragment.formatDuration(totalTime);
    }
    public void updateList(List<TrackInfoComplete> newData)
    {
        this.data = newData;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected ImageView icon;
        protected TextView textStartTime, textEndTime, textStartDate, textTotalTime,
                textTotalDistance, textTotalExpense, textAverageSpeed;

        public ViewHolder(View v)
        {
            super(v);
            //icon = (ImageView) v.findViewById(R.id.text_icon);
            textStartDate = (TextView) v.findViewById(R.id.text_date);
            textStartTime = (TextView) v.findViewById(R.id.text_start_date);
            textEndTime = (TextView) v.findViewById(R.id.text_end_date);
            textTotalDistance = (TextView) v.findViewById(R.id.text_total_distance);
            textTotalExpense = (TextView) v.findViewById(R.id.text_total_expense);
            textAverageSpeed = (TextView) v.findViewById(R.id.text_average_speed_complete);
            textTotalTime = (TextView) v.findViewById(R.id.text_total_time);

        }
    }
}

