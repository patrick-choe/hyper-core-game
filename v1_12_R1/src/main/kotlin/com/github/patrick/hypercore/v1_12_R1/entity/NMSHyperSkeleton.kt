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
import net.minecraft.server.v1_12_R1.Block
import net.minecraft.server.v1_12_R1.BlockPosition
import net.minecraft.server.v1_12_R1.Blocks
import net.minecraft.server.v1_12_R1.DamageSource
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.DataWatcherObject
import net.minecraft.server.v1_12_R1.DataWatcherRegistry
import net.minecraft.server.v1_12_R1.DifficultyDamageScaler
import net.minecraft.server.v1_12_R1.EntityArrow
import net.minecraft.server.v1_12_R1.EntityBoat
import net.minecraft.server.v1_12_R1.EntityCreature
import net.minecraft.server.v1_12_R1.EntityCreeper
import net.minecraft.server.v1_12_R1.EntityHuman
import net.minecraft.server.v1_12_R1.EntityLiving
import net.minecraft.server.v1_12_R1.EntityMonster
import net.minecraft.server.v1_12_R1.EntitySpectralArrow
import net.minecraft.server.v1_12_R1.EntityTippedArrow
import net.minecraft.server.v1_12_R1.EntityWolf
import net.minecraft.server.v1_12_R1.EnumDifficulty
import net.minecraft.server.v1_12_R1.EnumItemSlot
import net.minecraft.server.v1_12_R1.EnumMonsterType
import net.minecraft.server.v1_12_R1.GenericAttributes
import net.minecraft.server.v1_12_R1.GroupDataEntity
import net.minecraft.server.v1_12_R1.IRangedEntity
import net.minecraft.server.v1_12_R1.ItemBow
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.Items
import net.minecraft.server.v1_12_R1.LootTables
import net.minecraft.server.v1_12_R1.MathHelper
import net.minecraft.server.v1_12_R1.MinecraftKey
import net.minecraft.server.v1_12_R1.NBTTagCompound
import net.minecraft.server.v1_12_R1.PathfinderGoalAvoidTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalFleeSun
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalLookAtPlayer
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomLookaround
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStrollLand
import net.minecraft.server.v1_12_R1.PathfinderGoalRestrictSun
import net.minecraft.server.v1_12_R1.SoundEffect
import net.minecraft.server.v1_12_R1.SoundEffects
import net.minecraft.server.v1_12_R1.World
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory.callEntityShootBowEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM
import org.bukkit.event.entity.EntityCombustEvent
import kotlin.math.roundToInt
import org.bukkit.inventory.ItemStack as BukkitItemStack

@Suppress("LeakingThis")
class NMSHyperSkeleton(world: World) : EntityMonster(world), IRangedEntity, HyperSkeleton {
    private val b = NMSHyperSkeletonGoal(this, 1.0, 20, 15.0f)
    private val c = object : PathfinderGoalMeleeAttack(this, 1.2, false) {
        override fun d() {
            super.d()
            this@NMSHyperSkeleton.p(false)
        }

        override fun c() {
            super.c()
            this@NMSHyperSkeleton.p(true)
        }
    }

    companion object {
        private var a: DataWatcherObject<Boolean>? = null

        init {
            a = DataWatcher.a(NMSHyperSkeleton::class.java, DataWatcherRegistry.h)
        }
    }

    init {
        setSize(0.6f, 1.99f)
        dm()
        getWorld().addEntity(this, CUSTOM)
        hyperSkeletons.add(this)
        (bukkitEntity as LivingEntity).equipment.itemInMainHand = BukkitItemStack(Material.BOW)
    }

    override val entity = bukkitEntity as LivingEntity

    override fun shoot(): Unit? = goalTarget?.run { a(this, ItemBow.b(cL())) }

    override fun a(entityliving: EntityLiving, f: Float) {
        val entityArrow = a(f)
        val d0 = entityliving.locX - locX
        val d1 = entityliving.boundingBox.b + (entityliving.length / 3.0f).toDouble() - entityArrow.locY
        val d2 = entityliving.locZ - locZ
        val d3 = MathHelper.sqrt(d0 * d0 + d2 * d2).toDouble()
        entityArrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6f, (14 - world.difficulty.a() * 4).toFloat())
        val event = callEntityShootBowEvent(this, this.itemInMainHand, entityArrow, 0.8f)
        if (event.isCancelled) event.projectile.remove() else {
            if (event.projectile === entityArrow.bukkitEntity) world.addEntity(entityArrow)
            this.a(SoundEffects.gW, 1.0f, 1.0f / (getRandom().nextFloat() * 0.4f + 0.8f))
        }
    }

    override fun p(flag: Boolean) = datawatcher.set(a, flag)

    override fun r() {
        goalSelector.a(1, PathfinderGoalFloat(this))
        goalSelector.a(2, PathfinderGoalRestrictSun(this))
        goalSelector.a(3, PathfinderGoalFleeSun(this, 1.0))
        goalSelector.a(3, PathfinderGoalAvoidTarget(this, EntityWolf::class.java, 6.0f, 1.0, 1.2))
        goalSelector.a(5, PathfinderGoalRandomStrollLand(this, 1.0))
        goalSelector.a(6, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 8.0f))
        goalSelector.a(6, PathfinderGoalRandomLookaround(this))
        targetSelector.a(1, PathfinderGoalHurtByTarget(this, false, *arrayOfNulls(0)))
        targetSelector.a(2, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

    override fun initAttributes() {
        super.initAttributes()
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).value = 0.25
    }

    override fun i() {
        super.i()
        datawatcher.register(a, false)
    }

    override fun a(blockposition: BlockPosition, block: Block) = a(this.p(), 0.15f, 1.0f)

    override fun J(): MinecraftKey? = LootTables.ao

    override fun F(): SoundEffect? = SoundEffects.gQ

    override fun d(damageSource: DamageSource?): SoundEffect? = SoundEffects.gV

    override fun cf(): SoundEffect? = SoundEffects.gR

    private fun p(): SoundEffect? = SoundEffects.gX

    override fun getMonsterType() = EnumMonsterType.UNDEAD

    override fun n() {
        if (world.D() && !world.isClientSide) {
            val f = aw()
            val blockPosition = if (bJ() is EntityBoat) BlockPosition(locX, locY.roundToInt().toDouble(), locZ).up() else BlockPosition(locX, locY.roundToInt().toDouble(), locZ)
            if (f > 0.5f && random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && world.h(blockPosition)) {
                var flag = true
                val itemStack = getEquipment(EnumItemSlot.HEAD)
                if (!itemStack.isEmpty) {
                    if (itemStack.f()) {
                        itemStack.data = itemStack.i() + random.nextInt(2)
                        if (itemStack.i() >= itemStack.k()) {
                            b(itemStack)
                            setSlot(EnumItemSlot.HEAD, ItemStack.a)
                        }
                    }
                    flag = false
                }
                if (flag) {
                    val event = EntityCombustEvent(getBukkitEntity(), 8)
                    world.server.pluginManager.callEvent(event)
                    if (!event.isCancelled) setOnFire(event.duration)
                }
            }
        }
        super.n()
    }

    override fun aE() {
        super.aE()
        if (bJ() is EntityCreature) aN = (bJ() as EntityCreature).aN
    }

    override fun a(difficultydamagescaler: DifficultyDamageScaler) {
        super.a(difficultydamagescaler)
        setSlot(EnumItemSlot.MAINHAND, ItemStack(Items.BOW))
    }

    override fun prepare(scaler: DifficultyDamageScaler, groupDataEntity: GroupDataEntity?): GroupDataEntity? {
        var entity = groupDataEntity
        entity = super.prepare(scaler, entity)
        this.a(scaler)
        this.b(scaler)
        dm()
        m(random.nextFloat() < 0.55f * scaler.d())
        if (getEquipment(EnumItemSlot.HEAD).isEmpty) {
            val calendar = world.ae()
            if (calendar[2] + 1 == 10 && calendar[5] == 31 && random.nextFloat() < 0.25f) {
                setSlot(EnumItemSlot.HEAD, ItemStack(if (random.nextFloat() < 0.1f) Blocks.LIT_PUMPKIN else Blocks.PUMPKIN))
                dropChanceArmor[EnumItemSlot.HEAD.b()] = 0.0f
            }
        }
        return entity
    }

    override fun die(damageSource: DamageSource) {
        if (damageSource.entity is EntityCreeper) {
            val entityCreeper = damageSource.entity as EntityCreeper
            if (entityCreeper.isPowered && entityCreeper.canCauseHeadDrop()) {
                entityCreeper.setCausedHeadDrop()
                this.a(ItemStack(Items.SKULL, 1, 0), 0.0f)
            }
        }
        super.die(damageSource)
    }

    private fun dm() {
        if (world != null && !world.isClientSide) {
            goalSelector.a(this.c)
            goalSelector.a(this.b)
            val itemStack = this.itemInMainHand
            if (itemStack.item === Items.BOW) {
                var b0: Byte = 20
                if (world.difficulty != EnumDifficulty.HARD) {
                    b0 = 40
                }
                b.c = b0.toInt()
                goalSelector.a(4, this.b)
            } else {
                goalSelector.a(4, this.c)
            }
        }
    }

    private fun a(f: Float): EntityArrow {
        val itemStack = getEquipment(EnumItemSlot.OFFHAND)
        return if (itemStack.item === Items.SPECTRAL_ARROW) {
            val entitySpectralArrow = EntitySpectralArrow(world, this)
            entitySpectralArrow.a(this, f)
            entitySpectralArrow
        } else {
            val entityArrow = EntityTippedArrow(world, this)
            entityArrow.a(this, f)
            if (itemStack.item === Items.TIPPED_ARROW) entityArrow.a(itemStack)
            entityArrow
        }
    }

    override fun a(nbtTagCompound: NBTTagCompound) {
        super.a(nbtTagCompound)
        dm()
    }

    override fun setSlot(enumitemslot: EnumItemSlot, itemstack: ItemStack) {
        super.setSlot(enumitemslot, itemstack)
        if (!world.isClientSide && enumitemslot == EnumItemSlot.MAINHAND) dm()
    }

    override fun getHeadHeight() = 1.74f

    override fun aF() = -0.6
}