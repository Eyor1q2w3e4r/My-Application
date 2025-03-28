package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val startLessonButton: Button = view.findViewById(R.id.btnStartLesson)
        val settingsButton: Button = view.findViewById(R.id.btnSettings)
        val logoutButton: Button = view.findViewById(R.id.logoutButton)

        startLessonButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LessonsFragment())
                .addToBackStack(null)
                .commit()
        }

        settingsButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logoutUser()
        }

        return view
    }
}