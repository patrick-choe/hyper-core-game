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

package com.github.patrick.hypercore.v1_12_R1.entity

import com.github.noonmaru.customentity.CustomEntityPacket.colorAndScale
import com.github.noonmaru.customentity.CustomEntityPacket.register
import com.github.patrick.hypercore.Hyper.hyperCreepers
import com.github.patrick.hypercore.entity.HyperCreeper
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import net.minecraft.server.v1_12_R1.EntityCreeper
import net.minecraft.server.v1_12_R1.GenericAttributes
import net.minecraft.server.v1_12_R1.World
import org.bukkit.entity.Creeper
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM

class NMSHyperCreeper(world: World) : EntityCreeper(world), HyperCreeper {
    override var explosionStart = -1

    init {
        getWorld().addEntity(this, CUSTOM)
        hyperCreepers[id] = this
        register(id).sendAll()
        forceExplosionKnockback = true
        (bukkitEntity as Creeper).let {
            it.isPowered = true
            it.explosionRadius = 512
            it.maxFuseTicks = 20
        }
    }

    override val entity = (bukkitEntity as LivingEntity)

    override fun update() {
        if (explosionStart == -1) return

        val ticks = entity.ticksLived - explosionStart
        if (ticks < 200) {
            val scale = ticks / 4F
            val color = 255 - (ticks * 255 / 200)
            colorAndScale(entity.entityId, 255, color, color, scale, scale, scale, 1).sendAll()
            a(AxisAlignedBB(locX - 0.3 * scale, locY, locZ - 0.3 * scale, locX + 0.3 * scale, locY + 1.7 * scale, locZ + 0.3 * scale))
        }
        if (ticks == 200) do_()
    }

    override fun initAttributes() {
        super.initAttributes()
        getAttributeInstance(GenericAttributes.maxHealth).value = 256.0
        health = 256F
    }
}