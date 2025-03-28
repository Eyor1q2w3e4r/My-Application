package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class LessonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)

        val lessonsListView: ListView = view.findViewById(R.id.lessonsListView)

        val lessons = arrayOf(
            "Mavzu 1", "Mavzu 2", "Mavzu 3", "Mavzu 4", "Mavzu 5",
            "Mavzu 6", "Mavzu 7", "Mavzu 8", "Mavzu 9", "Mavzu 10",
            "Mavzu 11", "Mavzu 12", "Mavzu 13", "Mavzu 14", "Mavzu 15"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            lessons
        )
        lessonsListView.adapter = adapter

        // ListView elementlarini bosganda LessonContentFragment ga o‘tish
        lessonsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedLesson = lessons[position]
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LessonContentFragment.newInstance(selectedLesson))
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
