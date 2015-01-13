package com.smp.mileagetracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import de.greenrobot.event.EventBus;

public class TrackService extends Service implements ConnectionCallbacks, LocationListener,
        OnConnectionFailedListener
{
    private static final String WAKELOCK_NAME = "com.smp.mileagetracker.wakelock";

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    PowerManager.WakeLock wakeLock;

    double mTotalDistance;
    long mTotalTime;

    public TrackService()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        acquireWakeLock();
        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        startServiceForeground();

    }
    private static final int ONGOING_NOTIFICATION_ID = 345438731;
    private void startServiceForeground()
    {
        Intent activityIntent = new Intent(this, TrackActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent aIntent = PendingIntent.getActivity(this, 0,
                activityIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setContentIntent(aIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void acquireWakeLock()
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                WAKELOCK_NAME);
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        wakeLock.release();
        stopForeground(true);
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates()
    {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        startLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (mCurrentLocation == null)
        {
            mCurrentLocation = location;
            return;
        } else
        {
            TrackInfoUpdateMessage message = new TrackInfoUpdateMessage();
            message.currentSpeed = calculateCurrentSpeed(location);
            message.totalTime = calculateTotalTime(location);
            message.totalExpense = calculateTotalExpense(location);
            message.totalDistance = calculateTotalDistance(location);
            mCurrentLocation = location;
            EventBus.getDefault().postSticky(message);
        }
    }

    private static final double DISTANCE_FUZZ = 1.0;

    private double calculateTotalDistance(Location location)
    {
        float[] result = new float[1];
        Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), result);
        double miles = result[0] < DISTANCE_FUZZ ? 0.0 : metersToMiles(result[0]);
        mTotalDistance += miles;
        return mTotalDistance;
    }

    private double metersToMiles(double meters)
    {
        return meters * 0.000621371192237334;
    }

    private double msToMph(double ms)
    {
        return ms * 2.23694;
    }

    private double metersToFeet(double meters)
    {
        return meters * 3.28084;
    }

    private static final double CURRENCY_MULTIPLIER_DEFAULT = 0.55;

    private double calculateTotalExpense(Location location)
    {
        return CURRENCY_MULTIPLIER_DEFAULT * mTotalDistance;

    }

    private long calculateTotalTime(Location location)
    {
        mTotalTime += location.getTime() - mCurrentLocation.getTime();
        return mTotalTime;
    }

    private double calculateCurrentSpeed(Location location)
    {
        return msToMph(location.getSpeed());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Toast.makeText(this, "CONNECTION FAILED", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }


}
