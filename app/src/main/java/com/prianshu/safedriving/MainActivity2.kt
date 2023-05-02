package com.prianshu.safedriving

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import org.checkerframework.checker.nullness.qual.NonNull


class MainActivity2 : AppCompatActivity() {


    var mFusedLocationClient: FusedLocationProviderClient? = null

    var car_number:String?=""
    var emer:String?=""

    override fun onResume() {
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        car_number = sh.getString("car", "")
        emer = sh.getString("em","")

        super.onResume()
    }

    lateinit var car_text:TextView
    lateinit var em_text:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(com.google.android.material.R.style.Theme_AppCompat)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val card = findViewById<CardView>(R.id.card)

        card.visibility= View.GONE




        Handler().postDelayed({

            if(!checkPermissions())
                requestPermissions()
            else
            {
                launch()
            }

            card.visibility = View.VISIBLE

        val car_num = findViewById<Button>(R.id.car_num_button)
        val em_num = findViewById<Button>(R.id.eme_num_button)
        car_text = findViewById(R.id.car_num)
        em_text = findViewById(R.id.eme_num)


        if( car_number.isNullOrEmpty()){
            showalert("Enter Car Number")
        }
        if( emer.isNullOrEmpty()){
            showalert("Enter Emergency contact number")
        }

        setcarnum(car_number.toString())
        seteme(emer.toString())

        car_num.setOnClickListener {
            showalert("Enter Car Number")
        }

        em_num.setOnClickListener {
            showalert("Enter Emergency contact number")
        }

        },1000)


    }

    private fun launch() {
        val i = Intent(this,locationService::class.java)
        startService(i)

    }


    fun setcarnum(str:String){
        car_text.text ="Your car number is "+str
        DATA.car_num= str
    }

    fun seteme(str: String){
        DATA.eme_num= str
        em_text.text = "Your emergency contact number is "+str
    }






    fun showalert(title:String){
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val  edittext = EditText(applicationContext);
        alert.setMessage("");
        alert.setTitle(title);

        alert.setView(edittext);

        alert.setPositiveButton("Save", object: DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {

                val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val myEdit = sharedPreferences.edit()

                if(title.contains("Car")) {
                    car_number = edittext.text.toString()

                    myEdit.putString("car", edittext.text.toString())

                }
                else{
                    emer = edittext.text.toString()
                    myEdit.putString("em", edittext.text.toString())

                }

                myEdit.apply()
                setcarnum(car_number.toString())
                seteme(emer.toString())


            }

        });

        alert.setNegativeButton("cancel", object :DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }

        });

        alert.show();

    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )
    }

//    private fun isLocationEnabled(): Boolean {
//        val locationManager = getSystemService<Any>( con) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launch()
            }
        }
    }


}