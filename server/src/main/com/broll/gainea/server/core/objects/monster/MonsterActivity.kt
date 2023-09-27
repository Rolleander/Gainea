package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.misc.RandomUtils

enum class MonsterActivity(private val min: Int, private val max: Int) {
    ALWAYS(1, 1),
    OFTEN(1, 2),
    SOMETIMES(2, 4),
    RARELY(4, 6);

    val turnTimer: Int
        get() = RandomUtils.random(min, max)
}
