/*
 * Copyright (C) 2020 PatrickKR
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

import com.github.patrick.hypercore.Hyper.hyperSkeletons
import com.github.patrick.hypercore.entity.HyperSkeleton
import net.minecraft.server.v1_12_R1.EntityHuman
import net.minecraft.server.v1_12_R1.EntityLiving
import net.minecraft.server.v1_12_R1.EntitySkeleton
import net.minecraft.server.v1_12_R1.EntitySpectralArrow
import net.minecraft.server.v1_12_R1.EntityTippedArrow
import net.minecraft.server.v1_12_R1.EnumItemSlot.OFFHAND
import net.minecraft.server.v1_12_R1.ItemBow
import net.minecraft.server.v1_12_R1.Items.SPECTRAL_ARROW
import net.minecraft.server.v1_12_R1.Items.TIPPED_ARROW
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStrollLand
import net.minecraft.server.v1_12_R1.SoundEffects.gW
import net.minecraft.server.v1_12_R1.World
import org.bukkit.Bukkit.getLogger
import org.bukkit.Material.BOW
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory.callEntityShootBowEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM
import org.bukkit.inventory.ItemStack
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble

@Suppress("LeakingThis")
class NMSHyperSkeleton(world: World) : EntitySkeleton(world), HyperSkeleton {
    init {
        getWorld().addEntity(this, CUSTOM)
        getLogger().info("HyperSKELETON")
        hyperSkeletons.add(this)
        (bukkitEntity as LivingEntity).equipment.itemInMainHand = ItemStack(BOW)
    }

    override val entity = bukkitEntity as LivingEntity

    override fun update() = goalTarget?.run { a(this, ItemBow.b(cL())) }

    override fun a(target: EntityLiving, damage: Float) {
        val itemStack = getEquipment(OFFHAND)
        val arrow = if (itemStack.item === SPECTRAL_ARROW) {
            val entitySpectralArrow = EntitySpectralArrow(world, this)
            entitySpectralArrow.a(this, damage)
            entitySpectralArrow
        } else {
            val entityArrow = EntityTippedArrow(world, this)
            entityArrow.a(this, damage)
            if (itemStack.item === TIPPED_ARROW) entityArrow.a(itemStack)
            entityArrow
        }
        val deltaX = target.locX - locX + target.motX
        val deltaZ = target.locZ - locZ + target.motZ
        arrow.shoot(deltaX, target.boundingBox.b + (target.length / 3F) - arrow.locY + nextDouble(-0.15, 0.15) + sqrt(deltaX.pow(2) + deltaZ.pow(2)) / 12, deltaZ, 2.4F, 0F)
        val event = callEntityShootBowEvent(this, this.itemInMainHand, arrow, 1.2F)
        if (event.isCancelled) event.projectile.remove() else {
            if (event.projectile === arrow.bukkitEntity) world.addEntity(arrow)
            a(gW, 1F, 1 / (nextDouble(0.8, 1.2).toFloat()))
        }
    }

    override fun r() {
        goalSelector.a(1, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 20F))
        goalSelector.a(2, PathfinderGoalFloat(this))
        goalSelector.a(2, PathfinderGoalRandomStrollLand(this, 1.0))
        goalSelector.a(2, PathfinderGoalRandomLookaround(this))
        targetSelector.a(1, PathfinderGoalHurtByTarget(this, false, *arrayOfNulls(0)))
        targetSelector.a(1, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }
}