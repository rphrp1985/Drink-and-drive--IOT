package com.prianshu.safedriving

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class MainActivity3 : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var pb: ProgressBar
    lateinit var card :CardView
    lateinit var text :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(com.google.android.material.R.style.Theme_AppCompat)
        setContentView(R.layout.activity_main2)
        supportActionBar?.hide()

        pb = findViewById<ProgressBar>(R.id.pgbar)
        card = findViewById(R.id.card)

        card.visibility= View.GONE
        pb.visibility = View.VISIBLE
        text = findViewById(R.id.text)
        text.text= " "



        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.onItemSelectedListener = this


        val requestQueue = Volley.newRequestQueue(this)

        // Create a StringRequest for the GET request

        // Create a StringRequest for the GET request
        val stringRequest = StringRequest(
            Request.Method.GET, "https://rphrp1985-iot.hf.space/getdata",
            {

                card.visibility= View.VISIBLE
                pb.visibility = View.GONE

             val list= it.toString().split('\\')


                val items: MutableList<String> = ArrayList()

                for( x in list)
                    items.add(x.substring(1))

                // Create an ArrayAdapter using the string array and a default spinner layout

                // Create an ArrayAdapter using the string array and a default spinner layout
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

                // Specify the layout to use when the list of choices appears

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Apply the adapter to the spinner

                // Apply the adapter to the spinner
                spinner.adapter = adapter


            }
        ) { error -> // Handle the error
            finish()
          Toast.makeText(this,"Some error occured  $error",Toast.LENGTH_SHORT).show()
        }

        // Add the request to the request queue

        // Add the request to the request queue
        requestQueue.add(stringRequest)






        // Create a list of items for the Spinner

        // Create a list of items for the Spinner











    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        val selectedItem: String = p0?.getItemAtPosition(p2).toString()


        val requestQueue = Volley.newRequestQueue(this)

        // Create a StringRequest for the GET request

        // Create a StringRequest for the GET request
        val stringRequest = StringRequest(
            Request.Method.GET, "https://rphrp1985-iot.hf.space/predict?address=\"$selectedItem\"",
            {
               text.text = "$it   Hours"

            }
        ) { error -> // Handle the error

            Toast.makeText(this,"Some error occured  $error",Toast.LENGTH_SHORT).show()
        }

        // Add the request to the request queue

        // Add the request to the request queue
        requestQueue.add(stringRequest)




    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}