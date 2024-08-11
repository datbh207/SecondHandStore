package com.example.fashionstoreapp.GoogleMap;

import android.content.Context;

import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.maps.model.LatLng;

public class RouteManager {
    private final Context context;
    private final RouteListener routeListener;

    public RouteManager(Context context, RouteListener routeListener) {
        this.context = context;
        this.routeListener = routeListener;
    }

    public void drawRoute(LatLng origin, LatLng destination) {
        RouteDrawing routeDrawing = new RouteDrawing.Builder()
                .context(context)
                .travelMode(AbstractRouting.TravelMode.DRIVING) // Using Driving Mode
                .withListener(routeListener)
                .alternativeRoutes(true)
                .waypoints(origin, destination)
                .build();
        routeDrawing.execute();
    }
}
