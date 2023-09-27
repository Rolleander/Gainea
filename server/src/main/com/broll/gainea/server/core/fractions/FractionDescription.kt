package com.broll.gainea.server.core.fractions

class FractionDescription(@JvmField val general: String) {
    private val plus: MutableList<String> = ArrayList()
    private val contra: MutableList<String> = ArrayList()
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
