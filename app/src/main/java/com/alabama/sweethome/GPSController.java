package com.alabama.sweethome;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class GPSController implements LocationListener {
    private Context context;
    private SharedPreferences pref;
    private Location lastLocation;
    private Location homeLocation;
    boolean inHome;

    public GPSController(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences("home", Context.MODE_PRIVATE);
        this.lastLocation = null;
        this.inHome = true;
        this.homeLocation = restoreHome();

        LocationManager locationManager =
                (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Uruchom GPS!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("MyApp", "ERR: BRAK UPRAWNIEN!");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                0, this);
    }

    private synchronized Location restoreHome() {
        Location location = new Location("jp2gmd");
        if(!pref.contains("lat")) {
            return null;
        }
        location.setLatitude(pref.getFloat("lat", 0f));
        location.setLongitude(pref.getFloat("lon", 0f));
        return location;
    }

    public synchronized boolean setHome() {
        if(lastLocation == null) {
            return false;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("lat", (float)lastLocation.getLatitude());
        editor.putFloat("lon", (float)lastLocation.getLongitude());
        editor.apply();
        inHome = true;
        this.homeLocation = restoreHome();
        return true;
    }

    public synchronized void removeHome() {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("lat");
        editor.remove("lon");
        editor.apply();
        this.homeLocation = null;
    }

    public synchronized boolean isHomeSet() {
        return homeLocation != null;
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        lastLocation = new Location(location);

        if(homeLocation == null) {
            return;
        }

        if(!inHome && location.distanceTo(homeLocation) < 10) {
            inHome = true;
        }

        if(inHome && location.distanceTo(homeLocation) > 40.0) {
            inHome = false;
            systemNotify("Pamiętaj o maseczce!");
        } else if(!inHome && location.distanceTo(homeLocation) < 40.0) {
            inHome = true;
            systemNotify("Zdezynfekuj ręce!");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    private void systemNotify(String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context.getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context.getApplicationContext());
        }

        builder = builder
                .setSmallIcon(R.drawable.ic_round_home_24)
                .setColor(ContextCompat.getColor(context, R.color.dgreen))
                .setContentTitle(context.getString(R.string.app_name))
                .setTicker("2137")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        notificationManager.notify(0, builder.build());
    }
}
