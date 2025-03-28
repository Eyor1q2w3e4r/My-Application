package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var securityManager: SecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        securityManager = SecurityManager(this)
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        // Agar ilova bloklangan bo‘lsa
        if (securityManager.isBlocked()) {
            Toast.makeText(this, "Ilova bloklangan! Keyinroq urinib ko‘ring.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Agar shartlar qabul qilinmagan bo‘lsa
        val acceptedTerms = sharedPref.getBoolean("AcceptedTerms", false)
        if (!acceptedTerms) {
            startActivity(Intent(this, TermsActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val signInButton: Button = findViewById(R.id.signInButton)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInButton.setOnClickListener {
            Log.d("GoogleSignIn", "Sign-in bosildi!")
            signIn()
        }

        buttonLogin.setOnClickListener {
            val password = editTextPassword.text.toString()
            if (securityManager.isPasswordSet()) {
                if (securityManager.checkPassword(password)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    securityManager.incrementFailedAttempts()
                    Toast.makeText(this, "Parol noto‘g‘ri! Qoldi: ${3 - sharedPref.getInt("FailedAttempts", 0)}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Parol o‘rnatilmagan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 1001,
                        null, 0, 0, 0, null
                    )
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Sign-in xatosi: ${e.message}")
                    Toast.makeText(this, "Xatolik yuz berdi", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("GoogleSignIn", "Sign-in muvaffaqiyatsiz: ${e.message}")
                Toast.makeText(this, "Kirish amalga oshmadi", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("GoogleSignIn", "Google bilan kirish muvaffaqiyatli!")
                                val email = firebaseAuth.currentUser?.email
                                val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                if (sharedPref.getBoolean("Blocked_$email", false)) {
                                    Toast.makeText(this, "Siz ushbu akkaunt bilan kira olmaysiz!", Toast.LENGTH_LONG).show()
                                    FirebaseAuth.getInstance().signOut()
                                    return@addOnCompleteListener
                                }
                                val deviceList = sharedPref.getStringSet("ActiveDevices_$email", mutableSetOf()) ?: mutableSetOf()
                                if (deviceList.size >= 2) {
                                    deviceList.remove(deviceList.first())
                                }
                                deviceList.add(android.os.Build.MODEL)
                                sharedPref.edit().putStringSet("ActiveDevices_$email", deviceList).apply()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Log.e("GoogleSignIn", "Firebase autentifikatsiya xatosi: ${task.exception?.message}")
                                Toast.makeText(this, "Autentifikatsiya xatosi", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in xatosi: ${e.message}")
                Toast.makeText(this, "Google kirishida xatolik", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
