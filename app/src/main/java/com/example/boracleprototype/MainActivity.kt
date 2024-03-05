package com.example.boracleprototype

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.boracleprototype.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore // Instance of Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // We need to init Firestore
        firestore = FirebaseFirestore.getInstance()


        // Find the button by its ID
        val syncButton: Button = findViewById(R.id.syncButton)

        // Set an onClickListener for the button
        syncButton.setOnClickListener {
            // TODO: Add the logic to perform the sync operation here
            writeDummyData() // Function to write out dummy data in case I can't figure out mock data

            // just showing basic message for now
            Toast.makeText(this, "Sync in progress...", Toast.LENGTH_SHORT).show()
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun writeDummyData() {
        val dummyData = hashMapOf(
            "field1" to "value1",
            "field2" to "value2",
            "field3" to 123
        )

        // Add a new document with a generated ID to a collection called 'testCollection'
        firestore.collection("testCollection")
            .add(dummyData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Dummy data added with ID: ${documentReference.id}", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding dummy data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}