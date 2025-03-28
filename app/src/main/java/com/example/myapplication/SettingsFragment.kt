package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var securityManager: SecurityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextAge = view.findViewById<EditText>(R.id.editTextAge)
        val spinnerGender = view.findViewById<Spinner>(R.id.spinnerGender)
        val switchNotifications = view.findViewById<Switch>(R.id.switchNotifications)
        val switchTheme = view.findViewById<Switch>(R.id.switchTheme)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val buttonSetPassword = view.findViewById<Button>(R.id.buttonSetPassword)
        val textViewLockStatus = view.findViewById<TextView>(R.id.textViewLockStatus)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        securityManager = SecurityManager(requireContext())
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        // Oldin saqlangan ma’lumotlarni ekranga chiqaramiz
        editTextName.setText(sharedPref.getString("UserName", ""))
        editTextAge.setText(sharedPref.getString("UserAge", ""))
        switchNotifications.isChecked = sharedPref.getBoolean("Notifications", false)
        switchTheme.isChecked = sharedPref.getBoolean("DarkMode", false)
        textViewLockStatus.text = "Blok holati: ${securityManager.getLockStatus()}"

        // Parol saqlash tugmasi bosilganda
        buttonSetPassword.setOnClickListener {
            val password = editTextPassword.text.toString()
            if (password.length == 4) {
                securityManager.savePassword(password)
                Toast.makeText(context, "Parol saqlandi!", Toast.LENGTH_SHORT).show()
                editTextPassword.text.clear()
            } else {
                Toast.makeText(context, "Parol 4 raqamli bo‘lishi kerak!", Toast.LENGTH_SHORT).show()
            }
        }

        // Saqlash tugmasi bosilganda
        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val age = editTextAge.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val notifications = switchNotifications.isChecked
            val darkMode = switchTheme.isChecked

            sharedPref.edit()
                .putString("UserName", name)
                .putString("UserAge", age)
                .putString("UserGender", gender)
                .putBoolean("Notifications", notifications)
                .putBoolean("DarkMode", darkMode)
                .apply()

            Toast.makeText(context, "Ma’lumotlar saqlandi!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
