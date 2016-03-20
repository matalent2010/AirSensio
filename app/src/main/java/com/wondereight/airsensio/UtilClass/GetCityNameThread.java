package com.wondereight.airsensio.UtilClass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.wondereight.airsensio.Helper._Debug;
import com.wondereight.airsensio.Interface.ThreadCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by VENUSPC on 2/26/2016.
 */
public class GetCityNameThread implements Runnable {
    private final String LOG_TAG = "GetCityNameThread";
    private static _Debug _debug = new _Debug(true);

    private Context _context;
    private ThreadCallback _callback;

    // Flag for GPS status
    boolean isGPSEnabled = false;
    // Flag for network status
    boolean isNetworkEnabled = false;
    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude = 0; // Latitude
    double longitude = 0; // Longitude
    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GetCityNameThread(Context context, ThreadCallback callback) {
        this._context = context;
        this._callback = callback;
    }

    @Override
    public void run() {
        try {
            String cityName = "";
            locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
                _debug.e(LOG_TAG, "GPS Provider is not available.");
                Global.GetInstance().SetGoeCityName("");
                _callback.runFailCallback("GPS Provider is not available. Please allow the permission and try again.");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    _debug.d(LOG_TAG, "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            _debug.d(LOG_TAG, String.format("Geolocation by network : %f, %f", latitude, longitude));
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        _debug.d(LOG_TAG, "GPS Enabled");
                        if (locationManager != null) {
                            if (checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                _debug.e(LOG_TAG, "The permission of Location is not granted.");
                            } else {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    _debug.d(LOG_TAG, String.format("Geolocation by GPS : %f, %f", latitude, longitude));
                                }
                            }
                        }
                    }
                }
                Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
                if(geocoder == null) {
                    _debug.e(LOG_TAG, "Geocoder is not created.");
                    Global.GetInstance().SetGoeCityName("");
                    _callback.runFailCallback("Geocoder is not created.");
                    return;
                }
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if( addresses.isEmpty() ){
                        _debug.e(LOG_TAG, "address is empty.");
                        cityName = "";
                    } else {
                        cityName = addresses.get(0).getLocality(); //getAddressLine(0);
                        if (cityName == null || cityName.isEmpty())
                            cityName = addresses.get(0).getSubAdminArea();
                        if (cityName == null || cityName.isEmpty())
                            cityName = addresses.get(0).getAdminArea();
                        if (cityName == null || cityName.isEmpty())
                            cityName = "";
                        String countryName = addresses.get(0).getCountryName();
                        _debug.d(LOG_TAG, "City:" + cityName + "\nCountry:" + countryName);
                    }

                } catch (IllegalArgumentException | IOException e){
                    e.printStackTrace();
                    _debug.e(LOG_TAG, e.getMessage());
                    cityName = "";
                }
                Global.GetInstance().SetGeolocation(latitude + ", " + longitude);
                Global.GetInstance().SetGoeCityName(cityName);
                _callback.runSuccessCallback();
            }

        } catch (IllegalArgumentException e){
            e.printStackTrace();
            _debug.e(LOG_TAG, e.getMessage());
            Global.GetInstance().SetGeolocation(latitude + ", " + longitude);
            Global.GetInstance().SetGoeCityName("");
            _callback.runFailCallback(e.getMessage());
        }
    }
}
