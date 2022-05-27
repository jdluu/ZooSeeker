package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class GPSTracker implements LocationListener {

    private Context context;
    private Activity activity;
    private static NodeDao dao;

    public boolean isGPSEnabled = false;

    public Location location;
    public double latitude;
    public double longitude;

    private boolean rejected = false;
    private String nextExhibit = "";

    private static final String provider = LocationManager.GPS_PROVIDER;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

    public LocationManager locationManager;

    public GPSTracker(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        dao = DirectionTracker.getDao(); // TODO: make sure DirectionTracker is called before GPSTracker
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(provider);

        if (!isGPSEnabled) {
            // gpsTracker only called if Precise Location granted
            // TODO: downgrade
        }
        else {
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
                                                   MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                Location lastKnownLocation =
                        locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation != null) {
                    location = lastKnownLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
        return location;
    }

    // OffTrack
    @Override
    public void onLocationChanged(@NonNull Location location) {
//        List<String> exhibitIds = DirectionTracker.currentExhibitIdsOrder;
//
//        List<Node> exhibits = new ArrayList<>();
//        for (String id : exhibitIds) {
//            Node exhibit = dao.get(id);
//            exhibits.add(exhibit);
//        }
//
//        Node next = exhibits.get(0);
//        double closestDistance = Integer.MAX_VALUE;
//        String closestExhibit = next.id;
//
//        if (rejected && !nextExhibit.equals(next.id)) {
//            rejected = false;
//        }
//
//        {
//            for (Node exhibit : exhibits) {
//                double distance = Utilities.getVincentyDistance(latitude, longitude,
//                                                                exhibit.latitude,
//                                                                exhibit.longitude);
//                if (distance < closestDistance) {
//                    closestDistance = distance;
//                    closestExhibit = exhibit.id;
//                }
//            }
//            /* Scenario 1 */
//            if (!closestExhibit.equals(next.id)) {
//                // prompt
//                boolean redirect = Utilities.showAlert(activity, "You are off track! Do" +
//                                                                 " you want to re-plan " +
//                                                                 "your directions?",
//                                                       "Yes", "No");
//                if (redirect) {
//                    DirectionTracker.redirect(findNearestNode(latitude, longitude));
//                }
//                else {
//                    // do nothing until next exhibit
//                    rejected = true;
//                    nextExhibit = next.id;
//                }
//            }
//            /* Scenario 2 */
//            else {
//                // TODO: check if passing nodes in correct order; waiting for Ziv's code
//                DirectionTracker.redirect(findNearestNode(latitude, longitude));
//            }
//        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    private String findNearestNode(double latitude, double longitude) {
        List<Node> nodes = ExhibitList.getAllNodes();
        double closestDistance = Integer.MAX_VALUE;
        String closestNodeId = "";

        for (Node node : nodes) {
            double distance = Utilities.getVincentyDistance(latitude, longitude,
                                                            node.latitude,
                                                            node.longitude);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestNodeId = node.id;
            }
        }

        return closestNodeId;
    }
}

