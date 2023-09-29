package com.broll.gainea.server.core.fractions

class FractionDescription(val general: String) {
    private val plus = mutableListOf<String>()
    private val contra = mutableListOf<String>()
    operator fun plus(strength: String) {
        plus.add(strength)
    }

    fun contra(weakness: String) {
        contra.add(weakness)
    }

    fun getContra(): List<String> {
        return contra
    }

    fun getPlus(): List<String> {
        return plus
    }
}
