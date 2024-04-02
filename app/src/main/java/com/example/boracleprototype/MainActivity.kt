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
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue


class MainActivity : AppCompatActivity() {

    companion object {
        // Define TAG constant for logging
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore // Instance of Firestore
    private lateinit var auth: FirebaseAuth // Firebase Auth instance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "User is signed in with uid: ${user.uid}")
                binding.authButton.text = "Authenticated as ${user.email}"
                binding.authButton.setOnClickListener {
                    signOut()
                }
            } else {
                Log.d(TAG, "No user is signed in.")
                binding.authButton.text = "Authenticate"
                configureGoogleSignIn()
            }
        }

        configureGoogleSignIn()

        binding.syncButton.setOnClickListener {
            Toast.makeText(this, "Sync in progress...", Toast.LENGTH_SHORT).show()
            writeDummyData()

        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
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
            val email = user.email // Directly access the email from FirebaseUser
            if (email != null) { // Check if email is not null
                val dummyData = hashMapOf(
                    "Username" to email,
                    "Sharing Enabled" to false,
                    "Steps" to 123,
                    "Timestamp" to FieldValue.serverTimestamp()
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
                Toast.makeText(this, "Email is not available.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_LONG).show()
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_idx))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.authButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun signOut() {
        // Sign out from Firebase
        auth.signOut()

        // If using Google Sign-In, also sign out from Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_idx))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Update the UI after sign out (reset button text, etc.)
            binding.authButton.text = "Authenticate"
            configureGoogleSignIn() // Reconfigure sign-in if needed
            Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Update UI or proceed with user information
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


}