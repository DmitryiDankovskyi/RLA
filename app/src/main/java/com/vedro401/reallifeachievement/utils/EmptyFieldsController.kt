package com.vedro401.reallifeachievement.utils

import android.widget.EditText
import kotlin.collections.HashMap


class EmptyFieldsController(var defaultWarningText: CharSequence) {
    private val fields = HashMap<EditText, CharSequence?>()
    fun addField(et: EditText, warningText: CharSequence? = null){
        fields.put(et, null)
    }
    fun isOkay() : Boolean {
        var isOkay = true
        fields.entries.filter { it.key.text.isEmpty() }.forEach {
            it.key.error = it.value ?: defaultWarningText
            isOkay = false
        }
        return isOkay
    }
}