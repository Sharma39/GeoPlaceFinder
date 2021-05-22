package com.example.geoloacationfinder.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import com.example.geoloacationfinder.model.Result
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.geoloacationfinder.MyLocationListener
import com.example.geoloacationfinder.R
import com.example.geoloacationfinder.util.State
import com.example.geoloacationfinder.viewmodel.PlacesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private val viewModel: PlacesViewModel by viewModels()
    private val placeAdapter = PlaceAdapter(listOf())

    private lateinit var loc: Location
    private lateinit var typ: String


    val placeList: MutableList<String> = mutableListOf(
        "restaurant", "gym", "bar", "school",
        "store", "parking", "museum", "bank", "pharmacy"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Shared preference for first time opening the app
//        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
//        val firstStart = prefs.getBoolean("firstStart", true)
//        if (firstStart) {
//            showAlertDialog()
//        }

        //TextView Animation
        startAnimation()

        viewModel.liveData.observe(this, { list ->
            if(list.isNotEmpty()){
                Log.d("TAG_I", list[0].toString())
            }
            else
                Log.d("TAG_I", "Null list")
            placeAdapter.updatePlaces(list)
        })

        main_recy_view.adapter = placeAdapter
        val sHelper = LinearSnapHelper().also {
            it.attachToRecyclerView(main_recy_view)
        }


        viewModel.statusData.observe(this, {
            when (it) {
                State.LOADING -> status_progressbar.visibility = View.VISIBLE
                State.ERROR -> displayError()
                else -> status_progressbar.visibility = View.GONE
            }
        })

//        val spin = spinner
        if (spinner != null) {
            val adapter =
                ArrayAdapter(this, R.layout.spinner_item_layout, R.id.item_text, placeList)

            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                @SuppressLint("MissingPermission")
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    showMessage(placeList[position])
//                    if(checker()) {
//                        Log.d("TAG_N", loc.toString())
                        typ = placeList[position]
                        makeApiCall()
//                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    showMessage("Nothing Selected")
                }

//        with(spinner)
//        {
//            adapter = padapter
//            setSelection(0, false)
//            onItemSelectedListener = this@MainActivity
//            prompt = "Select your favourite language"
//            gravity = Gravity.CENTER
//
            }
        }
    }

    // First Time Alert Dialog
//    private fun showAlertDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Welcome Place Finder")
//            .setMessage("Press ok to continue")
//            .setPositiveButton(
//                "ok"
//            ) { dialog, which -> dialog.dismiss() }
//            .create().show()
//
//        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
//        val editor = prefs.edit()
//        editor.putBoolean("firstStart", false)
//        editor.apply()
//    }

//    TextView Animation
    private fun startAnimation() {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        loc_textview.startAnimation(animation)
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // If no internet then display error
    private fun displayError() {
        status_progressbar.visibility = View.GONE
        Snackbar.make(view_main, "An error occurred!", Snackbar.LENGTH_SHORT).show()
    }

    //On quitting the app display
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure!")
        builder.setMessage("You want to close the app?")
        builder.setPositiveButton("yes") { diallogInterface: DialogInterface, i: Int ->
            finish()
        }
        builder.setNegativeButton("No") { diallogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        //Request permission at start
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 700
            )
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000L,
            5f,
            myLocationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 700) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        700
                    )
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

// When ever the location is getting updated from onStart it will call here with updated location
        private val myLocationListener = MyLocationListener(
            object : MyLocationListener.LocationDelegate {
                override fun provideLocation(location: Location) {
                    try {
                        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                        val addresses: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address: String = addresses[0].getAddressLine(0)
                        loc_textview.text = address
                        loc = location
                        makeApiCall()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//
                }

            }
        )

    private fun checker(): Boolean = this::loc.isInitialized && this::typ.isInitialized

    private fun makeApiCall() {
        if(checker()) {
            Log.d("TAG_1", "sending ${loc.toString()}, $typ")
            viewModel.getPlacesNearMe(loc, typ)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(myLocationListener)
    }


}