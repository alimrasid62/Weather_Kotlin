package com.alimrasid.cuaca_1.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alimrasid.cuaca_1.R
import com.alimrasid.cuaca_1.api.CustomInfoWindow
import com.alimrasid.cuaca_1.api.ModelMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets

class SelectLocationActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var currentMarker: Marker? = null
    private lateinit var searchInput: EditText
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_select_location)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        searchInput = findViewById(R.id.search_input)

        val startPoint = GeoPoint(-6.200000, 106.816666)
        val mapController = map.controller
        mapController.setZoom(12.0)
        mapController.setCenter(startPoint)

        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Jakarta"
        map.overlays.add(marker)

        searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchInput.text.toString()
                if (query.isNotBlank()) {
                    searchLocation(query)
                }

                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchInput.windowToken, 0)

                true
            } else {
                false
            }
        }

    }

    private fun searchLocation(query: String) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=json&limit=1"
                    URL(url).readText()
                }

                val jsonArray = JSONObject("{\"results\":$response}").getJSONArray("results")
                if (jsonArray.length() > 0) {
                    val obj = jsonArray.getJSONObject(0)
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")
                    val geoPoint = GeoPoint(lat, lon)

                    map.controller.setZoom(15.0)
                    map.controller.setCenter(geoPoint)
                    map.controller.animateTo(geoPoint)

                    currentMarker?.let {
                        map.overlays.remove(it)
                    }

                    val marker = Marker(map)
                    marker.position = geoPoint
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = query
                    map.overlays.clear()
                    map.overlays.add(marker)
                    map.invalidate()

                    val resultIntent = Intent()
                    resultIntent.putExtra("selected_location", query)
                    setResult(RESULT_OK, resultIntent)
                    finish()

                    map.invalidate()


                } else {
                    Toast.makeText(this@SelectLocationActivity, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SelectLocationActivity, "Gagal mencari lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}