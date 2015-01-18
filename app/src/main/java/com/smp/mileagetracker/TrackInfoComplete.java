package com.smp.mileagetracker;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Steve on 1/11/15.
 */
public class TrackInfoComplete extends SugarRecord<TrackInfoComplete>
{

    long startTime, endTime;
    double totalDistance;
    double averageSpeed;
    double totalExpense;
    long totalTime;
}
