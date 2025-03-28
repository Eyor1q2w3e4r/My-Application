package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class TermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val termsTextView = findViewById<TextView>(R.id.termsTextView)
        val scrollView = findViewById<ScrollView>(R.id.termsScrollView)
        val acceptButton = findViewById<Button>(R.id.acceptButton)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val continueButton = findViewById<Button>(R.id.continueButton)

        acceptButton.visibility = Button.GONE
        continueButton.isEnabled = false

        val inputStream = resources.openRawResource(R.raw.terms)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val termsText = reader.readText()
        reader.close()
        termsTextView.text = termsText

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (!scrollView.canScrollVertically(1)) {
                acceptButton.visibility = Button.VISIBLE
            }
        }

        acceptButton.setOnClickListener {
            checkBox.isEnabled = true
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            continueButton.isEnabled = isChecked
        }

        continueButton.setOnClickListener {
            if (checkBox.isChecked) {
                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                sharedPref.edit().putBoolean("AcceptedTerms", true).apply()
                Log.d("TermsActivity", "Foydalanuvchi shartlarni qabul qildi!")
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Foydalanish shartlarini qabul qiling!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}