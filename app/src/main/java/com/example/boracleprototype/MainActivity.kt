package com.example.boracleprototype

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.boracleprototype.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object {
        // Define TAG constant for logging
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore // Instance of Firestore
    private lateinit var auth: FirebaseAuth // Firebase Auth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "User is signed in with uid: ${user.uid}")
            } else {
                Log.d(TAG, "No user is signed in.")
            }
        }

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

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInAnonymously:success")
                    writeDummyData()
                } else {
                    // If sign in fails, log the error message
                    task.exception?.let {
                        Log.w(TAG, "signInAnonymously:failure", it)
                        Toast.makeText(baseContext, "Authentication failed: ${it.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun writeDummyData() {
        val user = auth.currentUser
        if (user != null) {
            val dummyData = hashMapOf(
                "field1" to "value1",
                "field2" to "value2",
                "field3" to 123
            )

            firestore.collection("testCollection")
                .add(dummyData)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Document added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Successful Write.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Write failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_LONG).show()
        }
    }


}