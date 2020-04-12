package com.github.patrick.hypercore.listener

import com.github.patrick.hypercore.Hyper.hyperCreepers
import com.github.patrick.hypercore.Hyper.hyperPlayer
import com.github.patrick.hypercore.Hyper.hyperSkeletons
import com.github.patrick.hypercore.Hyper.hyperTask
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent

class HyperListener : Listener {
    @EventHandler
    fun onTarget(event: EntityTargetLivingEntityEvent) {
        hyperCreepers[event.entity.entityId]?.let {
            event.isCancelled = true
            if (it.explosionStart == -1) it.explosionStart = it.entity.ticksLived
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (player == hyperPlayer) {
            hyperPlayer = null
            hyperTask = null
            val border = player.world.worldBorder
            border.setCenter(0.0, 0.0)
            border.size = 60000000.0
        }
    }
}