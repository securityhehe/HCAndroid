package com.hc.uicomponent.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;

import static android.content.Context.LOCATION_SERVICE;

public class LocationUtils {

    public boolean isSuccFinishLocationFlag = false;

    private static double mLocLat = 0.0;
    private static double mLocLng = 0.0;

    private static LocationUtils mLoactionUtils = new LocationUtils();
    public static LocationUtils getInstance(){
        return new LocationUtils();
    }
    private LoanLocationListener gpsListener;

    private LocationUtils() {
    }

    /**
     * 手机是否开启位置服务，如果没有开启将无法定位
     */
    public static boolean isLocServiceEnable(Context app) {
        try {
            LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //System.out.println("location---->isNetwork-->" + isNetworkEnable + "<----");
            return isNetworkEnable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 手机是否开启网络定位服务
     */
    public static boolean isNetworkEnable(Context app) {
        try {
            LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //System.out.println("location---->isNetwork-->" + isNetworkEnable + "<----");
            return isNetworkEnable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 手机是否开启GPS定位服务
     */
    public static boolean isGpsEnable(Context app) {
        try {
            LocationManager locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //System.out.println("location---->gps->" + isGpsEnable + "<----");
            return isGpsEnable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** >>>>>>>>>>>>>>>>>> android.location定位服务 <<<<<<<<<<<<<<<<<<< start **/
    private boolean controlGpsStartLocOnceFlag;
    private LocationManager gpsManager;
    public void startGps(Context app) {
        try {
            if (ActivityCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (controlGpsStartLocOnceFlag) return;
            gpsListener = new LoanLocationListener(app);
            // 获取到LocationManager对象
            gpsManager = (LocationManager) app.getSystemService(LOCATION_SERVICE);
            //provider可为gps定位，也可为为基站和WIFI定位 //注意：权限
            gpsManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, gpsListener);
            //System.out.println("--->gps-location----->> start -----");
            controlGpsStartLocOnceFlag = true;
        } catch (Exception e) {
            //System.out.println("--->gps-location----->> register error -----");
            e.printStackTrace();
        }
    }

    public void stopGps() {
        try {
            //System.out.println("--->gps-location-stopGps");
            if (gpsManager != null && gpsListener != null)
                gpsManager.removeUpdates(gpsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetGpsLocationUpdates(Context app){
        if (isSuccFinishLocationFlag)return;

        if (controlGpsStartLocOnceFlag) {
            controlGpsStartLocOnceFlag = false;
            stopGps();
        }
        //System.out.println("--->gps-location-resetGpsLocationUpdates");
        startGps(app);
    }

    // 创建位置监听器
     class LoanLocationListener implements LocationListener {
        Context app;

        public LoanLocationListener(Context app){
           this.app = app;
        }
        // 位置发生改变时调用
        @Override
        public void onLocationChanged(Location location) {
            mLocLat = location.getLatitude();
            mLocLng = location.getLongitude();
            //System.out.println("--->gps-location----->>" +mLocLat + "," + mLocLng + "<<-----------");
//            isSuccFinishLocationFlag = true;
//            stopGps();
        }

        // provider失效时调用
        @Override
        public void onProviderDisabled(String provider) {
            //System.out.println("--->gps-location-->> disable");
        }

        // provider启用时调用
        @Override
        public void onProviderEnabled(String provider) {
            //System.out.println("--->gps-location-->> enable --provider--->" + provider);
            resetGpsLocationUpdates( app);
        }

        // 状态改变时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //System.out.println("--->gps-location-->> change");
        }
    };

    /** >>>>>>>>>>>>>>>>>>>> android.location定位服务 <<<<<<<<<<<<<<<<<<<<<<<< end **/

    /** >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 分隔开<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< **/


    /** >>>>>>>>>>>>>>> google gms location 定位服务<<<<<<<<<<<<<<<<<<<< start **/

    private static final String TAG = LocationUtils.class.getSimpleName();

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 3;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;


    private WeakReference<FragmentActivity> mWeakRefrerence;


    public void initLocation(Context app,FragmentActivity activity){
        try {
            if (!LocationUtils.isGoogleServiceAvailable(app)) return;
            //System.out.println("--->gsm-location-start");
            controlStartLocOnceFlag = false;
            isSuccFinishLocationFlag = false;

            if (mWeakRefrerence == null) {
                mWeakRefrerence = new WeakReference<>(activity);
            }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            mSettingsClient = LocationServices.getSettingsClient(activity);

            // Kick off the process of building the LocationCallback, LocationRequest, and
            // LocationSettingsRequest objects.
            createLocationCallback();
            createLocationRequest();
            buildLocationSettingsRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GpReqLoactionCallback gpReqLoactionCallback;

    public interface GpReqLoactionCallback{
        public void reqLocation(boolean isReqSucc);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        try {
            //System.out.println("--->gsm-location-createLocationCallback-1");
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        return;
                    }
                    Location currentLocation = locationResult.getLastLocation();
                    mLocLat = currentLocation.getLatitude();
                    mLocLng = currentLocation.getLongitude();

                    //System.out.println("--->gsm-location success --->>" + mLocLat +"," + mLocLng + "<<----------");
    //                stopLocationUpdates();
    //                isSuccFinishLocationFlag = true;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        //System.out.println("--->gsm-location-createLocationRequest-2");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        try {
            //System.out.println("--->gsm-location-createLocationRequest-3");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean controlStartLocOnceFlag;

    public void resetLocationUpdates(Context app,GpReqLoactionCallback gpReqLoactionCallback){
        if (isSuccFinishLocationFlag)return;

        if (controlStartLocOnceFlag) {
            controlStartLocOnceFlag = false;
            stopLocationUpdates(app);
        }
        this.gpReqLoactionCallback = gpReqLoactionCallback;
        //System.out.println("--->gsm-location-resetLocationUpdates");
        startLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        try {
            if (isNullActivity()) return;

            if (controlStartLocOnceFlag) return;
            controlStartLocOnceFlag = true;

            //System.out.println("--->gsm-location-startLocationUpdates-loading");
            // Begin by checking if the device has the necessary location settings.
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(mWeakRefrerence.get(), new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "--->gsm-location-All location settings are satisfied.");
                            if (gpReqLoactionCallback != null){
                                gpReqLoactionCallback.reqLocation(true);
                            }
                            //noinspection MissingPermission
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        }
                    })
                    .addOnFailureListener(mWeakRefrerence.get(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            try {
                                int statusCode = ((ApiException) e).getStatusCode();
                                switch (statusCode) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        Log.i(TAG, "--->gsm-location-Location settings are not satisfied. Attempting to upgrade location settings ");
                                        if (gpReqLoactionCallback != null){
                                            gpReqLoactionCallback.reqLocation(false);
                                        }
                                        try {
                                            // Show the dialog by calling startResolutionForResult(), and check the
                                            // result in onActivityResult().
                                            ResolvableApiException rae = (ResolvableApiException) e;
                                            rae.startResolutionForResult(mWeakRefrerence.get(), REQUEST_CHECK_SETTINGS);
                                        } catch (Exception sie) {
                                            Log.i(TAG, "--->gsm-location-PendingIntent unable to execute request.");
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        String errorMessage = "--->gsm-location-Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                        Log.e(TAG, errorMessage);
                                        if (gpReqLoactionCallback != null){
                                            gpReqLoactionCallback.reqLocation(false);
                                        }
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates(Context app) {
        try {
            if (isNullActivity()) return;
            if (isSuccFinishLocationFlag) return;
            if (!LocationUtils.isGoogleServiceAvailable(app)) return;

            //System.out.println("--->gsm-location-stopLocationUpdates-stop");
            // It is a good practice to remove location requests when the activity is in a paused or
            // stopped state. Doing so helps battery performance and is especially
            // recommended in applications that request frequent location updates.
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(mWeakRefrerence.get(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNullActivity(){
        return mWeakRefrerence == null || mWeakRefrerence.get() == null;
    }

    /** >>>>>>>>>>>>>>>>>>> google gms location 定位服务<<<<<<<<<<<<<<<<<<<<<<<<<< end **/

    /**
     * 获取定位的经纬度
     * @return
     */
    public static String getLatLng(){
        return   mLocLng + "," + mLocLat;
    }

    /**
     * 判断是否打开了允许虚拟位置
     * @return boolean
     * @notice 使用前需要检验定位权限是否申请，否则校验不正确！
     */
    public static boolean isAllowMockLocation(Context context) {
        boolean canMockPosition = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0以下
            canMockPosition = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0);
        } else {
            try {
                LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);//获得LocationManager引用
                String providerStr = LocationManager.GPS_PROVIDER;
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                // 模拟位置可用
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }

    /**
     * 直接跳转至位置信息设置界面
     */
    public void jump2LocationServiceClick(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * Google服务是否可用
     * @return
     */
    public static boolean isGoogleServiceAvailable(Context app){
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(app) == 0;
    }
}
