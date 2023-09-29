package com.broll.gainea.server.core.map

class Coordinates(var x: Float, var y: Float) : Comparable<Coordinates> {
    var displayX = 0f
        private set
    var displayY = 0f
        private set
    private var sx = 0f
    private var sy = 0f
    fun shift(x: Float, y: Float) {
        sx += x
        sy += y
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun calcDisplayLocation(size: Float) {
        displayX = getX() * size
        displayY = getY() * size
    }

    fun setSx(sx: Float) {
        this.sx = sx
    }

    fun setSy(sy: Float) {
        this.sy = sy
    }

    fun getX(): Float {
        return sx + x
    }

    fun getY(): Float {
        return sy + y
    }

    fun mirrorY(mirrorY: Float) {
        y = mirrorY - y
    }

    override fun compareTo(o: Coordinates): Int {
        val compare = y.compareTo(o.y)
        return if (compare != 0) {
            compare
        } else x.compareTo(o.x)
    }
}
