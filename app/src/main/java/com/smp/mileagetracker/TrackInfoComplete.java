package com.smp.mileagetracker;

import java.io.Serializable;

/**
 * Created by Steve on 1/11/15.
 */
public class TrackInfoComplete implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;

    long startTime, endTime;
    double totalDistance;
    double averageSpeed;
    double totalExpense;
    long totalTime;
}
