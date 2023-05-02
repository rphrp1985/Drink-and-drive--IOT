package com.prianshu.safedriving

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale


class locationService : Service() {

    val db = Firebase.firestore

    override fun onBind(intent: Intent): IBinder? {
//        TODO("Return the communication channel to the service.")

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


                requestNewLocationData()



        return super.onStartCommand(intent, flags, startId)
    }
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

        Handler().postDelayed({
            requestNewLocationData()
        },60000)


    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            DATA.longitude= mLastLocation.latitude.toString()
            DATA.latitude = mLastLocation.longitude.toString()
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val addresses = geocoder.getFromLocation(
                mLastLocation.latitude,
                mLastLocation.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            val address: String? = addresses?.get(0)?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city: String? = addresses?.get(0)?.getLocality()
            val state: String? = addresses?.get(0)?.getAdminArea()
            val country: String? = addresses?.get(0)?.getCountryName()
            val postalCode: String? = addresses?.get(0)?.getPostalCode()
            val knownName: String? = addresses?.get(0)?.getFeatureName()

            DATA.address = address.toString()


              publishDATA()
            Log.d("RP-LOCATION","${DATA.longitude}  ${DATA.latitude } ${DATA.address}")
//            latitudeTextView.setText("Latitude: " + mLastLocation.latitude + "")
//            longitTextView.setText("Longitude: " + mLastLocation.longitude + "")
        }
    }

    fun publishDATA(){
        db.collection("location").document("${DATA.car_num}").set(DATA)
            .addOnSuccessListener { documentReference ->
                Log.d("FIRESTORE_rP", "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w("FIRESTORE_rP", "Error adding document", e)
            }
    }


}

object DATA{
    var car_num=""
    var eme_num=""
    var latitude=""
    var longitude=""
    var address=""
}