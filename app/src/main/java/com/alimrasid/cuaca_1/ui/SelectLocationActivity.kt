package com.alimrasid.cuaca_1.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alimrasid.cuaca_1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class SelectLocationActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var searchField: EditText
    private lateinit var btnCurrentLocation: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchField = findViewById(R.id.et_search)
        btnCurrentLocation = findViewById(R.id.btn_current_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnCurrentLocation.setOnClickListener {
            // TODO: ambil lokasi GPS user dan pindahkan kamera
        }
    }

    fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Default Indonesia
        val indonesia = LatLng(-2.5489, 118.0149)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))
    }
}

private fun SupportMapFragment.getMapAsync(activity: SelectLocationActivity) {
    TODO("Not yet implemented")
}
