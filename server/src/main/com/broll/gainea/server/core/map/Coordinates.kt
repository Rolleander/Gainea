package com.broll.gainea.server.core.map

class Coordinates(var rootX: Float, var rootY: Float) : Comparable<Coordinates> {
    var displayX = 0f
        private set
    var displayY = 0f
        private set
    private var sx = 0f
    private var sy = 0f

    var x: Float = 0f
        get() = sx + rootX
        private set

    var y: Float = 0f
        get() = sy + rootY
        private set

    fun shift(x: Float, y: Float) {
        sx += x
        sy += y
    }

    fun set(x: Float, y: Float) {
        this.rootX = x
        this.rootY = y
    }

    fun calcDisplayLocation(size: Float) {
        displayX = x * size
        displayY = y * size
    }

    fun setSx(sx: Float) {
        this.sx = sx
    }

    fun setSy(sy: Float) {
        this.sy = sy
    }

    fun mirrorY(mirrorY: Float) {
        rootY = mirrorY - rootY
    }

    override fun compareTo(o: Coordinates): Int {
        val compare = rootY.compareTo(o.rootY)
        return if (compare != 0) {
            compare
        } else rootX.compareTo(o.rootX)
    }
}
