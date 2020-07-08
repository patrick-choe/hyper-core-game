/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.hypercore.listener

import com.github.noonmaru.tap.packet.Packet
import com.github.patrick.hypercore.Hyper
import com.github.patrick.hypercore.block.HyperTree
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.log
import kotlin.random.Random

class HyperListener : Listener {
    @EventHandler
    fun onCreeperTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        Hyper.HYPER_CREEPERS[event.entity.entityId]?.let {
            event.isCancelled = true
            if (it.explosionStart == -1) it.explosionStart = it.entity.ticksLived
        }
    }

    @EventHandler
    fun onHyperPlayerDeath(event: PlayerDeathEvent) {
        with(Hyper) {
            when (val player = event.entity) {
                HYPER_BORDER_PLAYER -> {
                    HYPER_BORDER_PLAYER = null
                    HYPER_BORDER_TASK = null
                    player.world.worldBorder.run {
                        setCenter(0.0, 0.0)
                        size = 60000000.0
                    }
                }
                HYPER_BLOCK_PLAYER -> {
                    HYPER_TREE_TASKS.forEach {
                        val chance = log(it.tapArmorStands.count().toDouble(), 64.0) * 64 / it.tapArmorStands.count()
                        it.tapArmorStands.forEach { entry ->
                            val stand = entry.key
                            stand.run {
                                if (Random.nextDouble() < chance) {
                                    bukkitWorld.dropItemNaturally(Location(bukkitWorld, posX, posY, posZ), stand.getEquipment(EquipmentSlot.HEAD).toItemStack())
                                }
                            }
                            Packet.ENTITY.destroy(stand.id).sendAll()
                        }
                    }
                    HYPER_TREE_BUKKIT_TASKS.run {
                        forEach {
                            it.cancel()
                        }
                        clear()
                    }
                }
            }
        }
    }

    @EventHandler
    fun onTreeBreak(event: BlockBreakEvent) {
        event.block?.run {
            if (Hyper.TREE_MATERIAL.contains(type)) {
                event.isCancelled = true
                HyperTree(this)
            }
        }
    }
}