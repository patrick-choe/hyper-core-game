package com.github.patrick.hypercore.task

import com.github.noonmaru.tap.entity.TapArmorStand
import com.github.noonmaru.tap.packet.Packet
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class HyperTreeTask(private val player: Player, private val baseStand: TapArmorStand, val tapArmorStands: Map<TapArmorStand, Vector>) : Runnable {
    private var tick = 0

    override fun run() {
        with(Packet.ENTITY) {
            player.location.run {
                teleport(baseStand.bukkitEntity, x, y + if (++tick % 4 in 1..2) 4 else -4, z, 0F, 0F, false).sendAll()
                if (tick % 4 == 0) player.damage(1.2, player)
            }
            tapArmorStands.forEach {
                val stand = it.key
                val vec = it.value
                teleport(stand.bukkitEntity, baseStand.posX + vec.x, baseStand.posY + vec.y, baseStand.posZ + vec.z, 0F, 0F, false).sendAll()
            }
        }
    }
}