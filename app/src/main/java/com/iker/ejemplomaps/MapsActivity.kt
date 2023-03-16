package com.iker.ejemplomaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.LocationServices


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.google.android.gms.maps.model.MarkerOptions
import com.iker.ejemplomaps.databinding.ActivityMapsBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var checkButton: Button
    var mark1 = getMarkers()
    var mark2 = getMarkers()
    var mark3 = getMarkers()
    var markprueba = MarkerOptions().position(LatLng(42.23713161065555, -8.714419705122266))
    var mark11: Marker? = null
    var mark21: Marker? = null
    var mark31: Marker? = null
    var markprueba1: Marker? = null
    private val permissionCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@MapsActivity)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //val mapFragment = supportFragmentManager
        //    .findFragmentById(R.id.map) as SupportMapFragment
        //mapFragment.getMapAsync(this)
        fetchLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = MAP_TYPE_SATELLITE
        mMap.setOnMyLocationChangeListener(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }
        mMap.isMyLocationEnabled = true

        val circleOptions = CircleOptions()
            .center(LatLng(currentLocation.latitude, currentLocation.longitude))
            .radius(100.0)
            .strokeColor(Color.BLUE)
            .fillColor(Color.argb(70, 0, 0, 255))
        mMap.addCircle(circleOptions)



        mark11 = mMap.addMarker(mark1)
        mark21 = mMap.addMarker(mark2)
        mark31 = mMap.addMarker(mark3)
        markprueba1 = mMap.addMarker(markprueba)

        mark11?.isVisible = false
        mark21?.isVisible = false
        mark31?.isVisible = false
        markprueba1?.isVisible=false
    }


    /*override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = MAP_TYPE_SATELLITE
        mMap.setOnMyLocationChangeListener { this }
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
            return
        }
        mMap.isMyLocationEnabled = true

        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.addMarker(markerOptions)



    }*/


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    applicationContext, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT
                ).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@MapsActivity)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                //Location request every 2 min
                fetchLocation()
            }
        }
    }

    override fun onMyLocationChange(location: Location) {
        val userLocation = LatLng(location.latitude, location.longitude)

        // Calcula la distancia entre la ubicación del usuario y cada marca
        val distanceToMark1 = calcularDistancia(userLocation, mark1.position)
        val distanceToMark2 = calcularDistancia(userLocation, mark2.position)
        val distanceToMark3 = calcularDistancia(userLocation, mark3.position)
        val distanceToMark4 = calcularDistancia(userLocation, markprueba.position)

        // Si la distancia es menor que 50 metros, muestra la marca en el mapa
        val threshold = 100 // metros
        if (distanceToMark1 < threshold) {
            mark11?.isVisible = true
        }
        if (distanceToMark2 < threshold) {
            mark21?.isVisible = true
        }
        if (distanceToMark3 < threshold) {
            mark31?.isVisible = true
        }
        if (distanceToMark4 < threshold) {
            markprueba1?.isVisible = true
        }
    }

    private fun calcularDistancia(location1: LatLng, location2: LatLng): Float {

        val result = FloatArray(1)
        Location.distanceBetween(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude,
            result
        )
        return result[0]
    }


    private fun getMarkers(): MarkerOptions {
        val rotonda = LatLng(42.2378770371617, -8.71431986102628)
        val circuloEmpresarios = LatLng(42.23678569531483, -8.712046508639517)
        val abanca = LatLng(42.237919578082845, -8.720148733828857)
        val lavanderia = LatLng(42.23787191885741, -8.716903260712563)
        val gadis = LatLng(42.23765745193615, -8.713625601555714)

        val arrayLugares = arrayOf(rotonda, circuloEmpresarios, abanca, lavanderia, gadis)

        var random = Math.floor(Math.random() * arrayLugares.size)

        val markeroptions = MarkerOptions().position(arrayLugares.get(random.toInt())).title("OLE")
        return markeroptions

    }

}