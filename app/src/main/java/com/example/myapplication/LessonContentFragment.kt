package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.PDFView
import java.io.IOException


class LessonContentFragment : Fragment() {

    companion object {
        private const val ARG_LESSON = "lesson"

        // Fragmentni yaratish uchun factory method
        fun newInstance(lesson: String): LessonContentFragment {
            val fragment = LessonContentFragment()
            val args = Bundle()
            args.putString(ARG_LESSON, lesson)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var pdfView: PDFView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment layoutini yuklash
        val view = inflater.inflate(R.layout.fragment_lesson_content, container, false)

        // Tanlangan mavzuni olish
        val lesson = arguments?.getString(ARG_LESSON) ?: "Mavzu 1"

        // PDFView ni topish
        pdfView = view.findViewById(R.id.pdfView)

        // Matnli ma'lumotlar ikonasini topish
        val iconTextInfo: ImageView = view.findViewById(R.id.iconTextInfo)
        // Ko'rgazmali materiallar ikonasini topish
        val iconVisualMaterials: ImageView = view.findViewById(R.id.iconVisualMaterials)

        // Matnli ma'lumotlar ikonasiga click listener qo'shish
        iconTextInfo.setOnClickListener {
            val pdfResId = getTextInfoResourceId(lesson)
            if (pdfResId != -1) {
                loadPdfFromRaw(pdfResId)
                pdfView.visibility = View.VISIBLE
            }
        }

        // Ko'rgazmali materiallar ikonasiga click listener qo'shish
        iconVisualMaterials.setOnClickListener {
            val pdfResId = getVisualMaterialsResourceId(lesson)
            if (pdfResId != -1) {
                loadPdfFromRaw(pdfResId)
                pdfView.visibility = View.VISIBLE
            }
        }

        // Hozircha boshqa ikonalar faol emas
        return view
    }

    // Matnli ma'lumotlar uchun resurs ID ni aniqlash
    private fun getTextInfoResourceId(lesson: String): Int {
        return when (lesson) {
            "Mavzu 1" -> R.raw.sample_mavzu1
            "Mavzu 2" -> R.raw.sample_mavzu2
            else -> -1
        }
    }

    // Ko'rgazmali materiallar uchun resurs ID ni aniqlash
    private fun getVisualMaterialsResourceId(lesson: String): Int {
        return when (lesson) {
            "Mavzu 1" -> R.raw.visual_materials_mavzu1
            "Mavzu 2" -> R.raw.visual_materials_mavzu2
            else -> -1
        }
    }

    // PDF faylni res/raw dan yuklash funksiyasi
    private fun loadPdfFromRaw(pdfResId: Int) {
        try {
            // res/raw dan PDF faylni o'qish
            pdfView.fromStream(requireContext().resources.openRawResource(pdfResId))
                .defaultPage(0) // Birinchi sahifadan boshlash
                .enableSwipe(true) // Swipe qilish imkoniyati
                .swipeHorizontal(false) // Vertikal skroll
                .enableDoubletap(true) // Double tap bilan zoom qilish
                .load()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "PDF faylni yuklashda xatolik: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}