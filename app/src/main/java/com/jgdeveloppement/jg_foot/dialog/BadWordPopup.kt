package com.jgdeveloppement.jg_foot.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.jgdeveloppement.jg_foot.R

class BadWordPopup(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_bad_word)
        setupComponents()
    }

    private fun setupComponents() {
        val okButton = findViewById<Button>(R.id.bad_word_popup_button)

        okButton.setOnClickListener { dismiss() }
    }
}