package com.broll.gainea.server.core.shop

import com.broll.gainea.net.NT_Shop
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.cards.impl.play.C_PickCard
import com.broll.gainea.server.core.objects.impl.Challenger
import com.broll.gainea.server.core.objects.impl.DemonKnight
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.isCommanderAlive

class Shop(val game: Game, val owner: Player) {

    val items: List<ShopItem>
        get() = listOf(
            ShopUnit(4, Mercenary(owner, 1 to 1, 10, "Leichter Söldner")),
            ShopUnit(7, Mercenary(owner, 2 to 2, 28, "Schwerer Söldner")),
            ShopUnit(8, DemonKnight(owner)),
            ShopUnit(10, owner.fraction.createCommander(), !owner.isCommanderAlive()),
            ShopUnit(13, SoulStealer(owner)),
            ShopUnit(20, Challenger(owner)),
            MovementCard,
            PickCard
        )

    fun nt(): NT_Shop {
        val nt = NT_Shop()
        nt.items = items.map { it.nt() }.toTypedArray()
        return nt
    }

}

object MovementCard :
    ShopOther(price = 7, description = "Erhaltet eine zufällige Transportations-Karte") {
    override fun purchased(game: Game, player: Player) {
        val card = game.cardStorage.getRandomPlayableCard(MOVEMENT)
        player.cardHandler.receiveCard(card)
    }
}

object PickCard :
    ShopOther(price = 9, description = """Erhaltet eine "Arkane Bibliothek"-Karte""") {
    override fun purchased(game: Game, player: Player) {
        player.cardHandler.receiveCard(C_PickCard())
    }
}

