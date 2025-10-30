package com.example.merjumaterjalid

import android.view.View
import android.widget.AdapterView

class SimpleItemSelectedListener(
    private val onItemSelected: (String) -> Unit
) : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val value = parent.getItemAtPosition(position).toString()
        onItemSelected(value)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
