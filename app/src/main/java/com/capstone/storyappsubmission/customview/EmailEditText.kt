package com.capstone.storyappsubmission.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        doOnTextChanged { text, _, _, _ ->
            if (text != null && text.isNotEmpty()) {
                validateEmail(text.toString())
            }
        }
    }

    private fun validateEmail(email: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setBackgroundResource(android.R.color.holo_green_light)
        } else {
            setBackgroundResource(android.R.color.holo_red_light)
            setError("Email tidak valid", null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan Email Anda"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}