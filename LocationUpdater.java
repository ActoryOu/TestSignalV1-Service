package edu.nctu.wirelab.testsignalv1;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Actory on 2016/1/13.
 */
public class LocationUpdater implements LocationListener {
    private LocationManager locationManager;
    public static Location userlocation;

    public LocationUpdater(LocationManager lm){
        locationManager = lm;
        userlocation = null;
    }

    public boolean isOpenGps() {
        // get the location by GPS
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // get the location by WLAN or mobile network. It's usually used at the place which is more hidden.
        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            //locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,2000,10, this);

            Criteria criteria = new Criteria();	//資訊提供者選取標準
            String bestProvider = locationManager.getBestProvider(criteria, true);	//選擇精準度最高的提供者
            Location location = locationManager.getLastKnownLocation(bestProvider);

            Log.d("temp", "HI");
            if( location!=null ) {
                Log.d("temp", "Lat:" + location.getLatitude() + "  Lnt:" + location.getLongitude());
                userlocation = location;
                locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER,2000,10, this);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d("temp", "LOCATION UPDATE");
        //userlocation = new Location(location);
        userlocation.set(location);
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
