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

import net.minecraft.server.v1_12_R1.EnumHand
import net.minecraft.server.v1_12_R1.IRangedEntity
import net.minecraft.server.v1_12_R1.ItemBow
import net.minecraft.server.v1_12_R1.Items
import net.minecraft.server.v1_12_R1.PathfinderGoal

class NMSHyperSkeletonGoal(private val entity: NMSHyperSkeleton, private val b: Double, var c: Int, var5: Float) : PathfinderGoal() {
    private val d: Float = var5 * var5
    private var e = -1
    private var f = 0
    private var g = false
    private var h = false
    private var i = -1

    override fun a() = if (entity.goalTarget == null) false else hasBowOnMainHand()

    private fun hasBowOnMainHand() = !entity.itemInMainHand.isEmpty && entity.itemInMainHand.item === Items.BOW

    override fun b() = (a() || !entity.navigation.o()) && hasBowOnMainHand()

    override fun c() {
        super.c()
        (entity as IRangedEntity).p(true)
    }

    override fun d() {
        super.d()
        (entity as IRangedEntity).p(false)
        f = 0
        e = -1
        entity.cN()
    }

    override fun e() {
        val var1 = entity.goalTarget
        if (var1 != null) {
            val var2 = entity.d(var1.locX, var1.boundingBox.b, var1.locZ)
            val var4 = entity.entitySenses.a(var1)
            val var5 = f > 0
            if (var4 != var5) f = 0
            if (var4) ++f else --f
            if (var2 <= d.toDouble() && f >= 2) {
                entity.navigation.p()
                ++i
            } else {
                entity.navigation.a(var1, b)
                i = -1
            }
            if (i >= 20) {
                if (entity.random.nextDouble() < 0.3) g = !g
                if (entity.random.nextDouble() < 0.3) h = !h
                i = 0
            }
            if (i > -1) {
                if (var2 > d * 0.75) h = false else if (var2 < d * 0.25) h = true
                entity.controllerMove.a(if (h) -0.5f else 0.5f, if (g) 0.5f else -0.5f)
                entity.a(var1, 30.0f, 30.0f)
            } else entity.controllerLook.a(var1, 30.0f, 30.0f)
            if (entity.isHandRaised) {
                if (!var4 && f < - 2) entity.cN() else if (var4 && entity.cL() >= 20) {
                    entity.cN()
                    (this.entity as IRangedEntity).a(var1, ItemBow.b(entity.cL()))
                    e = c
                }
            } else if (--e <= 0 && f >= -2) entity.c(EnumHand.MAIN_HAND)
        }
    }

    init {
        a(3)
    }
}