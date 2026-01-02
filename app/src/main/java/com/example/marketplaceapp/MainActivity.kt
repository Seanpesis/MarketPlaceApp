package com.example.marketplaceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.marketplaceapp.viewmodel.MarketViewModel
import com.google.android.gms.location.LocationServices
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.example.marketplaceapp.data.CartManager



class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val viewModel: MarketViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Marketplace" // Set the main title

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.subtitle = destination.label // Update the subtitle
        }

        setupActionBarWithNavController(navController)

        checkLocationPermission()


        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            supportActionBar?.title = "Marketplace"


            supportActionBar?.subtitle = destination.label
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.setCurrentLocation(location)
                    }
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Failed to get location due to security issue.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // טעינת התפריט

        val cartItem = menu.findItem(R.id.action_cart)
        val actionView = cartItem.actionView // זה ה-FrameLayout מהקובץ menu_item_cart.xml
        val badgeTextView = actionView?.findViewById<TextView>(R.id.cart_badge)

        // האזנה לשינויים בכמות הפריטים מה-CartManager
        CartManager.totalItemsCount.observe(this) { count ->
            if (count > 0) {
                badgeTextView?.text = count.toString()
                badgeTextView?.visibility = View.VISIBLE
            } else {
                badgeTextView?.visibility = View.GONE
            }
        }

        // הוספת לחיצה על האייקון (כי ActionLayout מבטל את הלחיצה הרגילה)
        actionView?.setOnClickListener {
            navController.navigate(R.id.cartFragment) // נווט לעגלה
        }

        return true
    }
}