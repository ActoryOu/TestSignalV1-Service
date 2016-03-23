package edu.nctu.wirelab.testsignalv1;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

public class LocationUpdater implements LocationListener {
    private final Context mContext;
    private static final String TagName = "LocationUpdater";
    private static LocationManager locationManager;
    public static Location userlocation = null;

    public LocationUpdater(Context context){
        mContext = context;
        locationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
        userlocation = null;
    }

    public boolean isOpenGps() {
        // get the location by GPS
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // get the location by WLAN or mobile network. It's usually used at the place which is more hidden.
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    public void startGPS(){
//        Criteria criteria = new Criteria();	//資訊提供者選取標準
//        String bestProvider = locationManager.getBestProvider(criteria, true);	//選擇精準度最高的提供者
//        Log.d(TagName, "BestProvider:"+bestProvider+"  GPS_PROVIDER:"+LocationManager.GPS_PROVIDER);
//        userlocation = locationManager.getLastKnownLocation(bestProvider);
        userlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if( userlocation==null ){
            Log.d(TagName, "getLastKnownLocation: null");
        }
        else{
            Log.d(TagName, "getLastKnownLocation: Lat-"+userlocation.getLatitude()+", Lng-"+userlocation.getLongitude());
        }

        //getMainLooper() is used for updating the location by service
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, LocationUpdater.this, Looper.getMainLooper()); //(name of provider, minimum time interval, minDistance(m), listener, mainlooper)
    }

    public void stopGPS(){
        locationManager.removeUpdates(LocationUpdater.this);
    }

    @Override
    public void onLocationChanged(Location location){
        //Log.d("temp", "LOCATION UPDATE");
        //userlocation = new Location(location);
        Log.d(TagName, "onLocationChanged");
//        userlocation = new Location(location);
        if( userlocation==null )
            userlocation = new Location(location);
        else
            userlocation.set(location);

//        try {
//            if( RunIntentService2.filewriter!=null ){
//                RunIntentService2.filewriter.write("Lat: " + location.getLatitude() + "Long: " + location.getLongitude() + "\n");
//                RunIntentService2.filewriter.write("time: " + System.currentTimeMillis() + "\n\n" );
//                RunIntentService2.filewriter.write("---------------------------------------------------------\n\n" );
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
