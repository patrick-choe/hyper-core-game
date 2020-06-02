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

import com.github.noonmaru.customentity.CustomEntityPacket.register
import com.github.noonmaru.customentity.CustomEntityPacket.scale
import com.github.patrick.hypercore.Hyper.hyperZombies
import com.github.patrick.hypercore.entity.HyperZombie
import net.minecraft.server.v1_12_R1.AxisAlignedBB
import net.minecraft.server.v1_12_R1.EntityHuman
import net.minecraft.server.v1_12_R1.EntityZombie
import net.minecraft.server.v1_12_R1.GenericAttributes
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStrollLand
import net.minecraft.server.v1_12_R1.PathfinderGoalZombieAttack
import net.minecraft.server.v1_12_R1.World
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM

class NMSHyperZombie(world: World) : EntityZombie(world), HyperZombie {
    init {
        getWorld().addEntity(this, CUSTOM)
        hyperZombies[id] = this
        register(id).sendAll()
    }
    override val entity = bukkitEntity as LivingEntity

    override fun update() {
        val scale = health / 100
        scale(id, scale, scale, scale, 1).sendAll()
        a(AxisAlignedBB(locX - 0.3 * scale, locY, locZ - 0.3 * scale, locX + 0.3 * scale, locY + 1.9 * scale, locZ + 0.3 * scale))
    }

    override fun initAttributes() {
        super.initAttributes()
        getAttributeInstance(GenericAttributes.maxHealth).value = 100.0
        health = 100F
    }

    override fun r() {
        goalSelector.a(1, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 20F))
        goalSelector.a(2, PathfinderGoalZombieAttack(this, 1.0, false))
        goalSelector.a(3, PathfinderGoalFloat(this))
        goalSelector.a(3, PathfinderGoalRandomStrollLand(this, 1.0))
        goalSelector.a(3, PathfinderGoalRandomLookaround(this))
        targetSelector.a(1, PathfinderGoalHurtByTarget(this, false, *arrayOfNulls(0)))
        targetSelector.a(1, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }
}